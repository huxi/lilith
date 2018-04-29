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

package de.huxhorn.lilith.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

class HtmlTransferable
	implements Transferable
{

	public static final DataFlavor XHTML_FLAVOR = new DataFlavor("application/xhtml+xml; charset=\"UTF-8\"", "HTML");

	// HTML clipping is horribly broken in Java.
	// See
	// http://www.peterbuettner.de/develop/javasnippets/clipHtml/index.html
	// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6392086
	// for details...
	//public static final DataFlavor HTML_FLAVOR=new DataFlavor("text/html; charset=\"UTF-8\"", "HTML");
	public static final DataFlavor PLAIN_TEXT_FLAVOR = new DataFlavor("text/plain; charset=\"UTF-8\"", "UTF-8 Text");

	private static final DataFlavor[] FLAVORS = new DataFlavor[]
		{
			XHTML_FLAVOR,
			//HTML_FLAVOR,
			PLAIN_TEXT_FLAVOR,
		};

	private final String html;
	private final byte[] data;

	HtmlTransferable(String html)
	{
		this.html = html;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);
			osw.append(html);
			osw.flush();
			osw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace(); // NOPMD
		}
		data = bos.toByteArray();
	}

	public String getHtml()
	{
		return html;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return FLAVORS.clone();
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		for(DataFlavor aFlavor : FLAVORS)
		{
			if(flavor.equals(aFlavor))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
		throws UnsupportedFlavorException, IOException
	{
		if(flavor.equals(XHTML_FLAVOR) || /* flavor.equals(HTML_FLAVOR) || */ flavor.equals(PLAIN_TEXT_FLAVOR))
		{
			return new ByteArrayInputStream(data);
		}
		else
		{
			throw new UnsupportedFlavorException(flavor);
		}
	}
}
