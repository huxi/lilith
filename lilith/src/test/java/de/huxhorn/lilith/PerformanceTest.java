package de.huxhorn.lilith;

import de.huxhorn.lilith.data.eventsource.EventIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.xml.LoggingXmlDeserializer;
import de.huxhorn.lilith.data.logging.xml.LoggingXmlSerializer;
import de.huxhorn.lilith.engine.FileConstants;
import de.huxhorn.sulky.buffers.ExtendedSerializingFileBuffer;
import de.huxhorn.sulky.formatting.HumanReadable;
import de.huxhorn.sulky.generics.io.Deserializer;
import de.huxhorn.sulky.generics.io.SerializableDeserializer;
import de.huxhorn.sulky.generics.io.SerializableSerializer;
import de.huxhorn.sulky.generics.io.Serializer;
import de.huxhorn.sulky.generics.io.XmlDeserializer;
import de.huxhorn.sulky.generics.io.XmlSerializer;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Ignore("Activate if you want to benchmark...")
public class PerformanceTest
{
	private final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);
	private static final int AMOUNT = 1000;
	private static List<EventWrapper<LoggingEvent>> loggingEvents;
	private static Integer magicValue;

	private File tempOutputPath;
	private File dataFile;
	private File indexFile;
	private long time;

	public enum TestFormat
	{
		SERIALIZATION,
		JAVA_UTIL_XML,
		LILITH_XML
	}

	@BeforeClass
	public static void setUpOnce()
	{
		magicValue = 0xDEADBEEF;
		loggingEvents = new ArrayList<EventWrapper<LoggingEvent>>();
		for(int i = 0; i < AMOUNT; i++)
		{
			SourceIdentifier sourceIdentifier = new SourceIdentifier("identifier", "secondIdentifier");
			EventIdentifier eventIdentifier = new EventIdentifier(sourceIdentifier, i);
			loggingEvents.add(new EventWrapper<LoggingEvent>(eventIdentifier, createLoggingEvent(i)));
		}
	}

	@Before
	public void setUp()
		throws IOException
	{
		tempOutputPath = File.createTempFile("performance-testing", "rulez");
		tempOutputPath.delete();
		tempOutputPath.mkdirs();
		dataFile = new File(tempOutputPath, "dump");
		indexFile = new File(tempOutputPath, "dump.index");
	}

	@After
	public void tearDown()
		throws Exception
	{
		dataFile.delete();
		indexFile.delete();
		tempOutputPath.delete();
	}

	@Test
	public void lilithXmlCompressed()
	{
		boolean compressed = true;
		Serializer<LoggingEvent> serializer = new LoggingXmlSerializer(compressed);
		Deserializer<LoggingEvent> deserializer = new LoggingXmlDeserializer(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = serializer.serialize(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopSerializerTest("lilithXmlCompressedSerializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = deserializer.deserialize(current);
			dummy += event.hashCode();
		}
		stopSerializerTest("lilithXmlCompressedDeserializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void lilithXmlUncompressed()
	{
		boolean compressed = false;
		Serializer<LoggingEvent> serializer = new LoggingXmlSerializer(compressed);
		Deserializer<LoggingEvent> deserializer = new LoggingXmlDeserializer(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = serializer.serialize(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopSerializerTest("lilithXmlUncompressedSerializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = deserializer.deserialize(current);
			dummy += event.hashCode();
		}
		stopSerializerTest("lilithXmlUncompressedDeserializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void javaUtilXmlCompressed()
	{
		boolean compressed = true;
		Serializer<LoggingEvent> serializer = new XmlSerializer<LoggingEvent>(compressed, LoggingEvent.Level.class);
		Deserializer<LoggingEvent> deserializer = new XmlDeserializer<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = serializer.serialize(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopSerializerTest("javaBeansXmlCompressedSerializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = deserializer.deserialize(current);
			dummy += event.hashCode();
		}
		stopSerializerTest("javaBeansXmlCompressedDeserializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void javaUtilXmlUncompressed()
	{
		boolean compressed = false;
		Serializer<LoggingEvent> serializer = new XmlSerializer<LoggingEvent>(compressed, LoggingEvent.Level.class);
		Deserializer<LoggingEvent> deserializer = new XmlDeserializer<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = serializer.serialize(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopSerializerTest("javaBeansXmlUncompressedSerializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = deserializer.deserialize(current);
			dummy += event.hashCode();
		}
		stopSerializerTest("javaBeansXmlUncompressedDeserializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void serializationCompressed()
	{
		boolean compressed = true;
		Serializer<LoggingEvent> serializer = new SerializableSerializer<LoggingEvent>(compressed);
		Deserializer<LoggingEvent> deserializer = new SerializableDeserializer<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = serializer.serialize(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopSerializerTest("serializationCompressedSerializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = deserializer.deserialize(current);
			dummy += event.hashCode();
		}
		stopSerializerTest("serializationCompressedDeserializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void serializationUncompressed()
	{
		boolean compressed = false;
		Serializer<LoggingEvent> serializer = new SerializableSerializer<LoggingEvent>(compressed);
		Deserializer<LoggingEvent> deserializer = new SerializableDeserializer<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = serializer.serialize(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopSerializerTest("serializationUncompressedSerializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = deserializer.deserialize(current);
			dummy += event.hashCode();
		}
		stopSerializerTest("serializationUncompressedDeserializer", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void xmlNoCompressionAdd()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, false);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("xmlNoCompressionAdd");
	}

	@Test
	public void xmlCompressionAdd()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, true);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("xmlCompressionAdd");
	}


	@Test
	public void serializationNoCompressionAdd()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, false);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("serializationNoCompressionAdd");
	}

	@Test
	public void serializationCompressionAdd()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, true);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("serializationCompressionAdd");
	}

	// ###

	@Test
	public void xmlNoCompressionAddAll()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, false);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("xmlNoCompressionAddAll");
	}

	@Test
	public void xmlCompressionAddAll()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, true);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("xmlCompressionAddAll");
	}


	@Test
	public void serializationNoCompressionAddAll()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, false);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("serializationNoCompressionAddAll");
	}

	@Test
	public void serializationCompressionAddAll()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, true);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("serializationCompressionAddAll");
	}

	// ###

	@Test
	public void xmlNoCompressionGet()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, false);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("xmlNoCompressionGet");
	}

	@Test
	public void xmlCompressionGet()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, true);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("xmlCompressionGet");
	}


	@Test
	public void serializationNoCompressionGet()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, false);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("serializationNoCompressionGet");
	}

	@Test
	public void serializationCompressionGet()
	{
		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, true);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("serializationCompressionGet");
	}

	private void startTest()
	{
		time = System.nanoTime();
	}

	private void stopTest(String name)
	{
		long expired = System.nanoTime() - time;
		double fraction = (double) expired / 1000000000;
		if(logger.isInfoEnabled()) logger.info("{}: expired={}s", name, fraction);
		long length = dataFile.length();
		if(logger.isInfoEnabled()) logger.info("{}: dataFileSize={} bytes", name, length);
		long eventAverage = length / AMOUNT;

		String formattedAverage = HumanReadable.getHumanReadableSize(eventAverage, true, false) + "bytes";

		if(logger.isInfoEnabled()) logger.info("{}: average={}b/event", name, formattedAverage);

		if(logger.isInfoEnabled())
		{
			logger.info("|| {} || {} || {} || {} ||", new Object[]{name, fraction, length, formattedAverage});
		}
	}

	private void stopSerializerTest(String name, long length)
	{
		long expired = System.nanoTime() - time;
		double fraction = (double) expired / 1000000000;
		if(logger.isInfoEnabled()) logger.info("{}: expired={}s", name, fraction);
		long eventAverage = length / AMOUNT;

		String formattedAverage = HumanReadable.getHumanReadableSize(eventAverage, true, false) + "bytes";

		if(logger.isInfoEnabled()) logger.info("{}: average={}b/event", name, formattedAverage);

		if(logger.isInfoEnabled())
		{
			logger.info("|| {} || {} || {} || {} ||", new Object[]{name, fraction, length, formattedAverage});
		}
	}

	private ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> createFileBuffer(TestFormat format, boolean compressing)
	{
		Map<String, String> metaData = new HashMap<String, String>();

		if(format == TestFormat.JAVA_UTIL_XML)
		{
			metaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_JAVA_BEANS_XML);
		}
		else if(format == TestFormat.LILITH_XML)
		{
			metaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_LILITH_XML);
		}
		else
		{
			metaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_SERIALIZED);
		}

		if(compressing)
		{
			metaData.put(FileConstants.COMPRESSED_KEY, "true");
		}
		else
		{
			metaData.put(FileConstants.COMPRESSED_KEY, "false");
		}

		ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>> result = new ExtendedSerializingFileBuffer<EventWrapper<LoggingEvent>>(magicValue, metaData, null, null, dataFile, indexFile);

		Map<String, String> actualMetaData = result.getMetaData();

		boolean compressed = false;

		String formatStr = null;
		if(actualMetaData != null)
		{
			compressed = Boolean.valueOf(actualMetaData.get(FileConstants.COMPRESSED_KEY));
			formatStr = actualMetaData.get(FileConstants.CONTENT_FORMAT_KEY);
		}
		Serializer<EventWrapper<LoggingEvent>> serializer;
		Deserializer<EventWrapper<LoggingEvent>> deserializer;
		if(FileConstants.CONTENT_FORMAT_VALUE_JAVA_BEANS_XML.equals(formatStr))
		{
			serializer = new XmlSerializer<EventWrapper<LoggingEvent>>(compressed, LoggingEvent.Level.class);
			deserializer = new XmlDeserializer<EventWrapper<LoggingEvent>>(compressed);
		}
//		else if(FileConstants.CONTENT_FORMAT_VALUE_LILITH_XML.equals(formatStr))
//		{
//			serializer = new LoggingXmlSerializer(compressed);
//			deserializer = new LoggingXmlDeserializer(compressed);
//		}
		else
		{
			serializer = new SerializableSerializer<EventWrapper<LoggingEvent>>(compressed);
			deserializer = new SerializableDeserializer<EventWrapper<LoggingEvent>>(compressed);
		}
		result.setSerializer(serializer);
		result.setDeserializer(deserializer);

		if(logger.isDebugEnabled()) logger.debug("Created file buffer: {}", result);
		return result;
	}

	private static LoggingEvent createLoggingEvent(int counter)
	{

		LoggingEvent result = new LoggingEvent();
		result.setApplicationIdentifier("ApplicationIdentifier-" + counter);
		result.setArguments(new String[]{"param1-" + counter, "param2-" + counter});
		result.setMessagePattern("messagePattern-" + counter + ": {} {}");
		result.setLevel(LoggingEvent.Level.INFO);
		result.setLogger("logger-" + counter);
		result.setMarker(new Marker("marker-" + counter));
		Map<String, String> mdc = new HashMap<String, String>();
		mdc.put("key-" + counter, "value-" + counter);
		result.setMdc(mdc);
		result
			.setNdc(new Message[]{new Message("ndcMessagePattern-" + counter + ": {} {}", new String[]{"ndcParam1-" + counter, "ndcParam2-" + counter})});
		result.setThreadName("threadName-" + counter);
		ExtendedStackTraceElement[] callStack = createCallStack();
		result.setCallStack(callStack);
		ThrowableInfo throwableInfo = createThrowableInfo();
		result.setThrowable(throwableInfo);
		result.setTimeStamp(new Date());
		return result;
	}

	private static ExtendedStackTraceElement[] createCallStack()
	{
		ExtendedStackTraceElement[] result = new ExtendedStackTraceElement[5];
		for(int i = 0; i < result.length; i++)
		{
			result[i] = createSTE(i);
		}
		return result;
	}

	private static ExtendedStackTraceElement createSTE(int counter)
	{
		ExtendedStackTraceElement ste = new ExtendedStackTraceElement();
		ste.setClassName("className-" + counter);
		ste.setCodeLocation("codeLocation-" + counter);
		ste.setExact(counter % 2 == 0);
		ste.setFileName("fileName-" + counter);
		ste.setLineNumber(17 + counter);
		ste.setMethodName("methodName-" + counter);
		ste.setVersion("version-" + counter);
		return ste;
	}

	private static ThrowableInfo createThrowableInfo()
	{

		ThrowableInfo result = new ThrowableInfo();
		result.setMessage("throwableMessage");
		result.setName("throwableName");
		result.setStackTrace(createCallStack());
		result.setOmittedElements(17);
		return result;
	}
}
