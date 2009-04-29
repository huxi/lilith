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
package de.huxhorn.lilith.swing.debug;

import de.huxhorn.lilith.debug.DebugProgressingCallable;
import de.huxhorn.lilith.debug.LoggerEventEmitter;
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

		JToolBar debugToolbar = new JToolBar();
		debugToolbar.setFloatable(false);

		JButton button;
		Action action;

		action = new LogStuffAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new LogExceptionsAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new LogParamExceptionsAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new LogSkullAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new LogTruthAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new LogAnonymousAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new LogNDCAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new LogDateAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new LogAllAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new EditGroovyAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new NetworkAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new DebugCallableAction();
		button = new JButton(action);
		debugToolbar.add(button);

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(debugToolbar, BorderLayout.NORTH);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
	}

	private class DoneAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7747612911180730271L;

		public DoneAction()
		{
			super("Done!");
		}

		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}
	}

	private class LogAllAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -5004276984975201630L;

		public LogAllAction()
		{
			super("Log all!");
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

	private class EditGroovyAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8812061542734868784L;

		public EditGroovyAction()
		{
			super("Edit groovy!");
			// broken: http://jira.codehaus.org/browse/GROOVY-2790
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.getPreferencesDialog().editDetailsFormatter();
		}
	}

	private class NetworkAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3099597544720236257L;

		public NetworkAction()
		{
			super("Network");
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

	private class LogStuffAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3191463140774402016L;

		public LogStuffAction()
		{
			super("Log stuff");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logStuff();
			loggerEventEmitter.logStuffWithMarker();
			loggerEventEmitter.logStuffWithMdc();
			loggerEventEmitter.logStuffWithMdcAndMarker();
		}
	}

	private class LogDateAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -1734237014776105342L;

		public LogDateAction()
		{
			super("Log date");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logDate();
		}
	}

	private class LogNDCAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -518958063141973150L;

		public LogNDCAction()
		{
			super("Log NDC");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logNDC();
		}
	}


	private class LogExceptionsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -2289262066339501111L;

		public LogExceptionsAction()
		{
			super("Log exceptions");
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
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logParamException();
			loggerEventEmitter.logParamException2();
		}
	}

	private class LogSkullAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3162155864945849819L;

		public LogSkullAction()
		{
			super("Log skull");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logSkull();
		}
	}

	private class LogTruthAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7354728704746203904L;

		public LogTruthAction()
		{
			super("Log truth");
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
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logAnonymous();
		}
	}

	private class DebugCallableAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -97047951966954750L;

		public DebugCallableAction()
		{
			super("Callable");
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.getLongWorkManager()
				.startTask(new DebugProgressingCallable(), "Test Task", "This is just a simply test task.");
		}
	}
}
