/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.lilith.swing;

import de.huxhorn.sulky.swing.KeyStrokes;
import de.huxhorn.sulky.tasks.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

public class ProgressGlassPane
	extends JPanel
{
	private final Logger logger = LoggerFactory.getLogger(ProgressGlassPane.class);

	private CancelAction cancelAction;
	//private JPanel searchingPanel;
	private JProgressBar progressBar;
	private JButton cancelButton;

	public ProgressGlassPane()
	{
		super(new GridBagLayout());

		MouseEventEater eater = new MouseEventEater();
		setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		JLabel searchLabel = new JLabel("Searching...");
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(searchLabel, gbc);


		progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		progressBar.setStringPainted(true); // percent

		gbc.gridy = 1;
		add(progressBar, gbc);

		cancelAction = new CancelAction();
		cancelButton = new JButton(cancelAction);
		gbc.gridy = 2;
		add(cancelButton, gbc);

		addKeyListener(new KeyAdapter()
		{
		});

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent e)
			{
				cancelButton.requestFocusInWindow();
				//requestFocusInternal();
			}
		});
		setFocusTraversalKeysEnabled(false);
		KeyStrokes.registerCommand(this, cancelAction, "CANCEL_ACTION");
		addMouseListener(eater);
		addMouseMotionListener(eater);
	}

	/*
	void requestFocusInternal()
	{
		if(searching)
		{
			cancelButton.requestFocusInWindow();
		}
		else
		{
			requestFocusInWindow();
		}
	}
    */
	public JInternalFrame resolveInternalFrame()
	{
		Container parent = getParent();
		while(parent != null && !(parent instanceof JInternalFrame))
		{
			parent = parent.getParent();
		}
		return (JInternalFrame) parent;
	}

	/*
	public boolean isSearching()
	{
		return searching;
	}

	public void setSearching(boolean searching)
	{
		if(this.searching != searching)
		{
			this.searching = searching;
			if(this.searching)
			{
				addMouseListener(eater);
				addMouseMotionListener(eater);
			}
			else
			{
				removeMouseListener(eater);
				removeMouseMotionListener(eater);
			}
		}
	}
	*/
	@Override
	protected void paintComponent(Graphics g)
	{
		Rectangle clip = g.getClipBounds();
		Graphics2D g2 = (Graphics2D) g.create();
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f);
		g2.setComposite(alpha);
		g2.setColor(Color.WHITE);
		g2.fillRect(clip.x, clip.y, clip.width, clip.height);
		g2.dispose();
		paintComponents(g);
	}

	public void setProgress(int progressValue)
	{
		progressBar.setValue(progressValue);
	}

	public CancelAction getFindCancelAction()
	{
		return cancelAction;
	}

	public class CancelAction
		extends AbstractAction
	{
		private Task<Long> task;

		public CancelAction()
		{
			super();
			/*
			Icon icon;
			{
				URL url = ProgressGlassPane.class.getResource("/tango/16x16/actions/process-stop.png");
				if (url != null)
				{
					icon = new ImageIcon(url);
				}
				else
				{
					icon = null;
				}
			}
			*/
			putValue(Action.NAME, "Cancel");
			putValue(Action.SHORT_DESCRIPTION, "Cancel search.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			setTask(null);
		}

		public Task<Long> getTask()
		{
			return task;
		}

		public void setTask(Task<Long> task)
		{
			this.task = task;
			setEnabled((this.task != null));
		}

		public void cancelSearch()
		{
			Task task = this.task;
			if(task != null)
			{
				task.getFuture().cancel(true);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			cancelSearch();
		}
	}

//	@Override
//	public void setVisible(boolean visible)
//	{
//		super.setVisible(visible);
//		if(logger.isInfoEnabled()) logger.info("Visible!", new Throwable());

	//	}

	private static class MouseEventEater
		implements MouseListener, MouseMotionListener
	{

		public void mouseClicked(MouseEvent e)
		{
		}

		public void mousePressed(MouseEvent e)
		{
		}

		public void mouseReleased(MouseEvent e)
		{
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

		public void mouseDragged(MouseEvent e)
		{
		}

		public void mouseMoved(MouseEvent e)
		{
		}
	}
}
