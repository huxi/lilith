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
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;

public class TipOfTheDayDialog
	extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(TipOfTheDayDialog.class);

	private ArrayList<URL> tipsOfTheDay;
	private int currentTipOfTheDay;
	private ApplicationPreferences applicationPreferences;
	private XHTMLPanel helpPane;
	private XhtmlNamespaceHandler xhtmlNamespaceHandler;
	private SelectionHighlighter.CopyAction copyAction;
	private JCheckBox showTipOfTheDayCheckbox;

	public TipOfTheDayDialog(MainFrame owner)
	{
		super(owner);

		applicationPreferences = owner.getApplicationPreferences();

		setTitle("Tip of the Day");
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setModal(false);

		JPanel content=new JPanel(new BorderLayout());
		setLayout(new GridLayout(1,1));
		JLabel didYouKnowLabel = new JLabel("Did you know...?");

		content.add(didYouKnowLabel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new GridBagLayout());

		GridBagConstraints gbc=new GridBagConstraints();

		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.gridy = 0;


		showTipOfTheDayCheckbox = new JCheckBox("Show Tip of the Day on startup.");
		showTipOfTheDayCheckbox.setSelected(applicationPreferences.isShowingTipOfTheDay());
		showTipOfTheDayCheckbox.addItemListener(new CheckboxListener());
		showTipOfTheDayCheckbox.setMnemonic(KeyEvent.VK_S);
		buttonPanel.add(showTipOfTheDayCheckbox, gbc);

		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;

		buttonPanel.add(new JButton(new PreviousTipAction()), gbc);

		gbc.gridx = 1;
		buttonPanel.add(new JButton(new NextTipAction()), gbc);

		CloseAction closeAction = new CloseAction();
		gbc.gridx = 2;
		buttonPanel.add(new JButton(closeAction), gbc);
		
		initHelpResources();
		helpPane = new XHTMLPanel();

		{
			LinkListener originalLinkListener = null;
			java.util.List mouseTrackingList = helpPane.getMouseTrackingListeners();
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
			helpPane.addMouseTrackingListener(new OpenUrlLinkListener(owner, originalLinkListener));
		}

		SelectionHighlighter helpPaneCaret = new SelectionHighlighter();
		helpPaneCaret.install(helpPane);

		copyAction = new SelectionHighlighter.CopyAction();
		copyAction.install(helpPaneCaret);


		xhtmlNamespaceHandler = new XhtmlNamespaceHandler();
		FSScrollPane helpScrollPane = new FSScrollPane(helpPane);
		helpScrollPane.setPreferredSize(new Dimension(400, 200));

		content.add(helpScrollPane, BorderLayout.CENTER);
		content.add(buttonPanel, BorderLayout.SOUTH);

		KeyStrokes.registerCommand(content, closeAction, "CLOSE_ACTION");


		setContentPane(content);

		setCurrentTipOfTheDay(applicationPreferences.getCurrentTipOfTheDay() + 1);
	}

	private void nextTipOfTheDay()
	{
		setCurrentTipOfTheDay(currentTipOfTheDay + 1);
	}

	private void previousTipOfTheDay()
	{
		setCurrentTipOfTheDay(currentTipOfTheDay - 1);
	}

	private void setCurrentTipOfTheDay(int currentTipOfTheDay)
	{
		int count = tipsOfTheDay.size();
		if(currentTipOfTheDay < 0)
		{
			currentTipOfTheDay = count - 1;
		}
		else if(currentTipOfTheDay >= count)
		{
			currentTipOfTheDay = 0;
		}
		this.currentTipOfTheDay = currentTipOfTheDay;
		applicationPreferences.setCurrentTipOfTheDay(currentTipOfTheDay);
		if(logger.isDebugEnabled()) logger.debug("Current Tip of the Day: {}", currentTipOfTheDay);
		helpPane.setDocument(tipsOfTheDay.get(currentTipOfTheDay).toExternalForm(), xhtmlNamespaceHandler);
	}

	private void initHelpResources()
	{
		tipsOfTheDay = new ArrayList<URL>();
		for(int i = 0; ; i++)
		{
			URL url = TipOfTheDayDialog.class.getResource("/tips/" + i + ".xhtml");
			if(url == null)
			{
				break;
			}
			tipsOfTheDay.add(url);
		}
		if(logger.isDebugEnabled()) logger.debug("Tip of the Day URLs found: {}", tipsOfTheDay);
		if(logger.isInfoEnabled()) logger.info("Found {} Tips of the Day.", tipsOfTheDay.size());
	}

	public void copySelection()
	{
		copyAction.actionPerformed(null);
	}

	public void setShowingTipOfTheDay(boolean showingTipOfTheDay)
	{
		showTipOfTheDayCheckbox.setSelected(showingTipOfTheDay);
	}

	private class PreviousTipAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4025074394725104369L;

		private PreviousTipAction()
		{
			super("Previous Tip");
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
		}

		public void actionPerformed(ActionEvent e)
		{
			previousTipOfTheDay();
		}
	}

	private class NextTipAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 107240711521577323L;

		private NextTipAction()
		{
			super("Next Tip");
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		}

		public void actionPerformed(ActionEvent e)
		{
			nextTipOfTheDay();
		}
	}

	private class CloseAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3837690263247686627L;

		private CloseAction()
		{
			super("Close");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);			
		}

		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}
	}

	private class CheckboxListener
		implements ItemListener
	{

		public void itemStateChanged(ItemEvent e)
		{
			Object source = e.getItemSelectable();

			if(source == showTipOfTheDayCheckbox)
			{
				applicationPreferences.setShowingTipOfTheDay(showTipOfTheDayCheckbox.isSelected());
			}
		}
	}
}
