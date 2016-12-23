/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright 2007-2011 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.idea;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

/**
 *
 * http://www.jetbrains.net/confluence/display/IDEADEV/Diana+Plugin+Migration+Guide
 *
 */
public class LilithPlugin
	implements ApplicationComponent
{
	private static final int DEFAULT_PORT = 11111;

	private Method getInstanceMethod;
	private Method findClassMethod;

	private ServerSocket serverSocket;
	private Set<Thread> receiverThreads;

	@NotNull
	public String getComponentName()
	{
		return "Lilith";
	}


	public void initComponent()
	{
		System.out.println("Starting initComponent...");
		Class<?> clazz=null;
		try
		{
			clazz = Class.forName("com.intellij.psi.JavaPsiFacade");
		}
		catch (ClassNotFoundException e)
		{
			try
			{
				clazz = Class.forName("com.intellij.psi.PsiManager");
			}
			catch (ClassNotFoundException e1)
			{
				e1.printStackTrace();
			}
		}
		Method instanceMethod = null;
		Method classMethod = null;
		if(clazz!=null)
		{
			try
			{
				instanceMethod = clazz.getMethod("getInstance", Project.class);
				classMethod = clazz.getMethod("findClass", String.class, GlobalSearchScope.class);
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
		}
		getInstanceMethod = instanceMethod;
		findClassMethod = classMethod;
		receiverThreads=new HashSet<Thread>();
		try
		{
			serverSocket=new ServerSocket(DEFAULT_PORT, 50, InetAddress.getByName("127.0.0.1"));
			ServerSocketRunnable r = new ServerSocketRunnable();
			Thread t=new Thread(r, "Lilith-ServerSocket");
			t.setDaemon(true);
			t.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Finished initComponent...");
	}

	public void disposeComponent()
	{
		System.out.println("Starting disposeComponent...");
		closeServerSocket();
		HashSet<Thread> threads = new HashSet<Thread>(receiverThreads);
		for(Thread t:threads)
		{
			t.interrupt();
		}
		System.out.println("Finished disposeComponent...");
	}


	class ServerSocketRunnable
		implements Runnable
	{
		public void run()
		{
			System.out.println("Started Lilith-ServerSocket-Runnable");
			ServerSocket ss = serverSocket;
			if(ss!=null)
			{
				for(;;)
				{
					try
					{
						Socket socket = ss.accept();
						Thread t=new Thread(new SocketRunnable(socket), "Lilith-Socket-"+socket);
						t.setDaemon(true);
						t.start();
						receiverThreads.add(t);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						break;
					}
				}
				closeServerSocket();
			}
			System.out.println("Finished Lilith-ServerSocket-Runnable");
		}
	}

	private void closeServerSocket()
	{
		if(serverSocket!=null)
		{
			try
			{
				serverSocket.close();
			}
			catch (IOException e)
			{
				// ignore
			}
			serverSocket=null;
		}
	}

	private class SocketRunnable
		implements Runnable
	{
		private Socket socket;

		public SocketRunnable(Socket socket)
		{
			this.socket=socket;
		}

		public void run()
		{
			System.out.println("Started Lilith-Socket-Runnable");
			ObjectInputStream ois;
			try
			{
				ois=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
				return;
			}

			for(;;)
			{
				try
				{
					Object obj=ois.readObject();
					if(obj instanceof StackTraceElement)
					{
						showInEditor((StackTraceElement) obj);
					}
					Thread.sleep(1);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					break;
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					break;
				}
				catch (InterruptedException e)
				{
					break;
				}
			}
			try
			{
				ois.close();
			}
			catch (IOException e)
			{
				//ignore
			}
			receiverThreads.remove(Thread.currentThread());
			System.out.println("Finished Lilith-Socket-Runnable");
		}
	}

	public void showInEditor(StackTraceElement ste)
	{
		if(ste==null)
		{
			return;
		}
		SwingUtilities.invokeLater(new EditorRunnable(ste));
	}

	public class EditorRunnable
		implements Runnable
	{
		private StackTraceElement stackTraceElement;

		public EditorRunnable(StackTraceElement stackTraceElement)
		{
			this.stackTraceElement=stackTraceElement;
		}

		public void run()
		{
			System.out.println(stackTraceElement);
			ProjectManager projectManager = ProjectManager.getInstance();
			Project[] openProjects = projectManager.getOpenProjects();

			String className=stackTraceElement.getClassName();
			String parentClassName=null;
			int dollarIndex = className.indexOf("$");
			if(dollarIndex>=0)
			{
				parentClassName=className.substring(0, dollarIndex);
				className=className.replace('$','.');
			}

			String methodName=stackTraceElement.getMethodName();
			int lineNumber=stackTraceElement.getLineNumber();
			Project project = null;
			PsiClass psiClass = null;
			PsiClass psiSourceClass = null;
			PsiClass psiParentClass = null;
			PsiClass psiParentSourceClass = null;
			for(Project current:openProjects)
			{
				GlobalSearchScope scope = GlobalSearchScope.projectScope(current);
				PsiClass found = findClass(current, className, scope);
				if(found!=null)
				{
					project=current;
					psiClass=found;
					if(psiClass.canNavigateToSource())
					{
						// don't search anympore if source class was found.
						psiSourceClass=psiClass;
						break;
					}
				}
				// we only care about parent class if class was not found.
				if(parentClassName != null && psiClass==null && psiParentClass==null)
				{
					// search for parent as fallback...
					found = findClass(current, parentClassName, scope);
					if(found!=null)
					{
						project=current;
						psiParentClass=found;
						if(psiParentClass.canNavigateToSource())
						{
							psiParentSourceClass=psiParentClass;
							// no break here because we might still find the direct class
						}
					}
				}
			}
			if(psiSourceClass == null)
			{
				// class not found yet, search all instead of project
				for(Project current:openProjects)
				{
					GlobalSearchScope scope = GlobalSearchScope.allScope(current);
					PsiClass found = findClass(current, className, scope);
					if(found!=null)
					{
						project=current;
						psiClass=found;
						if(psiClass.canNavigateToSource())
						{
							psiSourceClass=psiClass;
							// don't search anympore if source class was found.
							break;
						}
					}

					// we only care about parent class if class was not found.
					if(parentClassName != null && psiClass==null && psiParentClass==null)
					{
						// search for parent as fallback...
						found = findClass(current, parentClassName, scope);
						if(found!=null)
						{
							project=current;
							psiParentClass=found;
							if(psiParentClass.canNavigateToSource())
							{
								psiParentSourceClass=psiParentClass;
								// no break here because we might still find the direct class
							}
						}
					}
				}
			}

			if(project==null)
			{
				System.out.println("Couldn't find project...");
				return;
			}
			System.out.println("Project: "+project.getName());
			System.out.println("PsiClass: "+psiClass);
			System.out.println("PsiSourceClass: "+psiSourceClass);
			System.out.println("PsiParentClass: "+psiParentClass);
			System.out.println("PsiParentSourceClass: "+psiParentSourceClass);

			if(psiClass==null)
			{
				psiClass=psiParentClass;
				psiSourceClass=psiParentSourceClass;
			}
			PsiElement elem;
			if(psiSourceClass!=null)
			{
				elem=psiSourceClass.getNavigationElement();
			}
			else
			{
				elem=psiClass.getNavigationElement();
			}

			if(elem instanceof PsiClass)
			{
				psiClass = (PsiClass) elem;
			}
			PsiFile psiFile = psiClass.getContainingFile();

			System.out.println("PsiFile: "+psiFile);

			if(psiFile!=null)
			{
				VirtualFile vfile = psiFile.getVirtualFile();
				if(vfile!=null)
				{
					OpenFileDescriptor fileDescriptor=null;
					PsiMethod theMethod=null;
					PsiMethod[] methods = psiClass.getMethods();
					for(PsiMethod method: methods)
					{
						if(method.getName().equals(methodName))
						{
							theMethod=method;
							break;
						}
					}

					FileType fileType = psiFile.getFileType();

					if(lineNumber>=0 && !fileType.isBinary())
					{
						// go to the line.
						lineNumber--; // I don't get it...
						fileDescriptor=new OpenFileDescriptor(project, vfile, lineNumber, 0);
						System.out.println("Using lineNumber!");
					}
					else if(theMethod==null)
					{
						// just go to the file
						fileDescriptor=new OpenFileDescriptor(project, vfile);
						System.out.println("Using file!");
					}

					if(fileDescriptor!=null)
					{
						FileEditorManager fileEditorManager=FileEditorManager.getInstance(project);
						if(fileEditorManager == null)
						{
							System.out.println("fileEditorManager is null!");
							return;
						}

						fileDescriptor.navigate(true);
					}
					else
					{
						theMethod.navigate(true);
					}
					WindowManager.getInstance().suggestParentWindow(project).toFront();
				}
			}
		}

	}

	private PsiClass findClass(Project project, String className, GlobalSearchScope scope)
	{
		try
		{
			Object instance = getInstanceMethod.invoke(null, project);
			if(instance != null)
			{
				return (PsiClass) findClassMethod.invoke(instance, className, scope);
			}
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		// IDEA 7: PsiManager.getInstance(project).findClass(className, scope);
		// IDEA 8: JavaPsiFacade.getInstance(project).findClass(className, scope);
		System.err.println("Couldn't find class '"+className+"'!");
		return null;
	}
}
