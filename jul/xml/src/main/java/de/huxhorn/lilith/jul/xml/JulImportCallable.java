package de.huxhorn.lilith.jul.xml;

import de.huxhorn.lilith.data.eventsource.EventIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.sulky.tasks.AbstractProgressingCallable;

import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class JulImportCallable
	extends AbstractProgressingCallable<Long>
{
	private final Logger logger = LoggerFactory.getLogger(JulImportCallable.class);

	public static final String CLOSING_LOG4J_EVENT_TAG = "</log4j:event>";
	public static final String LOG4J_NAMESPACE = "xmlns:log4j=\"http://jakarta.apache.org/log4j/\"";
	public static final String OPENING_LOG4J_EVENT_TAG_EXCL_NS = "<log4j:event ";
	public static final String OPENING_LOG4J_EVENT_TAG_INCL_NS = OPENING_LOG4J_EVENT_TAG_EXCL_NS + LOG4J_NAMESPACE + " ";

	private File inputFile;
	private AppendOperation<EventWrapper<LoggingEvent>> buffer;
	private LoggingEventReader instance;
	private long result;

	public JulImportCallable(File inputFile, AppendOperation<EventWrapper<LoggingEvent>> buffer)
	{
		this.buffer = buffer;
		this.inputFile = inputFile;
		instance = new LoggingEventReader();
	}

	public AppendOperation<EventWrapper<LoggingEvent>> getBuffer()
	{
		return buffer;
	}

	public File getInputFile()
	{
		return inputFile;
	}

	public Long call()
		throws Exception
	{
		if(!inputFile.isFile())
		{
			throw new IllegalArgumentException("'" + inputFile.getAbsolutePath() + "' is not a file!");
		}
		if(!inputFile.canRead())
		{
			throw new IllegalArgumentException("'" + inputFile.getAbsolutePath() + "' is not a readable!");
		}
		long fileSize = inputFile.length();
		setNumberOfSteps(fileSize);
		FileInputStream fis = new FileInputStream(inputFile);

		CountingInputStream cis = new CountingInputStream(fis);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader = inputFactory.createXMLStreamReader(new InputStreamReader(cis, "utf-8"));
		for(; ;)
		{
			try
			{
				LoggingEvent event = instance.read(reader);
				setCurrentStep(cis.getByteCount());
				if(event == null)
				{
					break;
				}
				result++;
				EventWrapper<LoggingEvent> wrapper = new EventWrapper<LoggingEvent>();
				wrapper.setEvent(event);
				SourceIdentifier sourceIdentifier = new SourceIdentifier(inputFile.getAbsolutePath());
				EventIdentifier eventId = new EventIdentifier(sourceIdentifier, result);
				wrapper.setEventIdentifier(eventId);
				buffer.add(wrapper);
			}
			catch(XMLStreamException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while importing...", ex);
			}
		}
		return result;
	}

}
