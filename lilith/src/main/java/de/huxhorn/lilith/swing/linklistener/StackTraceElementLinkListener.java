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
package de.huxhorn.lilith.swing.linklistener;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.sulky.formatting.SimpleXml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.LinkListener;

public class StackTraceElementLinkListener
	extends LinkListener
{
	private final Logger logger = LoggerFactory.getLogger(OpenUrlLinkListener.class);
	public static final String STACK_TRACE_ELEMENT_URI_PREFIX = "ste://";

	private MainFrame mainFrame;

	public StackTraceElementLinkListener(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
	}

	@Override
	public void linkClicked(BasicPanel basicPanel, String uri)
	{
		if(logger.isDebugEnabled()) logger.debug("Link clicked: {}", uri);
		if(uri.startsWith(STACK_TRACE_ELEMENT_URI_PREFIX))
		{
			String steStr = uri.substring(STACK_TRACE_ELEMENT_URI_PREFIX.length());
			steStr = SimpleXml.unescape(steStr);
			ExtendedStackTraceElement ste = ExtendedStackTraceElement.parseStackTraceElement(steStr);
			if(logger.isDebugEnabled()) logger.debug("STE: {}", ste);
			mainFrame.goToSource(ste.getStackTraceElement());
		}
	}
}
