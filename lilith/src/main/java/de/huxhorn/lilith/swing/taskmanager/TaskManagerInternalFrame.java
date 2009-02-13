package de.huxhorn.lilith.swing.taskmanager;

import de.huxhorn.lilith.swing.MainFrame;

import java.awt.*;

import javax.swing.*;

public class TaskManagerInternalFrame
	extends JInternalFrame
{
	private MainFrame mainFrame;
	private TaskManagerPanel<Long> taskManagerPanel;

	public TaskManagerInternalFrame(MainFrame mainFrame)
	{
		super("Task Manager", true, true, true, true);
		this.mainFrame = mainFrame;
		taskManagerPanel = new TaskManagerPanel<Long>(mainFrame.getLongWorkManager());
		setLayout(new GridLayout(1, 1));
		add(taskManagerPanel);
	}

	@Override
	public void setVisible(boolean visible)
	{
		if(taskManagerPanel != null)
		{
			taskManagerPanel.setPaused(!visible);
		}
		super.setVisible(visible);
	}
}
