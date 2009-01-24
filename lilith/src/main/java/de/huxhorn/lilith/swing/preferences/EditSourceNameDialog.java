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
package de.huxhorn.lilith.swing.preferences;

import de.huxhorn.sulky.swing.KeyStrokes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class EditSourceNameDialog
	extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(EditSourceNameDialog.class);

	private JTextField sourceIdentifier;
	private JTextField sourceName;
	private OkAction okAction;
	private boolean adding;
	private boolean canceled;

	public EditSourceNameDialog(Dialog owner)
	{
		super(owner);
		setModal(true);
		createUi();
	}

	private void createUi()
	{
		okAction = new OkAction();
		Action cancelAction = new CancelAction();

		TextKeyListener listener = new TextKeyListener();
		sourceIdentifier = new JTextField(25);
		sourceIdentifier.addActionListener(new SourceIdentifierActionListener());
		sourceIdentifier.addKeyListener(listener);

		sourceName = new JTextField(25);
		sourceName.addActionListener(new SourceNameActionListener());
		sourceName.addKeyListener(listener);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.CENTER;
		//gbc.insets = new Insets(0,5,0,0);
		mainPanel.add(new JLabel("Source: "), gbc);

		gbc.gridx = 1;
		mainPanel.add(sourceIdentifier, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		mainPanel.add(new JLabel("Name: "), gbc);

		gbc.gridx = 1;
		mainPanel.add(sourceName, gbc);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(okAction));
		buttonPanel.add(new JButton(cancelAction));
		add(buttonPanel, BorderLayout.SOUTH);

		KeyStrokes.registerCommand(mainPanel, cancelAction, "CANCEL_ACTION");
		KeyStrokes.registerCommand(buttonPanel, cancelAction, "CANCEL_ACTION");

	}

	public void setAdding(boolean adding)
	{
		this.adding = adding;
		if(adding)
		{
			setTitle("Add source name...");
			//sourceIdentifier.setEditable(true);
		}
		else
		{
			setTitle("Edit source name...");
			//sourceIdentifier.setEditable(false);
		}
	}

	public void setVisible(boolean b)
	{
		if(b)
		{
			if(adding)
			{
				sourceIdentifier.requestFocusInWindow();
			}
			else
			{
				sourceName.selectAll();
				sourceName.requestFocusInWindow();
			}
			initUI();
		}
		super.setVisible(b);
	}

	private void updateActions()
	{
		okAction.update();
	}

	public boolean isAdding()
	{
		return adding;
	}

	public boolean isCanceled()
	{
		return canceled;
	}

	public void setSourceName(String sourceName)
	{
		this.sourceName.setText(sourceName);
	}

	public String getSourceName()
	{
		return sourceName.getText();
	}

	public String getSourceIdentifier()
	{
		return sourceIdentifier.getText();
	}

	public void setSourceIdentifier(String sourceIdentifier)
	{
		this.sourceIdentifier.setText(sourceIdentifier);
	}

	public void initUI()
	{
		updateActions();
	}

	private class OkAction
		extends AbstractAction
	{
		public OkAction()
		{
			super("Ok");
		}

		public void update()
		{
			String source = sourceIdentifier.getText();
			String name = sourceName.getText();
			if(name != null && !"".equals(name.trim()) && source != null && !"".equals(source.trim()))
			{
				setEnabled(true);
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			String source = sourceIdentifier.getText();
			if(source != null && !"".equals(source.trim()))
			{
				canceled = false;
				setVisible(false);
			}
		}
	}

	private class CancelAction
		extends AbstractAction
	{

		public CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			canceled = true;
			setVisible(false);
		}
	}

	private class SourceNameActionListener
		implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String name = sourceName.getText();
			if(name != null && !"".equals(name.trim()))
			{
				okAction.actionPerformed(e);
			}
		}
	}

	private class SourceIdentifierActionListener
		implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String source = sourceIdentifier.getText();
			if(source != null && !"".equals(source.trim()))
			{
				sourceName.selectAll();
				sourceName.requestFocusInWindow();
			}
		}
	}

	private class TextKeyListener
		implements KeyListener
	{
		public void keyTyped(KeyEvent e)
		{
			updateActions();
		}

		public void keyPressed(KeyEvent e)
		{
		}

		public void keyReleased(KeyEvent e)
		{
		}
	}

}
