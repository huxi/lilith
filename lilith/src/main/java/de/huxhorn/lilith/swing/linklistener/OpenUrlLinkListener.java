/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2014 Joern Huxhorn
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
package de.huxhorn.lilith.swing.linklistener;

import de.huxhorn.lilith.swing.MainFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.LinkListener;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class OpenUrlLinkListener
	extends LinkListener
{
	private final Logger logger = LoggerFactory.getLogger(OpenUrlLinkListener.class);
	public static final String HELP_URI_PREFIX = "help://";
	public static final String PREFS_URI_PREFIX = "prefs://";

	private MainFrame mainFrame;
	private LinkListener originalLinkListener;

	public OpenUrlLinkListener(MainFrame mainFrame, LinkListener originalLinkListener)
	{
		this.mainFrame = mainFrame;
		this.originalLinkListener = originalLinkListener;
	}

	@Override
	public void linkClicked(BasicPanel basicPanel, String uri)
	{
		if(logger.isDebugEnabled()) logger.debug("Link clicked: {}", uri);

		if(uri.startsWith(HELP_URI_PREFIX))
		{
			mainFrame.openHelp(uri.substring(HELP_URI_PREFIX.length()));
		}
		else if(uri.startsWith(PREFS_URI_PREFIX))
		{
			mainFrame.openPreferences(uri.substring(PREFS_URI_PREFIX.length()));
		}
		else if(uri.contains("://"))
		{
			try
			{
				MainFrame.openUrl(new URL(uri));
			}
			catch(MalformedURLException e)
			{
				if(logger.isInfoEnabled()) logger.info("Couldn't create URL for uri-string {}!", uri, e);
			}
		}
		else if(uri.contains("coin:"))
		{
			try
			{
				MainFrame.openUri(new URI(uri));
			}
			catch(URISyntaxException e)
			{
				if(logger.isInfoEnabled()) logger.info("Couldn't create URI for uri-string {}!", uri, e);
			}
		}
		else if(originalLinkListener != null)
		{
			originalLinkListener.linkClicked(basicPanel, uri);
		}
	}
}
