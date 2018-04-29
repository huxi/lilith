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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorChooserPanel
	extends JPanel
{
	private static final long serialVersionUID = -8183344633360150605L;

	private final Logger logger = LoggerFactory.getLogger(ColorChooserPanel.class);

	private final JColorChooser colorChooser;
	private final JCheckBox inheritCheckbox;
	private final Color defaultColor;

	ColorChooserPanel(Color defaultColor)
	{
		this.defaultColor=defaultColor;

		JPanel emptyPreview = new JPanel();
		emptyPreview.setMinimumSize(new Dimension(0, 0));
		emptyPreview.setPreferredSize(new Dimension(0, 0));
		emptyPreview.setMaximumSize(new Dimension(0, 0));

		setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();

		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.1;
		gbc.weighty = 0.1;

		inheritCheckbox = new JCheckBox("Inherit");
		inheritCheckbox.setToolTipText("Select if color is undefined and will be derived from default or other condition.");
		inheritCheckbox.setSelected(false);
		inheritCheckbox.setMnemonic(KeyEvent.VK_I);
		inheritCheckbox.setVerticalAlignment(SwingConstants.TOP);
		inheritCheckbox.setHorizontalAlignment(SwingConstants.LEFT);
		inheritCheckbox.addActionListener(new InheritListener());
		add(inheritCheckbox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		colorChooser=new JColorChooser();
		colorChooser.setPreviewPanel(emptyPreview);
		add(colorChooser, gbc);

	}

	public JColorChooser getColorChooser()
	{
		return colorChooser;
	}

	public void setColor(Color color)
	{
		if(color == null)
		{
			inheritCheckbox.setSelected(true);

		}
		else
		{
			inheritCheckbox.setSelected(false);
			colorChooser.setColor(color);
		}
		updateComponents();
	}

	private void updateComponents()
	{
		if(logger.isDebugEnabled()) logger.debug("updateComponents...");

		// Due to http://bugs.sun.com/view_bug.do?bug_id=4222508
		// reported 1999-03-22 and seemingly fixed in some beta build
		// of Java 7 (7(b46)), disabling a color chooser is not possible.
		// Therefore I'll make the chooser invisible if disabled.
		// m(
		if(inheritCheckbox.isSelected())
		{
			colorChooser.setColor(defaultColor);
			colorChooser.setVisible(false);
		}
		else
		{
			colorChooser.setVisible(true);
		}
	}

	/**
	 * Shortcut for getColor(false).
	 * Use getColor(true) if you want to make sure that you always retrieve a Color.
	 *
	 * @return the Color, if it is not inherited. Otherwise null.
	 */
	public Color getColor()
	{
		return getColor(false);
	}

	Color getColor(boolean returnDefault)
	{
		if(!inheritCheckbox.isSelected())
		{
			return colorChooser.getColor();
		}
		if(returnDefault)
		{
			return defaultColor;
		}
		return null;
	}

	private class InheritListener
		implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			updateComponents();
		}
	}
}
