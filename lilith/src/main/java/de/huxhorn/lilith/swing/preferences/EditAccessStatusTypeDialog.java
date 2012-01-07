/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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

import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.lilith.swing.LilithKeyStrokes;
import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.sulky.swing.KeyStrokes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

public class EditAccessStatusTypeDialog
	extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(EditAccessStatusTypeDialog.class);

	private ColorScheme scheme;
	private boolean canceled;

	private ColorSchemeEditorPanel colorSchemeEditorPanel;

	public EditAccessStatusTypeDialog(Dialog owner)
	{
		super(owner);
		setModal(true);
		createUi();
	}

	private void createUi()
	{
		OkAction okAction = new OkAction();
		Action cancelAction = new CancelAction();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
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

	public void setVisible(boolean b)
	{
		if(b)
		{
			initUI();
		}
		super.setVisible(b);
	}

	public ColorScheme getScheme()
	{
		return scheme;
	}

	public void setScheme(ColorScheme scheme)
	{
		this.scheme = scheme;
	}

	public boolean isCanceled()
	{
		return canceled;
	}

	public void initUI()
	{
		if(scheme == null)
		{
			scheme = new ColorScheme().initDefaults();
		}

		colorSchemeEditorPanel.setColorScheme(scheme);
	}

	public void setType(HttpStatus.Type type)
	{
		setTitle("Edit colors for "+type+"...");
	}

	private class OkAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7380136684827113354L;

		public OkAction()
		{
			super("Ok");
		}

		public void actionPerformed(ActionEvent e)
		{
			canceled = false;
			colorSchemeEditorPanel.saveColors();
			scheme = colorSchemeEditorPanel.getColorScheme();
			EditAccessStatusTypeDialog.super.setVisible(false);
		}
	}

	private class ResetAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 3523022122100092148L;

		public ResetAction()
		{
			super("Reset");
		}

		public void actionPerformed(ActionEvent e)
		{
			initUI();
		}
	}

	private class CancelAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 5442950514112749763L;

		public CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = LilithKeyStrokes.getKeyStroke(LilithKeyStrokes.ESCAPE);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			canceled = true;
			EditAccessStatusTypeDialog.super.setVisible(false);
		}
	}
}
