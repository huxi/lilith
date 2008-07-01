/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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

import javax.swing.*;
import java.awt.Frame;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.concurrent.Future;

public class ProgressDialog
	extends JDialog
{
	private Future<Integer> future;

	public ProgressDialog(Frame owner)
	{
		super(owner, false);
		JProgressBar progress = new JProgressBar();
		progress.setIndeterminate(true);
		Container c=getContentPane();
		c.setLayout(new BorderLayout());
		c.add(progress, BorderLayout.CENTER);
		CancelAction cancelAction=new CancelAction();
		JButton cancelButton = new JButton(cancelAction);
		c.add(cancelButton, BorderLayout.SOUTH);
	}

	public void setFuture(Future<Integer> future)
	{
		this.future=future;
	}


	private class CancelAction
		extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel");
		}

		public void actionPerformed(ActionEvent e)
		{
			future.cancel(true);
			setVisible(false);
		}
	}
}
