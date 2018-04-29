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

package de.huxhorn.lilith.swing;

import de.huxhorn.sulky.swing.KeyStrokes;
import de.huxhorn.sulky.tasks.Task;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;

public class ProgressGlassPane
	extends JPanel
{
	private static final long serialVersionUID = -7692063970775627702L;

	private final CancelAction cancelAction;
	private final JProgressBar progressBar;
	private final JButton cancelButton;

	ProgressGlassPane()
	{
		super(new GridBagLayout());

		MouseEventEater eater = new MouseEventEater();
		setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		JLabel searchLabel = new JLabel("Searching…");
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
			@Override
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

	void setProgress(int progressValue)
	{
		progressBar.setValue(progressValue);
	}

	CancelAction getFindCancelAction()
	{
		return cancelAction;
	}

	public class CancelAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 3356495903301831775L;
		private Task<Long> task;

		public CancelAction()
		{
			super();
			putValue(Action.NAME, "Cancel");
			putValue(Action.SHORT_DESCRIPTION, "Cancel search.");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ESCAPE);
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

		void cancelSearch()
		{
			Task task = this.task;
			if(task != null)
			{
				task.getFuture().cancel(true);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			cancelSearch();
		}
	}

	private static class MouseEventEater
		implements MouseListener, MouseMotionListener
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			// just eat it
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			// just eat it
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			// just eat it
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			// just eat it
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			// just eat it
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			// just eat it
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			// just eat it
		}
	}
}
