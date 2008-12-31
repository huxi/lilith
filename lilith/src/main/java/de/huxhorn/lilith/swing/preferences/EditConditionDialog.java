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
package de.huxhorn.lilith.swing.preferences;

import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.sulky.swing.KeyStrokes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class EditConditionDialog
	extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(EditSourceNameDialog.class);

	private SavedCondition savedCondition;
	private JTextField conditionName;
	private OkAction okAction;
	private boolean adding;
	private boolean canceled;

	private JCheckBox activeCheckBox;
    private ColorSchemeEditorPanel colorSchemeEditorPanel;

    public EditConditionDialog(Dialog owner)
	{
		super(owner);
		setModal(true);
		createUi();
	}

	private void createUi()
	{
		okAction=new OkAction();
		Action cancelAction = new CancelAction();

		TextKeyListener listener=new TextKeyListener();

		conditionName = new JTextField(25);
		conditionName.addActionListener(new ConditionNameActionListener());
		conditionName.addKeyListener(listener);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		//gbc.insets = new Insets(0,5,0,0);
		JLabel conditionNameLabel = new JLabel("Name: ");
		conditionNameLabel.setLabelFor(conditionName);
		mainPanel.add(conditionNameLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(conditionName, gbc);

		gbc.gridx=2;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		activeCheckBox=new JCheckBox("Active");
		activeCheckBox.setToolTipText("<html>Active conditions are used to determine the rendering of the table cells.<br>Too many active conditions will slow down the application!</html>");
		mainPanel.add(activeCheckBox, gbc);

		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=3;
		gbc.weightx = 1.0;

        colorSchemeEditorPanel=new ColorSchemeEditorPanel();

		mainPanel.add(colorSchemeEditorPanel, gbc);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(okAction));
		buttonPanel.add(new JButton(cancelAction));
		add(buttonPanel, BorderLayout.SOUTH);

		KeyStrokes.registerCommand(mainPanel, cancelAction, "CANCEL_ACTION");
		KeyStrokes.registerCommand(buttonPanel, cancelAction, "CANCEL_ACTION");
	}


	public void setAdding(boolean adding)
	{
		this.adding=adding;
		if(adding)
		{
			setTitle("Add condition...");
		}
		else
		{
			setTitle("Edit condition...");
		}
	}

	public void setVisible(boolean b)
	{
		if(b)
		{
			conditionName.requestFocusInWindow();

			/*
			if(adding)
			{
				sourceName.requestFocusInWindow();
			}
			else
			{
				sourceName.selectAll();
				sourceName.requestFocusInWindow();
			}
			*/
			initUI();
		}
		super.setVisible(b);
	}

	private void updateActions()
	{
		okAction.update();
	}

	public SavedCondition getSavedCondition()
	{
		return savedCondition;
	}

	public void setSavedCondition(SavedCondition savedCondition)
	{
		this.savedCondition = savedCondition;
	}

	public boolean isAdding()
	{
		return adding;
	}

	public boolean isCanceled()
	{
		return canceled;
	}

	public void initUI()
	{
		conditionName.setText(savedCondition.getName());
		ColorScheme colorScheme = savedCondition.getColorScheme();

        colorSchemeEditorPanel.setColorScheme(colorScheme);

		activeCheckBox.setSelected(savedCondition.isActive());
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
			String name=conditionName.getText();
			if(name!=null && !"".equals(name.trim()))
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
			String name=conditionName.getText();
			if(name!=null && !"".equals(name.trim()))
			{
				canceled=false;
				savedCondition.setName(conditionName.getText());
				ColorScheme colorScheme=colorSchemeEditorPanel.getColorScheme();
				savedCondition.setColorScheme(colorScheme);
				savedCondition.setActive(activeCheckBox.isSelected());
				EditConditionDialog.super.setVisible(false);
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
			canceled=true;
			EditConditionDialog.super.setVisible(false);
		}
	}

	private class ConditionNameActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String name=conditionName.getText();
			if(name!=null && !"".equals(name.trim()))
			{
				okAction.actionPerformed(e);
			}
		}
	}

	private class TextKeyListener implements KeyListener
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