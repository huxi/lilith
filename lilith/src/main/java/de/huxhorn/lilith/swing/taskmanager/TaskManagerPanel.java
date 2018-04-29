/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.swing.taskmanager;

import de.huxhorn.lilith.swing.Icons;
import de.huxhorn.lilith.swing.taskmanager.table.TaskTable;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskManager;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManagerPanel<T>
	extends JPanel
{
	private static final long serialVersionUID = 4514870616761988724L;

	private final Logger logger = LoggerFactory.getLogger(TaskManagerPanel.class);

	private final CancelTaskAction cancelAction;
	private final TaskTable<T> table;
	private final JPopupMenu popup;
	private final JTextArea details;

	public TaskManagerPanel(TaskManager<T> taskManager)
	{
		setLayout(new BorderLayout());
		table = new TaskTable<>(taskManager);
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new TaskSelectionListener());

		table.addMouseListener(new TaskTableMouseListener());

		cancelAction = new CancelTaskAction();

		JToolBar toolBar = new JToolBar();
		toolBar.add(cancelAction);
		toolBar.setFloatable(false);

		details = new JTextArea();
		details.setLineWrap(true);
		details.setWrapStyleWord(true);
		details.setEditable(false);

		JScrollPane tableScrollPane = new JScrollPane(table);
		JScrollPane detailsScrollPane = new JScrollPane(details, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailsScrollPane);

		splitPane.setResizeWeight(0.5); // divide space equally in case of resize.
		splitPane.setOneTouchExpandable(true);

		add(toolBar, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);

		popup = new JPopupMenu();
		popup.add(cancelAction);
	}

	public void setPaused(boolean paused)
	{
		table.setPaused(paused);
	}

	private class TaskSelectionListener
		implements ListSelectionListener
	{

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if(e.getValueIsAdjusting()) return;

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if(lsm.isSelectionEmpty())
			{
				setSelectedTask(null);
			}
			else
			{
				int selectedRow = lsm.getMinSelectionIndex();

				selectedRow = table.convertRowIndexToModel(selectedRow);
				Task<T> task = table.getTaskTableModel().getValueAt(selectedRow);
				setSelectedTask(task);
			}
		}
	}

	private void setSelectedTask(Task<T> task)
	{
		if(logger.isDebugEnabled()) logger.debug("Selected task {}.", task);
		cancelAction.setTask(task);
		if(task != null && task.getDescription() != null)
		{
			details.setText(task.getDescription());
			details.setCaretPosition(0);
		}
		else
		{
			details.setText("");
		}
	}

	class CancelTaskAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -2004455657443480819L;

		private Task task;

		CancelTaskAction()
		{
			super("Cancel task");
			putValue(Action.SMALL_ICON, Icons.STOP_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Cancels the selected task.");
			//putValue(Action.MNEMONIC_KEY, Integer.valueOf('c'));
			setEnabled(false);
		}

		public Task getTask()
		{
			return task;
		}

		public void setTask(Task task)
		{
			this.task = task;
			setEnabled(this.task != null);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(task != null)
			{
				if(logger.isInfoEnabled()) logger.info("Cancel task {}.", task);
				task.getFuture().cancel(true);
			}
		}
	}

	private class TaskTableMouseListener
		implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
			else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1)
			{
				cancelAction.actionPerformed(null);
			}
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			Point p = e.getPoint();
			table.getTaskAt(p, true); // selects the clicked task...
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				showPopup(e.getPoint());
			}
		}

		private void showPopup(Point p)
		{
			popup.show(table, p.x, p.y);
		}
	}
}
