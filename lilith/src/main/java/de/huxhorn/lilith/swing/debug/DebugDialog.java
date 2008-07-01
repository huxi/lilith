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
package de.huxhorn.lilith.swing.debug;

import de.huxhorn.lilith.debug.LoggerEventEmitter;
import de.huxhorn.lilith.swing.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Action;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;

public class DebugDialog
	extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(DebugDialog.class);

	LoggerEventEmitter loggerEventEmitter;
	private MainFrame mainFrame;

	public DebugDialog(Frame owner, MainFrame mainFrame)
	{
		super(owner, "Debug");
		this.mainFrame=mainFrame;
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
		loggerEventEmitter=new LoggerEventEmitter();
		loggerEventEmitter.setDelay(0);
		JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(new DoneAction()));

		JToolBar debugToolbar=new JToolBar();
		debugToolbar.setFloatable(false);

		JButton button;
		Action action;

		action = new LogStuffAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action = new LogExceptionsAction();
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

		action = new LogAllAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action=new EditGroovyAction();
		button = new JButton(action);
		debugToolbar.add(button);

		action=new NetworkAction();
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
		public LogAllAction()
		{
			super("Log all!");
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				loggerEventEmitter.logStuff();
				loggerEventEmitter.logStuffWithMarker();
				loggerEventEmitter.logStuffWithMdc();
				loggerEventEmitter.logStuffWithMdcAndMarker();
				loggerEventEmitter.logException();
				loggerEventEmitter.logException2();
				loggerEventEmitter.logSkull();
				loggerEventEmitter.logTruth();
				loggerEventEmitter.logAnonymous();
			}
			catch (InterruptedException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Interrupted debug action...", ex);
			}
		}
	}

	private class EditGroovyAction
		extends AbstractAction
	{
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
		public NetworkAction()
		{
			super("Network");
		}

		public void actionPerformed(ActionEvent e)
		{
			Set<InetAddress> inetAddresses=new HashSet<InetAddress>();
			try
			{
				Enumeration<NetworkInterface> netIfcs = NetworkInterface.getNetworkInterfaces();

				while(netIfcs.hasMoreElements())
				{
					NetworkInterface ni=netIfcs.nextElement();
					Enumeration<InetAddress> inetAddrs = ni.getInetAddresses();
					while(inetAddrs.hasMoreElements())
					{
						InetAddress iadd=inetAddrs.nextElement();
						if(!iadd.isLoopbackAddress())
						{
							inetAddresses.add(iadd);
						}
					}

				}
			}
			catch (SocketException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while retrieving InetAddresses!", ex);
			}
			if(logger.isInfoEnabled()) logger.info("InetAddresses: {}", inetAddresses);
		}
	}

	private class LogStuffAction
		extends AbstractAction
	{
		public LogStuffAction()
		{
			super("Log stuff");
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				loggerEventEmitter.logStuff();
				loggerEventEmitter.logStuffWithMarker();
				loggerEventEmitter.logStuffWithMdc();
				loggerEventEmitter.logStuffWithMdcAndMarker();
			}
			catch (InterruptedException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Interrupted debug action...", ex);
			}
		}
	}

	private class LogExceptionsAction
		extends AbstractAction
	{
		public LogExceptionsAction()
		{
			super("Log exceptions");
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				loggerEventEmitter.logException();
				loggerEventEmitter.logException2();
			}
			catch (InterruptedException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Interrupted debug action...", ex);
			}
		}
	}

	private class LogSkullAction
		extends AbstractAction
	{
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
		public LogAnonymousAction()
		{
			super("Log anonymous");
		}

		public void actionPerformed(ActionEvent e)
		{
			loggerEventEmitter.logAnonymous();
		}
	}
}
