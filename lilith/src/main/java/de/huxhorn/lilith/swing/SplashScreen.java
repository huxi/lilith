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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JWindow;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.IOException;

public class SplashScreen
	extends JWindow
{
	private final Logger logger = LoggerFactory.getLogger(SplashScreen.class);

	private JLabel statusLabel;

	public SplashScreen(String applicationTitle)
	{
		super();
		initUI(applicationTitle);
	}


	private void initUI(String applicationTitle)
	{
		Container contentPane=new JPanel(true);
		contentPane.setLayout(new BorderLayout());
		URL url=SplashScreen.class.getResource("/splash/splash.jpg");
		if(url!=null)
		{
			try
			{
				BufferedImage image = ImageIO.read(url);
				if(image!=null)
				{
					ImagePanel imagePanel = new ImagePanel(image);
					contentPane.add(imagePanel, BorderLayout.CENTER);
				}
			}
			catch (IOException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while loading image!",ex);
			}
		}
		statusLabel=new JLabel();
		JLabel titleLabel = new JLabel(applicationTitle);
		contentPane.add(titleLabel,BorderLayout.NORTH);
		contentPane.add(statusLabel,BorderLayout.SOUTH);
		statusLabel.setText("Initializing...");
		setContentPane(contentPane);
	}

	public void setStatusText(final String statusText)
	{
		statusLabel.setText(statusText);
		if(logger.isInfoEnabled()) logger.info("Status: {}", statusText);
		if(!isVisible())
		{
			setVisible(true);
		}
		toFront();
		repaint();
	}

	private class ImagePanel extends JComponent
	{
		private BufferedImage image;

		public ImagePanel(BufferedImage image)
		{
			this.image = image;
			this.setPreferredSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
		}

		public void paint(Graphics g)
		{
			if(logger.isInfoEnabled()) logger.info("paint");
			g.drawImage(image, 0,0,this);
		}

		public void update(Graphics g)
		{
			if(logger.isInfoEnabled()) logger.info("update");
			paint(g);
		}

		protected void paintComponent(Graphics g)
		{
			paint(g);
		}

		public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h)
		{
			if(logger.isInfoEnabled()) logger.info("imageUpdate");
			repaint();
			return (infoflags & (ALLBITS|ABORT)) == 0;
		}
	}
}
