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
import de.huxhorn.sulky.swing.KeyStrokes;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
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

public class CheckForUpdateDialog
	extends JDialog
{
	private static final long serialVersionUID = 7361745831253216248L;

	private final XHTMLPanel helpPane;
	private final JLabel messageLabel;
	private final XhtmlNamespaceHandler xhtmlNamespaceHandler;
	private final ApplicationPreferences applicationPreferences;
	private final JCheckBox checkForUpdateCheckbox;

	private String docRoot;
	private static final int INSET = 10;

	CheckForUpdateDialog(MainFrame mainFrame)
	{
		super(mainFrame);
		this.applicationPreferences = mainFrame.getApplicationPreferences();
		setTitle("Check for Update…");
		setModal(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		helpPane = new XHTMLPanel();

		final Logger logger = LoggerFactory.getLogger(CheckForUpdateDialog.class);

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

		SelectionHighlighter.CopyAction copyAction = new SelectionHighlighter.CopyAction();
		copyAction.install(helpPaneCaret);

		checkForUpdateCheckbox = new JCheckBox("Check for updates on startup.");
		checkForUpdateCheckbox.setSelected(applicationPreferences.isCheckingForUpdate());
		checkForUpdateCheckbox.addItemListener(new CheckboxListener());
		checkForUpdateCheckbox.setMnemonic(KeyEvent.VK_U);


		FSScrollPane helpScrollPane = new FSScrollPane(helpPane);
		helpScrollPane.setPreferredSize(new Dimension(600, 300));

		JPanel content = new JPanel(new GridBagLayout());
		setLayout(new GridLayout(1, 1));
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets.top = INSET;
		gbc.insets.bottom = 0;
		gbc.insets.left = INSET;
		gbc.insets.right = INSET;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;

		messageLabel = new JLabel();
		Font labelFont = messageLabel.getFont();
		labelFont = labelFont.deriveFont(1.5f * labelFont.getSize2D());
		messageLabel.setFont(labelFont);

		content.add(messageLabel, gbc);

		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;

		content.add(helpScrollPane, gbc);

		gbc.gridy = 2;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_START;
		content.add(checkForUpdateCheckbox, gbc);

		gbc.insets.top = INSET;
		gbc.insets.bottom = INSET;
		gbc.insets.left = INSET;
		gbc.insets.right = INSET;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridy = 3;
		OkAction okAction = new OkAction();
		content.add(new JButton(okAction), gbc);
		KeyStrokes.registerCommand(content, okAction, "OK_ACTION");
		KeyStrokes.registerCommand(content, new CancelAction(), "CANCEL_ACTION");

		add(content);

		URL docRootUrl = CheckForUpdateDialog.class.getResource("/help");
		if(docRootUrl != null)
		{
			docRoot = docRootUrl.toString() + "/";
		}
		if(logger.isDebugEnabled()) logger.debug("docRoot: {}", docRoot);
		xhtmlNamespaceHandler = new XhtmlNamespaceHandler();
	}

	public void setMessage(String message)
	{
		if(message == null)
		{
			messageLabel.setText("Your version is up to date.");
			messageLabel.setIcon(null);
		}
		else
		{
			messageLabel.setText(message);
			messageLabel.setIcon(Icons.UPDATE_AVAILABLE_32_ICON);
		}
	}

	void setChanges(String changes)
	{
		if(changes == null)
		{
			changes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<!DOCTYPE html\n" +
				"\tPUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" +
				"\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" +
				"<head>\n" +
				"\t<title>Couldn't load changes file!</title>\n" +
				"\t<link href=\"help.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
				"</head>\n" +
				"<body>\n" +
				"<p>Couldn't load changes file!</p>\n" +
				"</body>\n" +
				"</html>";
		}
		helpPane.setDocumentFromString(changes, docRoot, xhtmlNamespaceHandler);
	}

	void setCheckingForUpdate(boolean checkingForUpdate)
	{
		checkForUpdateCheckbox.setSelected(checkingForUpdate);
	}

	private class OkAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7050362653241782872L;

		OkAction()
		{
			super("Ok");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ENTER);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
			// workaround for http://sourceforge.net/apps/trac/lilith/ticket/72 below
			Container parentContainer = getParent();
			if(parentContainer != null)
			{
				parentContainer.requestFocus();
			}
			// workaround for http://sourceforge.net/apps/trac/lilith/ticket/72 above
		}
	}

	private class CancelAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7356773009949031885L;

		CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ESCAPE);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}
	}

	private class CheckboxListener
		implements ItemListener
	{

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			Object source = e.getItemSelectable();

			if(source == checkForUpdateCheckbox) // NOPMD
			{
				applicationPreferences.setCheckingForUpdate(checkForUpdateCheckbox.isSelected());
			}
		}
	}

}
