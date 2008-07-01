package de.huxhorn.lilith.swing.linklistener;

import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.swing.BasicPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.huxhorn.lilith.swing.MainFrame;

import java.net.URL;
import java.net.MalformedURLException;

public class OpenUrlLinkListener
		extends LinkListener
{
	private final Logger logger = LoggerFactory.getLogger(OpenUrlLinkListener.class);
	public static final String STACK_TRACE_ELEMENT_URI_PREFIX = "ste://";

	private MainFrame mainFrame;
	private LinkListener originalLinkListener;

	public OpenUrlLinkListener(MainFrame mainFrame, LinkListener originalLinkListener)
	{
		this.mainFrame = mainFrame;
		this.originalLinkListener=originalLinkListener;
	}

	@Override
	public void linkClicked(BasicPanel basicPanel, String uri)
	{
		if (logger.isDebugEnabled()) logger.debug("Link clicked: {}", uri);
		if (uri.contains("://"))
		{
			try
			{
				mainFrame.openUrl(new URL(uri));
			}
			catch (MalformedURLException e)
			{
				if(logger.isInfoEnabled()) logger.info("Couldn't create URL for uri-string "+uri+"!", e);
			}
		}
		else if(originalLinkListener!=null)
		{
			originalLinkListener.linkClicked(basicPanel, uri);
		}
	}
}
