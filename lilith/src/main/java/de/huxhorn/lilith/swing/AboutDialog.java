/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2016 Joern Huxhorn
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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.JDialog;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AboutDialog
	extends JDialog
{

	private static final long serialVersionUID = -1777026913356705300L;
	private boolean wasScrolling;
	private AboutPanel aboutPanel;

	public AboutDialog(Frame owner, String title, String appName)
	{
		super(owner, title, false);
		wasScrolling = true;
		setLayout(new BorderLayout());
		InputStream is = MainFrame.class.getResourceAsStream("/about/aboutText.txt");
		String aboutText = null;
		final Logger logger = LoggerFactory.getLogger(AboutDialog.class);
		if(is != null)
		{
			try
			{
				aboutText = IOUtils.toString(is, StandardCharsets.UTF_8);
			}
			catch(IOException e)
			{
				if(logger.isErrorEnabled()) logger.error("Exception while loading aboutText!! *grrr*");
			}
		}
		try
		{
			aboutPanel = new AboutPanel(MainFrame.class.getResource("/about/lilith_big.jpg"), new Rectangle(50, 50, 400, 200), aboutText, MainFrame.class.getResource("/about/lilith.jpg"), appName, 20);
			//aboutPanel.setDebug(true);
			add(aboutPanel, BorderLayout.CENTER);
		}
		catch(IOException e)
		{
			if(logger.isErrorEnabled()) logger.error("Exception creating about panel!!");
		}
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			/**
			 * Invoked when a window is in the process of being closed.
			 * The close operation can be overridden at this point.
			 */
			@Override
			public void windowClosing(WindowEvent e)
			{
				setVisible(false);
			}
		});

	}

	@Override
	public void setVisible(boolean b)
	{
		super.setVisible(b);
		if(b)
		{
			aboutPanel.setScrolling(wasScrolling);
		}
		else
		{
			wasScrolling = aboutPanel.isScrolling();
			aboutPanel.setScrolling(false);
		}
	}
}
