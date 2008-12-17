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

import de.huxhorn.sulky.swing.KeyStrokes;
import de.huxhorn.sulky.swing.Windows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class EditConditionDialog
	extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(EditSourceNameDialog.class);

	private SavedCondition savedCondition;
	private JTextField conditionName;
	private OkAction okAction;
	private boolean adding;
	private boolean canceled;
	private ColorChooserDialog colorChooserDialog;

	public EditConditionDialog(Dialog owner)
	{
		super(owner);
		setModal(true);
		createUi();
	}

	private void createUi()
	{
		colorChooserDialog=new ColorChooserDialog(this);
		okAction=new OkAction();
		Action cancelAction = new CancelAction();
		Action editColorAction = new EditColorAction();

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
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.CENTER;
		//gbc.insets = new Insets(0,5,0,0);
		mainPanel.add(new JLabel("Name: "), gbc);

		gbc.gridx = 1;
		mainPanel.add(conditionName, gbc);

		/*
		gbc.gridx = 0;
		gbc.gridy = 1;
		mainPanel.add(new JLabel("Name: "), gbc);

		gbc.gridx = 1;
		mainPanel.add(sourceName, gbc);
        */
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(okAction));
		buttonPanel.add(new JButton(editColorAction));
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
		colorChooserDialog.setColorScheme(savedCondition.getColorScheme());
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
				if(!colorChooserDialog.isCanceled())
				{
					savedCondition.setColorScheme(colorChooserDialog.getColorScheme());
				}
				EditConditionDialog.super.setVisible(false);
			}
		}
	}


	private class EditColorAction
		extends AbstractAction
	{

		public EditColorAction()
		{
			super("Edit color");
//			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
//			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
//			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
//			canceled=true;
//			setVisible(false);
			Windows.showWindow(colorChooserDialog, EditConditionDialog.this, true);
			//colorChooserDialog.setVisible(true);

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