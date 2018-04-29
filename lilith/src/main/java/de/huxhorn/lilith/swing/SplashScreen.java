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

package de.huxhorn.lilith.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

public class SplashScreen
	extends JWindow
{
	private static final long serialVersionUID = 668541832046187990L;

	private final JPanel contentPane;
	private final JLabel statusLabel;

	public SplashScreen(String applicationTitle)
	{
		super();

		contentPane = new JPanel(true);
		contentPane.setLayout(new BorderLayout());
		URL url = SplashScreen.class.getResource("/splash/splash.jpg");
		if(url != null)
		{
			try
			{
				BufferedImage image = ImageIO.read(url);
				if(image != null)
				{
					ImagePanel imagePanel = new ImagePanel(image);
					contentPane.add(imagePanel, BorderLayout.CENTER);
				}
			}
			catch(IOException ex)
			{
				ex.printStackTrace(); // NOPMD
			}
		}
		statusLabel = new JLabel();
		statusLabel.setOpaque(true);
		statusLabel.setForeground(Color.BLACK);
		statusLabel.setBackground(Color.WHITE);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel titleLabel = new JLabel(applicationTitle);
		titleLabel.setOpaque(true);
		titleLabel.setForeground(Color.BLACK);
		titleLabel.setBackground(Color.WHITE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		contentPane.add(titleLabel, BorderLayout.NORTH);
		contentPane.add(statusLabel, BorderLayout.SOUTH);
		statusLabel.setText("Initializing…");
		setContentPane(contentPane);
	}

	public void setStatusText(final String statusText)
	{
		statusLabel.setText(statusText);
		if(!isVisible())
		{
			setVisible(true);
		}
		toFront();
		Rectangle bounds = contentPane.getBounds();
		int height=statusLabel.getHeight();
		contentPane.paintImmediately(0,bounds.height-height,bounds.width, height);
	}

	private class ImagePanel
		extends JComponent
	{
		private static final long serialVersionUID = 1400735425931232883L;
		private final BufferedImage image;

		ImagePanel(BufferedImage image)
		{
			this.image = image;
			this.setPreferredSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
		}

		@Override
		public void paint(Graphics g)
		{
			g.drawImage(image, 0, 0, this);
		}

		@Override
		public void update(Graphics g)
		{
			paint(g);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			paint(g);
		}

		@Override
		public boolean imageUpdate(Image img, int infoFlags, int x, int y, int w, int h)
		{
			repaint();
			return (infoFlags & (ALLBITS | ABORT)) == 0;
		}
	}
}
