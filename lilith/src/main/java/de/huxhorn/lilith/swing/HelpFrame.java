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

import de.huxhorn.lilith.swing.linklistener.OpenUrlLinkListener;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.context.AWTFontResolver;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.swing.SelectionHighlighter;

class HelpFrame
	extends JFrame
{
	private static final long serialVersionUID = 8050028149955107404L;

	private static final URL HELP_BASE_URL = HelpFrame.class.getResource("/help");

	private final Logger logger = LoggerFactory.getLogger(HelpFrame.class);

	private final XHTMLPanel helpPane;
	private final XhtmlNamespaceHandler xhtmlNamespaceHandler;
	private final SelectionHighlighter.CopyAction copyAction;
	private final JPopupMenu popup;

	HelpFrame(MainFrame mainFrame)
		throws HeadlessException
	{
		super();
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		helpPane = new XHTMLPanel();
		xhtmlNamespaceHandler = new XhtmlNamespaceHandler();

		// below does not work properly...
		//String baseUrlString=HELP_BASE_URL.toString()+"/";
		//if(logger.isDebugEnabled()) logger.debug("Help Base-URL: {}", baseUrlString);
		//helpPane.getSharedContext().setBaseURL(baseUrlString);

		{
			SharedContext sharedContext = helpPane.getSharedContext();
			TextRenderer textRenderer = sharedContext.getTextRenderer();
			textRenderer.setSmoothingThreshold(RendererConstants.SMOOTHING_THRESHOLD);
			FontResolver fontResolver = sharedContext.getFontResolver();
			if(fontResolver instanceof AWTFontResolver && RendererConstants.MENSCH_FONT != null)
			{
				AWTFontResolver awtFontResolver = (AWTFontResolver) fontResolver;
				awtFontResolver.setFontMapping(RendererConstants.MONOSPACED_FAMILY, RendererConstants.MENSCH_FONT);
				if(logger.isInfoEnabled()) logger.info("Installed '{}' font.", RendererConstants.MONOSPACED_FAMILY);
			}
		}

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


		FSScrollPane helpScrollPane = new FSScrollPane(helpPane);

		setContentPane(helpScrollPane);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle maxBounds = ge.getMaximumWindowBounds();
		setSize(maxBounds.width / 2, maxBounds.height / 2);

		setIconImages(Icons.resolveFrameIconImages(LilithFrameId.HELP));

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
		extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		@Override
		public void mousePressed(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}

		@Override
		public void mouseReleased(MouseEvent evt)
		{
			if(evt.isPopupTrigger())
			{
				showPopup(evt);
			}
		}
	}

	private void copySelection()
	{
		copyAction.actionPerformed(null);
	}

	void setHelpUrl(String helpUrl)
	{
		int hashIndex=helpUrl.indexOf('#');
		if(hashIndex > -1)
		{
			// drop anchor for now...
			helpUrl=helpUrl.substring(0,hashIndex);
		}
		helpUrl= HELP_BASE_URL.toExternalForm()+"/"+helpUrl;
		if(logger.isDebugEnabled()) logger.debug("Final helpUrl: {}", helpUrl);
		helpPane.setDocument(helpUrl, xhtmlNamespaceHandler);
		// TODO: jump to anchor. How can I do this??
	}

	private class CopySelectionAction
		extends AbstractLilithAction
	{
		private static final long serialVersionUID = 1045694633251532888L;

		CopySelectionAction()
		{
			super(LilithActionId.COPY_SELECTION);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			copySelection();
		}
	}
}
