/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.idea;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.awt.Container;
import java.awt.Frame;

public class LilithPlugin
	implements ApplicationComponent
{
	private static final int DEFAULT_PORT = 11111;

	private ServerSocket serverSocket;

	@NotNull
	public String getComponentName()
	{
		return "Lilith";
	}


	public void initComponent()
	{
		try
		{
			serverSocket=new ServerSocket(DEFAULT_PORT);
			ServerSocketRunnable r = new ServerSocketRunnable();
			Thread t=new Thread(r, "Lilith-ServerSocket");
			t.setDaemon(true);
			t.start();
		}
		catch (IOException e)
		{
// TODO: change body of catch statement
			e.printStackTrace();
		}
	}

	public void disposeComponent()
	{
		closeServerSocket();
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
					}
					catch (IOException e)
					{
		// TODO: change body of catch statement
						e.printStackTrace();
						break;
					}
				}
				closeServerSocket();
			}
			System.out.println("Exiting Lilith-ServerSocket-Runnable");
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

	private static class SocketRunnable
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
// TODO: change body of catch statement
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
			}
			catch (IOException e)
			{
// TODO: change body of catch statement
				e.printStackTrace();
				break;
			}
			catch (ClassNotFoundException e)
			{
// TODO: change body of catch statement
				e.printStackTrace();
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
		System.out.println("Exiting Lilith-Socket-Runnable");
	}
}

	public static void showInEditor(StackTraceElement ste)
	{
		if(ste==null)
		{
			return;
		}
		SwingUtilities.invokeLater(new EditorRunnable(ste));
	}

	public static class EditorRunnable
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

//			{
//				StringBuffer msg=new StringBuffer();
//				for(Project p:openProjects)
//				{
//					msg.append(p.getName()).append(", ");
//				}
//				System.out.println(msg.toString());
//			}

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
				PsiClass found = PsiManager.getInstance(current).findClass(className, scope);
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
					found = PsiManager.getInstance(current).findClass(parentClassName, scope);
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
					PsiClass found = PsiManager.getInstance(current).findClass(className, scope);
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
						found = PsiManager.getInstance(current).findClass(parentClassName, scope);
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
			// ok, we found the class...
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
			//System.out.println("PsiElem: "+elem);
			if(elem instanceof PsiClass)
			{
				psiClass= (PsiClass) elem;
				//System.out.println("PsiClass2: "+psiClass);
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
					if(theMethod!=null)
					{
						FileType fileType = psiFile.getFileType();

						if(fileType.isBinary())
						{
							// we don't have source code so just go to method instead of line.
							fileDescriptor=new OpenFileDescriptor(theMethod);
							System.out.println("Using method because file is binary.");
						}
					}

					if(fileDescriptor==null)
					{
						if(lineNumber>=0)
						{
							// go to the line.
							lineNumber--; // I don't get it...
							fileDescriptor=new OpenFileDescriptor(project, vfile, lineNumber, 0);
							System.out.println("Using lineNumber!");
						}
						else if(theMethod!=null)
						{
							// go to the method
							fileDescriptor=new OpenFileDescriptor(theMethod);
							System.out.println("Using method!");
						}
						else
						{
							// just go to the file
							fileDescriptor=new OpenFileDescriptor(project, vfile);
							System.out.println("Using file!");
						}
					}

					FileEditorManager fileEditorManager=FileEditorManager.getInstance(project);
					if(fileEditorManager == null)
					{
						System.out.println("fileEditorManager is null!");
						return;
					}

					Editor editor = fileEditorManager.openTextEditor(fileDescriptor, true);
					//System.out.println("editor: "+editor);
					fileDescriptor.navigate(true);
					if(editor!=null)
					{
						JComponent component = editor.getComponent();

						// now I'll just do my best to focus the correct IDEA frame...
						JFrame frame=resolveFrame(component);
						if(frame!=null)
						{
							if((frame.getState() & Frame.ICONIFIED) != 0)
							{
								frame.setState(Frame.NORMAL);
							}
							frame.toFront();
						}
						component.requestFocusInWindow();
					}
				}
			}
		}

		private JFrame resolveFrame(JComponent component)
		{
			Container c=component.getParent();
			while(c!=null)
			{
				if(c instanceof JFrame)
				{
					return (JFrame) c;
				}
				c=c.getParent();
			}
			return null;
		}
	}

}
