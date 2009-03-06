package de.huxhorn.lilith;

import de.huxhorn.lilith.data.eventsource.EventIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.protobuf.LoggingEventProtobufDecoder;
import de.huxhorn.lilith.data.logging.protobuf.LoggingEventProtobufEncoder;
import de.huxhorn.lilith.data.logging.protobuf.LoggingEventWrapperProtobufCodec;
import de.huxhorn.lilith.data.logging.xml.LoggingXmlDecoder;
import de.huxhorn.lilith.data.logging.xml.LoggingXmlEncoder;
import de.huxhorn.lilith.engine.FileConstants;
import de.huxhorn.lilith.engine.impl.LoggingEventWrapperXmlCodec;
import de.huxhorn.sulky.codec.Codec;
import de.huxhorn.sulky.codec.Decoder;
import de.huxhorn.sulky.codec.Encoder;
import de.huxhorn.sulky.codec.SerializableCodec;
import de.huxhorn.sulky.codec.SerializableDecoder;
import de.huxhorn.sulky.codec.SerializableEncoder;
import de.huxhorn.sulky.codec.XmlDecoder;
import de.huxhorn.sulky.codec.XmlEncoder;
import de.huxhorn.sulky.formatting.HumanReadable;
import de.huxhorn.sulky.codec.filebuffer.CodecFileBuffer;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Ignore("Activate if you want to benchmark...")
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
		PROTOBUF
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

	// ###

	@Test
	public void protobufUncompressed()
	{
		boolean compressed = false;
		Encoder<LoggingEvent> encoder = new LoggingEventProtobufEncoder(compressed);
		Decoder<LoggingEvent> decoder = new LoggingEventProtobufDecoder(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("protobufUncompressed", "Encoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("protobufUncompressed", "Decoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void protobufCompressed()
	{
		boolean compressed = true;
		Encoder<LoggingEvent> encoder = new LoggingEventProtobufEncoder(compressed);
		Decoder<LoggingEvent> decoder = new LoggingEventProtobufDecoder(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("protobufCompressed", "Encoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("protobufCompressed", "Decoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	@Test
	public void lilithXmlUncompressed()
	{
		boolean compressed = false;
		Encoder<LoggingEvent> encoder = new LoggingXmlEncoder(compressed);
		Decoder<LoggingEvent> decoder = new LoggingXmlDecoder(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("lilithXmlUncompressed", "Encoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("lilithXmlUncompressed", "Decoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void lilithXmlCompressed()
	{
		boolean compressed = true;
		Encoder<LoggingEvent> encoder = new LoggingXmlEncoder(compressed);
		Decoder<LoggingEvent> decoder = new LoggingXmlDecoder(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("lilithXmlCompressed", "Encoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("lilithXmlCompressed", "Decoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	@Test
	public void javaUtilXmlUncompressed()
	{
		boolean compressed = false;
		Encoder<LoggingEvent> encoder = new XmlEncoder<LoggingEvent>(compressed, LoggingEvent.Level.class);
		Decoder<LoggingEvent> decoder = new XmlDecoder<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("javaBeansXmlUncompressed", "Encoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("javaBeansXmlUncompressed", "Decoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void javaUtilXmlCompressed()
	{
		boolean compressed = true;
		Encoder<LoggingEvent> encoder = new XmlEncoder<LoggingEvent>(compressed, LoggingEvent.Level.class);
		Decoder<LoggingEvent> decoder = new XmlDecoder<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("javaBeansXmlCompressed", "Encoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("javaBeansXmlCompressed", "Decoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	@Test
	public void serializationUncompressed()
	{
		boolean compressed = false;
		Encoder<LoggingEvent> encoder = new SerializableEncoder<LoggingEvent>(compressed);
		Decoder<LoggingEvent> decoder = new SerializableDecoder<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("serializationUncompressed", "Encoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("serializationUncompressed", "Decoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	@Test
	public void serializationCompressed()
	{
		boolean compressed = true;
		Encoder<LoggingEvent> encoder = new SerializableEncoder<LoggingEvent>(compressed);
		Decoder<LoggingEvent> decoder = new SerializableDecoder<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(AMOUNT);
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("serializationCompressed", "Encoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("serializationCompressed", "Decoder", byteCounter);
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	@Test
	public void streamingSerialization()
		throws IOException, ClassNotFoundException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			oos.writeObject(current);
		}
		oos.flush();
		oos.close();
		byte[] bytes = bos.toByteArray();
		stopTest("streamingSerialization", "Write", bytes.length);
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", bytes.length);

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		long dummy = 0;

		startTest();
		for(int i = 0; i < AMOUNT; i++)
		{
			Object obj = ois.readObject();
			dummy += obj.hashCode();
		}
		stopTest("streamingSerialization", "Read", bytes.length);
		ois.close();
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	@Test
	public void protobufUncompressedAdd()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, false);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("protobufUncompressed", "add");
	}

	@Test
	public void protobufUncompressedAddAll()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, false);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("protobufUncompressed", "addAll");
	}

	@Test
	public void protobufUncompressedGet()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, false);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("protobufUncompressed", "get");
	}

	// ###

	@Test
	public void protobufCompressedAdd()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, true);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("protobufCompressed", "add");
	}

	@Test
	public void protobufCompressedAddAll()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, true);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("protobufCompressed", "addAll");
	}

	@Test
	public void protobufCompressedGet()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, true);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("protobufCompressed", "get");
	}

	// ###

	@Test
	public void xmlUncompressedAdd()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, false);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("xmlUncompressed", "add");
	}

	@Test
	public void xmlUncompressedAddAll()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, false);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("xmlUncompressed", "addAll");
	}

	@Test
	public void xmlUncompressedGet()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, false);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("xmlUncompressed", "get");
	}

	// ###

	@Test
	public void xmlCompressedAdd()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, true);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("xmlCompressed", "add");
	}

	@Test
	public void xmlCompressedAddAll()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, true);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("xmlCompressed", "addAll");
	}

	@Test
	public void xmlCompressedGet()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, true);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("xmlCompressed", "get");
	}

	// ###

	@Test
	public void serializationUncompressedAdd()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, false);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("serializationUncompressed", "add");
	}

	@Test
	public void serializationUncompressedAddAll()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, false);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("serializationUncompressed", "addAll");
	}

	@Test
	public void serializationUncompressedGet()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, false);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("serializationUncompressed", "get");
	}

	// ###

	@Test
	public void serializationCompressedAdd()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, true);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("serializationCompressed", "add");
	}

	@Test
	public void serializationCompressedAddAll()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, true);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("serializationCompressed", "addAll");
	}

	@Test
	public void serializationCompressedGet()
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, true);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assertEquals(loggingEvents.size(), size);
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("serializationCompressed", "get");
	}

	// ###


	private void startTest()
	{
		time = System.nanoTime();
	}

	private void stopTest(String name, String action)
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
			logger
				.info("|| {} || {} || {} || {} || {} ||", new Object[]{name, action, fraction, length, formattedAverage});
		}
	}

	private void stopTest(String name, String action, long length)
	{
		long expired = System.nanoTime() - time;
		double fraction = (double) expired / 1000000000;
		if(logger.isInfoEnabled()) logger.info("{}: expired={}s", name, fraction);
		long eventAverage = length / AMOUNT;

		String formattedAverage = HumanReadable.getHumanReadableSize(eventAverage, true, false) + "bytes";

		if(logger.isInfoEnabled()) logger.info("{}: average={}b/event", name, formattedAverage);

		if(logger.isInfoEnabled())
		{
			logger
				.info("|| {} || {} || {} || {} || {} ||", new Object[]{name, action, fraction, length, formattedAverage});
		}
	}

	private CodecFileBuffer<EventWrapper<LoggingEvent>> createFileBuffer(TestFormat format, boolean compressing)
	{
		Map<String, String> metaData = new HashMap<String, String>();

		if(format == TestFormat.JAVA_UTIL_XML)
		{
			metaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_JAVA_BEANS_XML);
		}
		else if(format == TestFormat.PROTOBUF)
		{
			metaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
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

		CodecFileBuffer<EventWrapper<LoggingEvent>> result = new CodecFileBuffer<EventWrapper<LoggingEvent>>(magicValue, metaData, null, dataFile, indexFile);

		Map<String, String> actualMetaData = result.getMetaData();

		boolean compressed = false;

		String formatStr = null;
		if(actualMetaData != null)
		{
			compressed = Boolean.valueOf(actualMetaData.get(FileConstants.COMPRESSED_KEY));
			formatStr = actualMetaData.get(FileConstants.CONTENT_FORMAT_KEY);
		}

		Codec<EventWrapper<LoggingEvent>> codec;
		if(FileConstants.CONTENT_FORMAT_VALUE_JAVA_BEANS_XML.equals(formatStr))
		{
			codec = new LoggingEventWrapperXmlCodec(compressed);
		}
		else if(FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF.equals(formatStr))
		{
			codec = new LoggingEventWrapperProtobufCodec(compressed);
		}
		else
		{
			codec = new SerializableCodec<EventWrapper<LoggingEvent>>(compressed);
		}
		result.setCodec(codec);

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
		ExtendedStackTraceElement[] originalCallStack = createCallStack();
		ExtendedStackTraceElement[] callStack = new ExtendedStackTraceElement[originalCallStack.length * 5];
		for(int i = 0; i < callStack.length; i++)
		{
			ExtendedStackTraceElement original = originalCallStack[i % originalCallStack.length];
			ExtendedStackTraceElement actual = new ExtendedStackTraceElement();
			if(original.getClassName() != null)
			{
				actual.setClassName(original.getClassName() + i);
			}
			if(original.getFileName() != null)
			{
				actual.setFileName(original.getFileName() + i);
			}
			if(original.getMethodName() != null)
			{
				actual.setMethodName(original.getMethodName() + i);
			}
			if(original.getCodeLocation() != null)
			{
				actual.setCodeLocation(original.getCodeLocation() + i);
			}
			if(original.getVersion() != null)
			{
				actual.setVersion(original.getVersion() + i);
			}
			actual.setLineNumber(original.getLineNumber());
			actual.setExact(original.isExact());

			callStack[i] = actual;
		}
		result.setCallStack(callStack);
		ThrowableInfo throwableInfo = createThrowableInfo();
		result.setThrowable(throwableInfo);
		result.setTimeStamp(new Date(1234567890L));
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
