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
package de.huxhorn.lilith.debug;

import de.huxhorn.lilith.swing.MainFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class DebugDialog
	extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(DebugDialog.class);

	LoggerEventEmitter loggerEventEmitter;
	private MainFrame mainFrame;

	public DebugDialog(Frame owner, MainFrame mainFrame)
	{
		super(owner, "Debug");
		this.mainFrame = mainFrame;
		initUI();
	}

	public DebugDialog(Dialog owner)
	{
		super(owner, "Debug");
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

		action = new LogSkullAction();
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

		action = new LogAllAction();
		button = new JButton(action);
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 4;
		loggingPanel.add(button, gbc);

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

		public DoneAction()
		{
			super("Done");
		}

		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}
	}

	private class LogStuffAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3191463140774402016L;

		public LogStuffAction()
		{
			super("Log stuff");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events, some containing MDC, some containing Markers and some containing both.");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logStuff();
			loggerEventEmitter.logStuffWithMarker();
			loggerEventEmitter.logStuffWithMdc();
			loggerEventEmitter.logStuffWithMdcAndMarker();
		}
	}

	private class LogSkullAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3162155864945849819L;

		public LogSkullAction()
		{
			super("Log skull");
			putValue(Action.SHORT_DESCRIPTION, "Logs an ASCII-art skull. This can be used to see if details view is handling preformatted text correctly.");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logSkull();
		}
	}

	private class LogExceptionsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -2289262066339501111L;

		public LogExceptionsAction()
		{
			super("Log exceptions");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing exceptions.");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logException();
			loggerEventEmitter.logException2();
		}
	}

	private class LogParamExceptionsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7404752330757185806L;

		public LogParamExceptionsAction()
		{
			super("Log param exceptions");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing exceptions. Exceptions are given as a parameter which is not supported by Logback. The exceptions will only show up in the MultiplexAppenders.");
		}

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

		public LogTruthAction()
		{
			super("Log truth");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing Discordian Truth.");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logTruth();
		}
	}

	private class LogAnonymousAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3343304084268635261L;

		public LogAnonymousAction()
		{
			super("Log anonymous");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events inside anonymous inner classes. Used to check if STE is handled correctly.");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logAnonymous();
		}
	}

	private class LogNDCAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -518958063141973150L;

		public LogNDCAction()
		{
			super("Log NDC");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing NDC. This is not supported by Logback. The NDC will only show up in the MultiplexAppenders.");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logNDC();
		}
	}

	private class LogDateAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -1734237014776105342L;

		public LogDateAction()
		{
			super("Log date");
			putValue(Action.SHORT_DESCRIPTION, "Creates logging events containing a Date as an parameter. This will use Date.toString in case of Logback and ISO8601-format in case of MultiplexAppenders.");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logDate();
		}
	}

	private class LogAllAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -5004276984975201630L;

		public LogAllAction()
		{
			super("Log all!");
			putValue(Action.SHORT_DESCRIPTION, "Executes all of the above logging examples.");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logStuff();
			loggerEventEmitter.logStuffWithMarker();
			loggerEventEmitter.logStuffWithMdc();
			loggerEventEmitter.logStuffWithMdcAndMarker();
			loggerEventEmitter.logException();
			loggerEventEmitter.logException2();
			loggerEventEmitter.logParamException();
			loggerEventEmitter.logParamException2();
			loggerEventEmitter.logSkull();
			loggerEventEmitter.logTruth();
			loggerEventEmitter.logAnonymous();
			loggerEventEmitter.logNDC();
			loggerEventEmitter.logDate();
		}
	}


	private class NetworkAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3099597544720236257L;

		public NetworkAction()
		{
			super("Network");
			putValue(Action.SHORT_DESCRIPTION, "Detects all non-local network addresses and logs them to the internal Lilith log.");
		}

		public void actionPerformed(ActionEvent e)
		{
			Set<InetAddress> inetAddresses = new HashSet<InetAddress>();
			try
			{
				Enumeration<NetworkInterface> netIfcs = NetworkInterface.getNetworkInterfaces();

				while(netIfcs.hasMoreElements())
				{
					NetworkInterface ni = netIfcs.nextElement();
					Enumeration<InetAddress> inetAddrs = ni.getInetAddresses();
					while(inetAddrs.hasMoreElements())
					{
						InetAddress iadd = inetAddrs.nextElement();
						if(!iadd.isLoopbackAddress())
						{
							inetAddresses.add(iadd);
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

		public DebugCallableAction()
		{
			super("Callable");
			putValue(Action.SHORT_DESCRIPTION, "Creates a callable that's simply counting up and adds it to the task-manager.");
		}

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

		public EditGroovyAction()
		{
			super("Edit groovy!");
			putValue(Action.SHORT_DESCRIPTION, "Edit the details view Groovy file.");
			// broken: http://jira.codehaus.org/browse/GROOVY-2790
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.getPreferencesDialog().editDetailsFormatter();
		}
	}

}
