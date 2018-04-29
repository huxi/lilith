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

package de.huxhorn.lilith.swing.preferences;

import de.huxhorn.lilith.swing.LilithKeyStrokes;
import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.sulky.swing.KeyStrokes;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class EditConditionDialog
	extends JDialog
{
	private static final long serialVersionUID = -217524106405669380L;

	private final JTextField conditionName;
	private final OkAction okAction;
	private final JCheckBox activeCheckBox;
	private final ColorSchemeEditorPanel colorSchemeEditorPanel;

	private SavedCondition savedCondition;
	private boolean canceled;

	EditConditionDialog(Dialog owner)
	{
		super(owner);
		setModal(true);

		okAction = new OkAction();
		Action cancelAction = new CancelAction();

		TextKeyListener listener = new TextKeyListener();

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
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		//gbc.insets = new Insets(0,5,0,0);
		JLabel conditionNameLabel = new JLabel("Name: ");
		conditionNameLabel.setLabelFor(conditionName);
		mainPanel.add(conditionNameLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(conditionName, gbc);

		gbc.gridx = 2;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		activeCheckBox = new JCheckBox("Active");
		activeCheckBox.setToolTipText("<html>Active conditions are used to determine the rendering of the table cells.<br>Too many active conditions will slow down the application!</html>");
		mainPanel.add(activeCheckBox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;

		colorSchemeEditorPanel = new ColorSchemeEditorPanel();

		mainPanel.add(colorSchemeEditorPanel, gbc);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(okAction));
		buttonPanel.add(new JButton(new ResetAction()));
		buttonPanel.add(new JButton(cancelAction));
		add(buttonPanel, BorderLayout.SOUTH);

		KeyStrokes.registerCommand(mainPanel, cancelAction, "CANCEL_ACTION");
		KeyStrokes.registerCommand(buttonPanel, cancelAction, "CANCEL_ACTION");
	}


	void setAdding(boolean adding)
	{
		if(adding)
		{
			setTitle("Add condition…");
		}
		else
		{
			setTitle("Edit condition…");
		}
	}

	@Override
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

	public boolean isCanceled()
	{
		return canceled;
	}

	public void initUI()
	{
		conditionName.setText(savedCondition.getName());
		ColorScheme colorScheme = savedCondition.getColorScheme();

		if(colorScheme == null)
		{
			colorScheme = new ColorScheme().initDefaults();
		}

		colorSchemeEditorPanel.setColorScheme(colorScheme);

		activeCheckBox.setSelected(savedCondition.isActive());
		updateActions();
	}

	private class OkAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7380136684827113354L;

		OkAction()
		{
			super("Ok");
		}

		public void update()
		{
			String name = conditionName.getText();
			if(name != null && !"".equals(name.trim()))
			{
				setEnabled(true);
			}
			else
			{
				setEnabled(false);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String name = conditionName.getText();
			if(name != null && !"".equals(name.trim()))
			{
				canceled = false;
				savedCondition.setName(conditionName.getText());
				colorSchemeEditorPanel.saveColors();
				ColorScheme colorScheme = colorSchemeEditorPanel.getColorScheme();
				savedCondition.setColorScheme(colorScheme);
				savedCondition.setActive(activeCheckBox.isSelected());
				EditConditionDialog.super.setVisible(false);
			}
		}
	}

	private class ResetAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 3523022122100092148L;

		ResetAction()
		{
			super("Reset");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			initUI();
		}
	}

	private class CancelAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 5442950514112749763L;

		CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ESCAPE);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			canceled = true;
			EditConditionDialog.super.setVisible(false);
		}
	}

	private class ConditionNameActionListener
		implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String name = conditionName.getText();
			if(name != null && !"".equals(name.trim()))
			{
				okAction.actionPerformed(e);
			}
		}
	}

	private class TextKeyListener
		implements KeyListener
	{
		@Override
		public void keyTyped(KeyEvent e)
		{
			updateActions();
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			// no-op
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			// no-op
		}
	}
}
