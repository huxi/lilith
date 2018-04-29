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

package de.huxhorn.lilith.debug;

import de.huxhorn.lilith.swing.MainFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugDialog
	extends JDialog
{
	private static final long serialVersionUID = 2056161561781289686L;
	private final Logger logger = LoggerFactory.getLogger(DebugDialog.class);

	private final MainFrame mainFrame;
	private LoggerEventEmitter loggerEventEmitter;

	public DebugDialog(Frame owner, MainFrame mainFrame)
	{
		super(owner, "Debug");
		this.mainFrame = mainFrame;
		initUI();
	}

	private void initUI()
	{
		setModal(false);
		loggerEventEmitter = new LoggerEventEmitter();
		loggerEventEmitter.setDelay(0);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(new DoneAction()));

		JButton button;
		Action action;

		JPanel loggingPanel = new JPanel(new GridBagLayout());
		loggingPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Create logging events"));
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		action = new LogStuffAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 0;
		loggingPanel.add(button, gbc);

		action = new LogASCIIArtAction();
		button = new JButton(action);
		gbc.gridx = 1;
		gbc.gridy = 0;
		loggingPanel.add(button, gbc);

		action = new LogExceptionsAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 1;
		loggingPanel.add(button, gbc);

		action = new LogParamExceptionsAction();
		button = new JButton(action);
		gbc.gridx = 1;
		gbc.gridy = 1;
		loggingPanel.add(button, gbc);

		action = new LogTruthAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 2;
		loggingPanel.add(button, gbc);

		action = new LogAnonymousAction();
		button = new JButton(action);
		gbc.gridx = 1;
		gbc.gridy = 2;
		loggingPanel.add(button, gbc);

		action = new LogNDCAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 3;
		loggingPanel.add(button, gbc);

		action = new LogDateAction();
		button = new JButton(action);
		gbc.gridx = 1;
		gbc.gridy = 3;
		loggingPanel.add(button, gbc);

		action = new LogContainerAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 4;
		loggingPanel.add(button, gbc);

		action = new LogJulAction();
		button = new JButton(action);
		gbc.gridx = 1;
		gbc.gridy = 4;
		loggingPanel.add(button, gbc);

		action = new LogAllAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		loggingPanel.add(button, gbc);
		gbc.gridwidth = 1;

		JPanel miscPanel = new JPanel(new GridBagLayout());
		miscPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Miscellaneous"));

		action = new NetworkAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 0;
		miscPanel.add(button, gbc);

		action = new DebugCallableAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 1;
		miscPanel.add(button, gbc);

		action = new EditGroovyAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 2;
		miscPanel.add(button, gbc);

		action = new UncaughtExceptionAction();
		button = new JButton(action);
		gbc.gridx = 0;
		gbc.gridy = 3;
		miscPanel.add(button, gbc);

		action = new UsingThymeleafAction();
		JCheckBox checkBox =new JCheckBox(action);
		checkBox.setSelected(mainFrame.isUsingThymeleaf());
		gbc.gridx = 0;
		gbc.gridy = 4;
		miscPanel.add(checkBox, gbc);

		JPanel centerPanel = new JPanel(new GridBagLayout());
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		centerPanel.add(loggingPanel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		centerPanel.add(miscPanel, gbc);


		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(centerPanel, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
	}

	private class DoneAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7747612911180730271L;

		DoneAction()
		{
			super("Done");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}
	}

	private class LogStuffAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3191463140774402016L;

		LogStuffAction()
		{
			super("Log stuff");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events, some containing MDC, some containing Markers and some containing both.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logStuff();
			loggerEventEmitter.logStuffWithMarker();
			loggerEventEmitter.logStuffWithMdc();
			loggerEventEmitter.logStuffWithMdcAndMarker();
		}
	}

	private class LogJulAction
			extends AbstractAction
	{
		private static final long serialVersionUID = -1010040947971712929L;

		LogJulAction()
		{
			super("Log j.u.l.Logger");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events via java.util.logging.Logger.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logJul();
		}
	}

	private class LogContainerAction
			extends AbstractAction
	{
		private static final long serialVersionUID = -4705100235484844484L;

		LogContainerAction()
		{
			super("Log containers");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events with arrays, Collections and Maps as parameters.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logContainers();
		}
	}

	private class LogASCIIArtAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3162155864945849819L;

		LogASCIIArtAction()
		{
			super("Log ASCII-Art");
			putValue(Action.SHORT_DESCRIPTION, "Logs some ASCII-Art. This can be used to see if details view is handling preformatted text correctly. May be NSFW depending on the closed-mindedness of your working environment...");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logASCII();
		}
	}

	private class LogExceptionsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -2289262066339501111L;

		LogExceptionsAction()
		{
			super("Log exceptions");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing exceptions.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logException();
			loggerEventEmitter.logException2();
			loggerEventEmitter.logExceptionSuppressed();
		}
	}

	private class LogParamExceptionsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7404752330757185806L;

		LogParamExceptionsAction()
		{
			super("Log param exceptions");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing exceptions. Exceptions are given as a parameter which is not supported by Logback. The exceptions will only show up in the MultiplexAppenders.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logParamException();
			loggerEventEmitter.logParamException2();
		}
	}

	private class LogTruthAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7354728704746203904L;

		LogTruthAction()
		{
			super("Log truth");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing Discordian Truth.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logTruth();
		}
	}

	private class LogAnonymousAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3343304084268635261L;

		LogAnonymousAction()
		{
			super("Log anonymous");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events inside anonymous inner classes. Used to check if STE is handled correctly.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logAnonymous();
		}
	}

	private class LogNDCAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -518958063141973150L;

		LogNDCAction()
		{
			super("Log NDC");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing NDC. This is not supported by Logback. The NDC will only show up in the MultiplexAppenders.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logNDC();
		}
	}

	private class LogDateAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -1734237014776105342L;

		LogDateAction()
		{
			super("Log date");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing a Date as an parameter. This will use Date.toString in case of Logback and ISO8601-format in case of MultiplexAppenders.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logDate();
		}
	}

	private class LogAllAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -5004276984975201630L;

		LogAllAction()
		{
			super("Log all!");
			putValue(Action.SHORT_DESCRIPTION, "Executes all of the above logging examples. May be NSFW depending on the closed-mindedness of your working environment…");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logStuff();
			loggerEventEmitter.logStuffWithMarker();
			loggerEventEmitter.logStuffWithMdc();
			loggerEventEmitter.logStuffWithMdcAndMarker();
			loggerEventEmitter.logException();
			loggerEventEmitter.logException2();
			loggerEventEmitter.logExceptionSuppressed();
			loggerEventEmitter.logParamException();
			loggerEventEmitter.logParamException2();
			loggerEventEmitter.logASCII();
			loggerEventEmitter.logTruth();
			loggerEventEmitter.logAnonymous();
			loggerEventEmitter.logNDC();
			loggerEventEmitter.logDate();
			loggerEventEmitter.logContainers();
			loggerEventEmitter.logJul();
		}
	}


	private class NetworkAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3099597544720236257L;

		NetworkAction()
		{
			super("Network");
			putValue(Action.SHORT_DESCRIPTION, "Detects all non-local network addresses and logs them to the internal Lilith log.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Set<InetAddress> inetAddresses = new HashSet<>();
			try
			{
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

				while(networkInterfaces.hasMoreElements())
				{
					NetworkInterface ni = networkInterfaces.nextElement();
					Enumeration<InetAddress> interfaceInetAddresses = ni.getInetAddresses();
					while(interfaceInetAddresses.hasMoreElements())
					{
						InetAddress address = interfaceInetAddresses.nextElement();
						if(!address.isLoopbackAddress())
						{
							inetAddresses.add(address);
						}
					}
				}
			}
			catch(SocketException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while retrieving InetAddresses!", ex);
			}
			if(logger.isInfoEnabled()) logger.info("InetAddresses: {}", inetAddresses);
		}
	}

	private class DebugCallableAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -97047951966954750L;

		DebugCallableAction()
		{
			super("Callable");
			putValue(Action.SHORT_DESCRIPTION, "Creates a callable that's simply counting up and adds it to the task-manager.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			mainFrame.getLongWorkManager()
				.startTask(new DebugProgressingCallable(), "Test Task", "This is just a simply test task.");
		}
	}

	private class EditGroovyAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8812061542734868784L;

		EditGroovyAction()
		{
			super("Edit details view groovy!");
			putValue(Action.SHORT_DESCRIPTION, "Edit the details view Groovy file.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			mainFrame.getPreferencesDialog().editDetailsFormatter();
		}
	}

	private class UncaughtExceptionAction
			extends AbstractAction
	{
		private static final long serialVersionUID = -3600189121760822853L;

		UncaughtExceptionAction()
		{
			super("Uncaught Exception");
			putValue(Action.SHORT_DESCRIPTION, "Throws an uncaught exception.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new RuntimeException("Uncaught Exception!"); // NOPMD
		}
	}

	private class UsingThymeleafAction
			extends AbstractAction
	{
		private static final long serialVersionUID = -2547578555283622327L;

		UsingThymeleafAction()
		{
			super("Using Thymeleaf");
			putValue(Action.SHORT_DESCRIPTION, "Using Thymeleaf instead of Groovy for DetailsView.");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			mainFrame.setUsingThymeleaf(!mainFrame.isUsingThymeleaf());
		}
	}
}
