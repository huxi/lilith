package de.huxhorn.lilith.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

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
			PLAIN_TEXT_FLAVOR
		};

	private String html;
	private byte[] data;

	public HtmlTransferable(String html)
	{
		this.html = html;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
			osw.append(html);
			osw.flush();
			osw.close();
		}
		catch(UnsupportedEncodingException e)
		{
// TODO: change body of catch statement
			e.printStackTrace();
		}
		catch(IOException e)
		{
// TODO: change body of catch statement
			e.printStackTrace();
		}
		data = bos.toByteArray();
	}

	public String getHtml()
	{
		return html;
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return FLAVORS.clone();
	}

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
