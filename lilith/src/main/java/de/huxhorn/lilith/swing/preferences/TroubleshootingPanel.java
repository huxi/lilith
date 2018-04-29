/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.huxhorn.lilith.swing.preferences;

import de.huxhorn.lilith.swing.LilithActionId;
import de.huxhorn.lilith.swing.MainFrame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"PMD.MethodReturnsInternalArray", "PMD.ArrayIsStoredDirectly"})
public class TroubleshootingPanel
	extends JPanel
{
	private static final long serialVersionUID = 5589305263321629687L;
	private static final String LILITH_LOGS_PLACEHOLDER = "##LilithLogsPlaceholder##";
	private static final String WINDOW_MENU_PLACEHOLDER = "##WindowMenuPlaceholder##";

	private final Logger logger = LoggerFactory.getLogger(TroubleshootingPanel.class);
	private final PreferencesDialog preferencesDialog;

	TroubleshootingPanel(PreferencesDialog preferencesDialog)
	{
		this.preferencesDialog = preferencesDialog;

		setLayout(new GridBagLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(new JButton(new InitDetailsViewAction()));
		buttonPanel.add(new JButton(new InitExampleConditionScriptsAction()));
		buttonPanel.add(new JButton(new InitExampleClipboardFormatterScriptsAction()));
		buttonPanel.add(new JButton(new DeleteAllLogsAction()));
		buttonPanel.add(new JButton(new CopySystemPropertiesAction()));
		buttonPanel.add(new JButton(new CopyThreadsAction()));
		buttonPanel.add(new JButton(new GarbageCollectionAction()));

		JPanel messagePanel = new JPanel(new GridLayout(1,1));
		JLabel messageField = new JLabel();
		messageField.setText(resolveMessage());
		messagePanel.add(messageField);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTH;

		add(buttonPanel, gbc);

		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		messagePanel.setBorder(new EmptyBorder(10,10,10,10));
		add(messagePanel, gbc);
	}

	static String resolveMessage()
	{
		final Logger logger = LoggerFactory.getLogger(TroubleshootingPanel.class);

		try
		{
			InputStream messageStream = TroubleshootingPanel.class.getResourceAsStream("/dependencies.message");
			if(messageStream == null)
			{
				if(logger.isErrorEnabled()) logger.error("Failed to get resource dependencies.message!");
			}
			else
			{
				String message = IOUtils.toString(messageStream, StandardCharsets.UTF_8);
				message = message.replace(WINDOW_MENU_PLACEHOLDER, LilithActionId.WINDOW.getText());
				message = message.replace(LILITH_LOGS_PLACEHOLDER, LilithActionId.VIEW_LILITH_LOGS.getText());
				return message;
			}
		}
		catch (IOException e)
		{
			if(logger.isErrorEnabled()) logger.error("Failed to load dependencies.message!", e);
		}
		return null;
	}

	public class InitDetailsViewAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 8374235720899930441L;

		InitDetailsViewAction()
		{
			super("Reinitialize details view files.");
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			preferencesDialog.reinitializeDetailsViewFiles();
		}
	}

	public class InitExampleConditionScriptsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4197531497673863904L;

		InitExampleConditionScriptsAction()
		{
			super("Reinitialize example groovy conditions.");
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			preferencesDialog.reinitializeGroovyConditions();
		}
	}

	public class InitExampleClipboardFormatterScriptsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4197531497673863904L;

		InitExampleClipboardFormatterScriptsAction()
		{
			super("Reinitialize example groovy clipboard formatters.");
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			preferencesDialog.reinitializeGroovyClipboardFormatters();
		}
	}

	public class DeleteAllLogsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 5218712842261152334L;

		DeleteAllLogsAction()
		{
			super("Delete *all* logs.");
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			preferencesDialog.deleteAllLogs();
		}
	}

	public class CopySystemPropertiesAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -2375370123070284280L;

		CopySystemPropertiesAction()
		{
			super("Copy properties");
			putValue(SHORT_DESCRIPTION, "Copy system properties to the clipboard.");
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			Properties props = System.getProperties();
			SortedMap<String, String> sortedProps = new TreeMap<>();
			Enumeration<?> keys = props.propertyNames();
			while(keys.hasMoreElements())
			{
				String current = (String) keys.nextElement();
				String value = props.getProperty(current);
				if("line.separator".equals(current))
				{
					value = value.replace("\n", "\\n");
					value = value.replace("\r", "\\r");
				}
				sortedProps.put(current, value);

			}
			StringBuilder builder = new StringBuilder();
			for(Map.Entry<String, String> current : sortedProps.entrySet())
			{
				builder.append(current.getKey())
						.append('=')
						.append(current.getValue())
						.append('\n');
			}
			MainFrame.copyText(builder.toString());
		}
	}

	public class CopyThreadsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -2375370123070284280L;

		CopyThreadsAction()
		{
			super("Copy threads");
			putValue(SHORT_DESCRIPTION, "Copy the stacktraces of all threads to the clipboard.");
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();

			Map<ThreadGroup, List<ThreadHolder>> threadGroupMapping = new Hashtable<>();
			List<ThreadHolder> nullList = new ArrayList<>();
			for(Map.Entry<Thread, StackTraceElement[]> current : allStackTraces.entrySet())
			{
				Thread key = current.getKey();
				StackTraceElement[] value = current.getValue();
				ThreadHolder holder = new ThreadHolder(key, value); // NOPMD - AvoidInstantiatingObjectsInLoops
				ThreadGroup group = key.getThreadGroup();
				if(group == null)
				{
					nullList.add(holder);
				}
				else
				{
					List<ThreadHolder> list = threadGroupMapping.get(group);
					if(list == null)
					{
						list = new ArrayList<>(); // NOPMD - AvoidInstantiatingObjectsInLoops
						threadGroupMapping.put(group, list);
					}
					list.add(holder);
				}

			}

			ThreadGroup rootGroup = null;
			Map<ThreadGroup, List<ThreadGroup>> threadGroups = new Hashtable<>();

			for(Map.Entry<ThreadGroup, List<ThreadHolder>> current : threadGroupMapping.entrySet())
			{
				ThreadGroup key = current.getKey();

				ThreadGroup root = addGroup(key, threadGroups);

				if(rootGroup == null)
				{
					rootGroup = root;
				}
				else if(rootGroup != root) // NOPMD
				{
					if(logger.isErrorEnabled()) logger.error("root={}, rootGroup={}", root, rootGroup); // NOPMD
				}
			}

			if(rootGroup == null)
			{
				if(logger.isErrorEnabled()) logger.error("Couldn't resolve root ThreadGroup!"); // NOPMD
				return;
			}

			StringBuilder builder = new StringBuilder();
			appendGroup(0, builder, rootGroup, threadGroups, threadGroupMapping);

			if(!nullList.isEmpty())
			{
				builder.append("no group:\n");
				for(ThreadHolder current : nullList)
				{
					appendThread(1, builder, current);
				}
			}

			MainFrame.copyText(builder.toString());
		}

		private void appendGroup(int indent, StringBuilder builder, ThreadGroup group, Map<ThreadGroup, List<ThreadGroup>> threadGroups, Map<ThreadGroup, List<ThreadHolder>> threadGroupMapping)
		{
			String indentStr = createIndent(indent);
			builder.append(indentStr).append("ThreadGroup[name='").append(group.getName()).append("'" + ", daemon=")
				.append(group.isDaemon()).append(", destroyed=").append(group.isDestroyed()).append(", maxPriority=")
				.append(group.getMaxPriority()).append("]\n");

			List<ThreadGroup> groups = threadGroups.get(group);
			if(groups != null && !groups.isEmpty())
			{
				builder.append(indentStr).append("groups = {\n");
				groups.sort(ThreadGroupComparator.INSTANCE);
				for(ThreadGroup current : groups)
				{
					appendGroup(indent + 1, builder, current, threadGroups, threadGroupMapping);
				}

				builder.append(indentStr).append("}\n");
			}

			List<ThreadHolder> threads = threadGroupMapping.get(group);
			if(threads != null && !threads.isEmpty())
			{
				builder.append(indentStr).append("threads = {\n");
				Collections.sort(threads);
				for(ThreadHolder current : threads)
				{
					appendThread(indent + 1, builder, current);
				}
				builder.append(indentStr).append("}\n");
			}
		}

		private void appendThread(int indent, StringBuilder builder, ThreadHolder threadHolder)
		{
			String indentStr = createIndent(indent);

			Thread t = threadHolder.getThread();
			StackTraceElement[] ste = threadHolder.getStackTraceElements();
			builder.append(indentStr).append("Thread[name=").append(t.getName()).append(", id=").append(t.getId())
				.append(", priority=").append(t.getPriority()).append(", state=").append(t.getState())
				.append(", daemon=").append(t.isDaemon()).append(", alive=").append(t.isAlive())
				.append(", interrupted=").append(t.isInterrupted()).append("]\n");
			appendStackTraceElements(indent + 1, builder, ste);
		}

		private void appendStackTraceElements(int indent, StringBuilder builder, StackTraceElement[] stackTraceElements)
		{
			String indentStr = createIndent(indent);

			for(StackTraceElement current : stackTraceElements)
			{
				builder.append(indentStr).append("at ").append(current).append('\n');
			}
		}

		private String createIndent(int indent)
		{
			StringBuilder result = new StringBuilder();
			for(int i = 0; i < indent; i++)
			{
				result.append('\t');
			}
			return result.toString();
		}

		private ThreadGroup addGroup(ThreadGroup group, Map<ThreadGroup, List<ThreadGroup>> threadGroups)
		{
			ThreadGroup parentGroup = group.getParent();
			if(parentGroup == null)
			{
				return group; // root
			}
			List<ThreadGroup> list = threadGroups.get(parentGroup);
			if(list == null)
			{
				list = new ArrayList<>();
				threadGroups.put(parentGroup, list);
			}
			if(!list.contains(group))
			{
				list.add(group);
			}
			return addGroup(parentGroup, threadGroups);
		}
	}

	private static class ThreadGroupComparator
		implements Comparator<ThreadGroup>
	{
		static final Comparator<ThreadGroup> INSTANCE = new ThreadGroupComparator();

		@Override
		public int compare(ThreadGroup o1, ThreadGroup o2)
		{
			if(o1 == o2) // NOPMD
			{
				return 0;
			}
			if(o1 == null)
			{
				return -1;
			}
			if(o2 == null)
			{
				return 1;
			}
			String name = o1.getName();
			String otherName = o2.getName();
			//noinspection StringEquality
			if(name == otherName) // NOPMD
			{
				return 0;
			}
			if(name == null)
			{
				return -1;
			}
			if(otherName == null)
			{
				return 1;
			}
			return name.compareTo(otherName);
		}
	}

	private static class ThreadHolder
		implements Comparable<ThreadHolder>
	{
		private final Thread thread;
		private final StackTraceElement[] stackTraceElements;

		ThreadHolder(Thread thread, StackTraceElement[] stackTraceElements)
		{
			this.thread = thread;
			this.stackTraceElements = stackTraceElements;
		}

		public Thread getThread()
		{
			return thread;
		}

		StackTraceElement[] getStackTraceElements()
		{
			return stackTraceElements;
		}

		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;

			ThreadHolder that = (ThreadHolder) o;

			return !(thread != null ? !thread.equals(that.thread) : that.thread != null);

		}

		@Override
		public int hashCode()
		{
			return (thread != null ? thread.hashCode() : 0);
		}

		@Override
		@SuppressWarnings("NullableProblems")
		public int compareTo(ThreadHolder other)
		{
			Objects.requireNonNull(other, "other must not be null!");
			if(thread == other.thread)
			{
				return 0;
			}
			if(thread == null)
			{
				return -1;
			}
			if(other.thread == null)
			{
				return 1;
			}
			String name = thread.getName();
			String otherName = other.thread.getName();
			//noinspection StringEquality
			if(name == otherName) // NOPMD
			{
				return 0;
			}
			// thread name is never null
			return name.compareTo(otherName);
		}
	}

	public class GarbageCollectionAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4636919088257143096L;

		GarbageCollectionAction()
		{
			super("Execute GC");
			putValue(SHORT_DESCRIPTION, "Execute garbage collection.");
		}

		@Override
		@SuppressWarnings("PMD.DoNotCallGarbageCollectionExplicitly")
		public void actionPerformed(ActionEvent actionEvent)
		{
			System.gc();
		}
	}

}
