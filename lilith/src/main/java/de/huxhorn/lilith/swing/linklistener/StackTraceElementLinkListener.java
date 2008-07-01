package de.huxhorn.lilith.swing.linklistener;

import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.swing.BasicPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.huxhorn.sulky.formatting.SimpleXml;
import de.huxhorn.lilith.data.logging.logback.LogbackLoggingAdapter;
import de.huxhorn.lilith.swing.MainFrame;

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
		if (logger.isDebugEnabled()) logger.debug("Link clicked: {}", uri);
		if (uri.startsWith(STACK_TRACE_ELEMENT_URI_PREFIX))
		{
			String steStr = uri.substring(STACK_TRACE_ELEMENT_URI_PREFIX.length());
			steStr = SimpleXml.unescape(steStr);
			StackTraceElement ste = LogbackLoggingAdapter.parseStackTraceElement(steStr);
			if (logger.isInfoEnabled()) logger.info("STE: {}", ste);
			mainFrame.goToSource(ste);
		}
	}
}