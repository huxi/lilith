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

import de.huxhorn.lilith.swing.linklistener.OpenUrlLinkListener;
import de.huxhorn.sulky.swing.KeyStrokes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.swing.SelectionHighlighter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.List;

import javax.swing.*;

public class HelpFrame
	extends JFrame
{
	private final Logger logger = LoggerFactory.getLogger(HelpFrame.class);

	private XHTMLPanel helpPane;
	private XhtmlNamespaceHandler xhtmlNamespaceHandler;
	private MainFrame mainFrame;
	private SelectionHighlighter.CopyAction copyAction;
	private JPopupMenu popup;


	public HelpFrame(MainFrame mainFrame)
		throws HeadlessException
	{
		super();
		this.mainFrame = mainFrame;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		initUI();
	}

	private void initUI()
	{
		helpPane = new XHTMLPanel();

		{
			LinkListener originalLinkListener = null;
			List mouseTrackingList = helpPane.getMouseTrackingListeners();
			if(mouseTrackingList != null)
			{
				for(Object o : mouseTrackingList)
				{
					if(logger.isDebugEnabled()) logger.debug("Before MTL {}", o);
					if(o instanceof LinkListener)
					{
						helpPane.removeMouseTrackingListener((LinkListener) o);
						originalLinkListener = (LinkListener) o;
					}
				}
			}
			helpPane.addMouseTrackingListener(new OpenUrlLinkListener(mainFrame, originalLinkListener));
		}

		SelectionHighlighter helpPaneCaret = new SelectionHighlighter();
		helpPaneCaret.install(helpPane);

		copyAction = new SelectionHighlighter.CopyAction();
		copyAction.install(helpPaneCaret);


		xhtmlNamespaceHandler = new XhtmlNamespaceHandler();
		FSScrollPane helpScrollPane = new FSScrollPane(helpPane);

		setContentPane(helpScrollPane);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle maxBounds = ge.getMaximumWindowBounds();
		setSize(maxBounds.width / 2, maxBounds.height / 2);
		{
			URL url = HelpFrame.class.getResource("/tango/16x16/apps/help-browser.png");

			if(url != null)
			{
				ImageIcon icon = new ImageIcon(url);
				setIconImage(icon.getImage());
			}
		}

		CopySelectionAction copySelectionAction = new CopySelectionAction();
		JMenuBar menuBar = new JMenuBar();
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(copySelectionAction);
		menuBar.add(editMenu);
		setJMenuBar(menuBar);

		popup = new JPopupMenu();
		popup.add(copySelectionAction);
		helpPane.addMouseListener(new PopupMouseListener());
	}

	private void showPopup(MouseEvent evt)
	{
		Point p = evt.getPoint();
		if(logger.isInfoEnabled()) logger.info("Show popup at {}.", p);
		popup.show(helpPane, p.x, p.y);
	}

	private class PopupMouseListener
		implements MouseListener
	{
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mousePressed(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}
	}

	public void copySelection()
	{
		copyAction.actionPerformed(null);
	}

	public void setHelpUrl(URL helpUrl)
	{
		helpPane.setDocument(helpUrl.toExternalForm(), xhtmlNamespaceHandler);
		// TODO: jump to anchor. How can I do this??
		//helpPane.setText(helpText);
		//helpPane.setCaretPosition(0);
	}

	private class CopySelectionAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -551520865313383753L;

		public CopySelectionAction()
		{
			super("Copy selection");
			putValue(Action.SHORT_DESCRIPTION, "Copies the selection to the clipboard.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " C");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			copySelection();
		}
	}
}
