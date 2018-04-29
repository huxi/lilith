/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseAgreementDialog
	extends JDialog
{
	private static final long serialVersionUID = -4349856392257012707L;
	private final Logger logger = LoggerFactory.getLogger(LicenseAgreementDialog.class);

	private boolean licenseAgreed;


	public LicenseAgreementDialog()
	{
		super((Frame) null, "License Agreement", true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				declineLicense();
			}
		});
		licenseAgreed = false;
		initUI();
	}

	@SuppressWarnings("PMD.DoNotCallSystemExit")
	private void initUI()
	{
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		JTextPane licenseTextPane = new JTextPane();
		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();

		String licenseText = null;
		InputStream licenseStream = LicenseAgreementDialog.class.getResourceAsStream("/licenses/license.html");
		if(licenseStream != null)
		{
			try(InputStream input=licenseStream)
			{
				licenseText = IOUtils.toString(input, StandardCharsets.UTF_8);
			}
			catch(IOException ex)
			{
				if(logger.isErrorEnabled()) logger.error("Exception while loading license!", ex);
			}
		}
		if(licenseText == null)
		{
			if(logger.isErrorEnabled()) logger.error("Couldn't find license text! Exiting!");
			System.exit(-1);
		}

		licenseTextPane.setEditorKit(htmlEditorKit);
		licenseTextPane.setEditable(false);
		licenseTextPane.setText(licenseText);
		JScrollPane licenseScrollPane = new JScrollPane(licenseTextPane);
		licenseScrollPane.setPreferredSize(new Dimension(640, 480));

		content.add(licenseScrollPane, BorderLayout.CENTER);
		content.add(buttonPanel, BorderLayout.SOUTH);

		AcceptAction acceptAction = new AcceptAction();
		DeclineAction declineAction = new DeclineAction();

		JButton acceptButton = new JButton(acceptAction);
		JButton declineButton = new JButton(declineAction);

		buttonPanel.add(acceptButton);
		buttonPanel.add(declineButton);
		setContentPane(content);
		licenseTextPane.setCaretPosition(0);
		declineButton.requestFocusInWindow();
	}

	public boolean isLicenseAgreed()
	{
		return licenseAgreed;
	}

	public void setLicenseAgreed(boolean licenseAgreed)
	{
		this.licenseAgreed = licenseAgreed;
		if(isVisible())
		{
			setVisible(false);
		}
	}

	private void acceptLicense()
	{
		setLicenseAgreed(true);
	}

	private void declineLicense()
	{
		setLicenseAgreed(false);
	}

	private class AcceptAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 5602271076398281248L;

		AcceptAction()
		{
			super("Accept");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			acceptLicense();
		}
	}

	private class DeclineAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 5007161609485321187L;

		DeclineAction()
		{
			super("Decline");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			declineLicense();
		}
	}
}
