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

import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.LinkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.net.URL;
import java.util.List;

import de.huxhorn.lilith.swing.linklistener.OpenUrlLinkListener;

public class HelpFrame
	extends JFrame
{
	private final Logger logger = LoggerFactory.getLogger(HelpFrame.class);

	private XHTMLPanel helpPane;
	private XhtmlNamespaceHandler xhtmlNamespaceHandler;
	private MainFrame mainFrame;
	//private JTextPane helpPane;


	public HelpFrame(MainFrame mainFrame) throws HeadlessException
	{
		super();
		this.mainFrame=mainFrame;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		initUI();
	}

	private void initUI()
	{
		/*
		helpPane = new JTextPane();
		HTMLEditorKit htmlEditorKit=new HTMLEditorKit();
		helpPane.setEditorKit(htmlEditorKit);
		helpPane.setEditable(false);
		JScrollPane helpScrollPane = new JScrollPane(helpPane);
		*/
		helpPane=new XHTMLPanel();

		{
			LinkListener originalLinkListener = null;
			List mouseTrackingList = helpPane.getMouseTrackingListeners();
			if(mouseTrackingList!=null)
			{
				for(Object o: mouseTrackingList)
				{
					if(logger.isDebugEnabled()) logger.debug("Before MTL {}",o);
					if(o instanceof LinkListener)
					{
						helpPane.removeMouseTrackingListener((LinkListener) o);
						originalLinkListener= (LinkListener) o;
					}
				}
			}
			helpPane.addMouseTrackingListener(new OpenUrlLinkListener(mainFrame, originalLinkListener));
		}



		xhtmlNamespaceHandler=new XhtmlNamespaceHandler();
		FSScrollPane helpScrollPane = new FSScrollPane(helpPane);

		setContentPane(helpScrollPane);
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle maxBounds = ge.getMaximumWindowBounds();
		setSize(maxBounds.width/2, maxBounds.height/2);
		{
			URL url=HelpFrame.class.getResource("/tango/16x16/apps/help-browser.png");

			if(url!=null)
			{
				ImageIcon icon = new ImageIcon(url);
				setIconImage(icon.getImage());
			}
		}

	}

	public void setHelpUrl(URL helpUrl)
	{
		helpPane.setDocument(helpUrl.toExternalForm(), xhtmlNamespaceHandler);
		//helpPane.setText(helpText);
		//helpPane.setCaretPosition(0);
	}
}
