package de.huxhorn.lilith.benchmark;

import ch.qos.logback.core.CoreConstants;
import de.huxhorn.lilith.api.FileConstants;
import de.huxhorn.lilith.data.eventsource.EventIdentifier;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.Marker;
import de.huxhorn.lilith.data.logging.Message;
import de.huxhorn.lilith.data.logging.ThreadInfo;
import de.huxhorn.lilith.data.logging.ThrowableInfo;
import de.huxhorn.lilith.data.logging.protobuf.CompressingLoggingEventWrapperProtobufCodec;
import de.huxhorn.lilith.data.logging.protobuf.LoggingEventProtobufDecoder;
import de.huxhorn.lilith.data.logging.protobuf.LoggingEventProtobufEncoder;
import de.huxhorn.lilith.data.logging.protobuf.LoggingEventWrapperProtobufCodec;
import de.huxhorn.lilith.data.logging.xml.LoggingXmlDecoder;
import de.huxhorn.lilith.data.logging.xml.LoggingXmlEncoder;
import de.huxhorn.lilith.engine.impl.CompressingLoggingEventWrapperXmlCodec;
import de.huxhorn.lilith.engine.impl.LoggingEventWrapperXmlCodec;
import de.huxhorn.sulky.codec.Codec;
import de.huxhorn.sulky.codec.CompressingSerializableCodec;
import de.huxhorn.sulky.codec.Decoder;
import de.huxhorn.sulky.codec.Encoder;
import de.huxhorn.sulky.codec.SerializableCodec;
import de.huxhorn.sulky.codec.SerializableDecoder;
import de.huxhorn.sulky.codec.SerializableEncoder;
import de.huxhorn.sulky.codec.XmlDecoder;
import de.huxhorn.sulky.codec.XmlEncoder;
import de.huxhorn.sulky.codec.filebuffer.CodecFileBuffer;
import de.huxhorn.sulky.codec.filebuffer.FileHeader;
import de.huxhorn.sulky.codec.filebuffer.MetaData;
import de.huxhorn.sulky.formatting.HumanReadable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Benchmark
{
	private final Logger logger = LoggerFactory.getLogger(Benchmark.class);
	private static final Integer magicValue = 0xDEADBEEF;

	private File tempOutputPath;
	private File dataFile;
	private File indexFile;
	private long time;
	private static boolean executeGcBetween = false;
	private static final String CONTENT_FORMAT_VALUE_JAVA_BEANS_XML = "java.beans.XML";
	private static final String CONTENT_FORMAT_VALUE_SERIALIZED = "java.io.Serializable";
	//private static final String CONTENT_FORMAT_VALUE_LILITH_XML = "Lilith XML";

	public enum TestFormat
	{
		SERIALIZATION,
		JAVA_UTIL_XML,
		//LILITH_XML,
		PROTOBUF
	}

	public static List<EventWrapper<LoggingEvent>> createDataSet(int amount)
	{
		ArrayList<EventWrapper<LoggingEvent>> loggingEvents = new ArrayList<EventWrapper<LoggingEvent>>();
		for(int i = 0; i < amount; i++)
		{
			SourceIdentifier sourceIdentifier = new SourceIdentifier("identifier", "secondIdentifier");
			EventIdentifier eventIdentifier = new EventIdentifier(sourceIdentifier, i);
			loggingEvents.add(new EventWrapper<LoggingEvent>(eventIdentifier, createLoggingEvent(i)));
		}
		return loggingEvents;
	}

	public void setUp()
		throws IOException
	{
		tempOutputPath = File.createTempFile("performance-testing", "rulez");
		tempOutputPath.delete();
		tempOutputPath.mkdirs();
		dataFile = new File(tempOutputPath, "dump");
		indexFile = new File(tempOutputPath, "dump.index");
	}

	public void tearDown()
		throws Exception
	{
		dataFile.delete();
		indexFile.delete();
		tempOutputPath.delete();
	}

	// ###

	public void protobufUncompressed(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		boolean compressed = false;
		Encoder<LoggingEvent> encoder = new LoggingEventProtobufEncoder(compressed);
		Decoder<LoggingEvent> decoder = new LoggingEventProtobufDecoder(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(loggingEvents.size());
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("protobufUncompressed", "encode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("protobufUncompressed", "decode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	public void protobufCompressed(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		boolean compressed = true;
		Encoder<LoggingEvent> encoder = new LoggingEventProtobufEncoder(compressed);
		Decoder<LoggingEvent> decoder = new LoggingEventProtobufDecoder(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(loggingEvents.size());
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("protobufCompressed", "encode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("protobufCompressed", "decode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	public void lilithXmlUncompressed(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		boolean compressed = false;
		Encoder<LoggingEvent> encoder = new LoggingXmlEncoder(compressed);
		Decoder<LoggingEvent> decoder = new LoggingXmlDecoder(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(loggingEvents.size());
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("lilithXmlUncompressed", "encode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("lilithXmlUncompressed", "decode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	public void lilithXmlCompressed(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		boolean compressed = true;
		Encoder<LoggingEvent> encoder = new LoggingXmlEncoder(compressed);
		Decoder<LoggingEvent> decoder = new LoggingXmlDecoder(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(loggingEvents.size());
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("lilithXmlCompressed", "encode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("lilithXmlCompressed", "decode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	public void javaUtilXmlUncompressed(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		boolean compressed = false;
		Encoder<LoggingEvent> encoder = new XmlEncoder<LoggingEvent>(compressed, LoggingEvent.Level.class);
		Decoder<LoggingEvent> decoder = new XmlDecoder<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(loggingEvents.size());
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("javaBeansXmlUncompressed", "encode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("javaBeansXmlUncompressed", "decode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	public void javaUtilXmlCompressed(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		boolean compressed = true;
		Encoder<LoggingEvent> encoder = new XmlEncoder<LoggingEvent>(compressed, LoggingEvent.Level.class);
		Decoder<LoggingEvent> decoder = new XmlDecoder<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(loggingEvents.size());
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("javaBeansXmlCompressed", "encode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("javaBeansXmlCompressed", "decode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	public void serializationUncompressed(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		boolean compressed = false;
		Encoder<LoggingEvent> encoder = new SerializableEncoder<LoggingEvent>(compressed);
		Decoder<LoggingEvent> decoder = new SerializableDecoder<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(loggingEvents.size());
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("serializationUncompressed", "encode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("serializationUncompressed", "decode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	public void serializationCompressed(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		boolean compressed = true;
		Encoder<LoggingEvent> encoder = new SerializableEncoder<LoggingEvent>(compressed);
		Decoder<LoggingEvent> decoder = new SerializableDecoder<LoggingEvent>(compressed);
		List<byte[]> collectedBytes = new ArrayList<byte[]>(loggingEvents.size());
		long byteCounter = 0;
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			byte[] bytes = encoder.encode(current.getEvent());
			byteCounter += bytes.length;
			collectedBytes.add(bytes);
		}
		stopTest("serializationCompressed", "encode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", byteCounter);

		long dummy = 0;
		startTest();
		for(byte[] current : collectedBytes)
		{
			LoggingEvent event = decoder.decode(current);
			dummy += event.hashCode();
		}
		stopTest("serializationCompressed", "decode", byteCounter, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	public void streamingSerialization(List<EventWrapper<LoggingEvent>> loggingEvents)
		throws IOException, ClassNotFoundException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		startTest();
		int counter = 0;
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			oos.writeObject(current);
			if(++counter >= CoreConstants.OOS_RESET_FREQUENCY)
			{
				counter = 0;
				// Failing to reset the object output stream every now and
				// then creates a serious memory leak.
				// System.err.println("Doing oos.reset()");
				oos.reset();
			}

		}
		oos.flush();
		oos.close();
		byte[] bytes = bos.toByteArray();
		stopTest("streamingSerialization", "write", bytes.length, loggingEvents.size());
		if(logger.isDebugEnabled()) logger.debug("byteCounter: {}", bytes.length);

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		long dummy = 0;

		startTest();
		//noinspection ForLoopReplaceableByForEach
		for(int i = 0; i < loggingEvents.size(); i++)
		{
			Object obj = ois.readObject();
			dummy += obj.hashCode();
		}
		stopTest("streamingSerialization", "read", bytes.length, loggingEvents.size());
		ois.close();
		if(logger.isDebugEnabled()) logger.debug("Dummy: {}", dummy);
	}

	// ###

	public void protobufUncompressedAdd(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, false);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("protobufUncompressed", "add", loggingEvents.size());
	}

	public void protobufUncompressedAddAll(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, false);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("protobufUncompressed", "addAll", loggingEvents.size());
	}

	public void protobufUncompressedGet(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, false);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assert loggingEvents.size() == size;
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("protobufUncompressed", "get", loggingEvents.size());
	}

	// ###

	public void protobufCompressedAdd(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, true);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("protobufCompressed", "add", loggingEvents.size());
	}

	public void protobufCompressedAddAll(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, true);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("protobufCompressed", "addAll", loggingEvents.size());
	}

	public void protobufCompressedGet(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.PROTOBUF, true);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assert loggingEvents.size() == size;
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("protobufCompressed", "get", loggingEvents.size());
	}

	// ###

	public void xmlUncompressedAdd(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, false);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("xmlUncompressed", "add", loggingEvents.size());
	}

	public void xmlUncompressedAddAll(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, false);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("xmlUncompressed", "addAll", loggingEvents.size());
	}

	public void xmlUncompressedGet(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, false);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assert loggingEvents.size() == size;
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("xmlUncompressed", "get", loggingEvents.size());
	}

	// ###

	public void xmlCompressedAdd(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, true);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("xmlCompressed", "add", loggingEvents.size());
	}

	public void xmlCompressedAddAll(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, true);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("xmlCompressed", "addAll", loggingEvents.size());
	}

	public void xmlCompressedGet(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.JAVA_UTIL_XML, true);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assert loggingEvents.size() == size;
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("xmlCompressed", "get", loggingEvents.size());
	}

	// ###

	public void serializationUncompressedAdd(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, false);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("serializationUncompressed", "add", loggingEvents.size());
	}

	public void serializationUncompressedAddAll(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, false);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("serializationUncompressed", "addAll", loggingEvents.size());
	}

	public void serializationUncompressedGet(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, false);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assert loggingEvents.size() == size;
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("serializationUncompressed", "get", loggingEvents.size());
	}

	// ###

	public void serializationCompressedAdd(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, true);
		startTest();
		for(EventWrapper<LoggingEvent> current : loggingEvents)
		{
			buffer.add(current);
		}
		stopTest("serializationCompressed", "add", loggingEvents.size());
	}

	public void serializationCompressedAddAll(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, true);
		startTest();
		buffer.addAll(loggingEvents);
		stopTest("serializationCompressed", "addAll", loggingEvents.size());
	}

	public void serializationCompressedGet(List<EventWrapper<LoggingEvent>> loggingEvents)
	{
		CodecFileBuffer<EventWrapper<LoggingEvent>> buffer = createFileBuffer(TestFormat.SERIALIZATION, true);
		buffer.addAll(loggingEvents);
		long size = buffer.getSize();
		assert loggingEvents.size() == size;
		startTest();
		for(long i = 0; i < size; i++)
		{
			buffer.get(i);
		}
		stopTest("serializationCompressed", "get", loggingEvents.size());
	}

	// ###
	public void runBenchmarks(List<EventWrapper<LoggingEvent>> loggingEvents, boolean benchmarkXml)
		throws Exception
	{
		betweenBenchmarks();

		protobufUncompressed(loggingEvents);

		betweenBenchmarks();

		protobufCompressed(loggingEvents);

		if(benchmarkXml)
		{
			betweenBenchmarks();

			lilithXmlUncompressed(loggingEvents);

			betweenBenchmarks();

			lilithXmlCompressed(loggingEvents);

			betweenBenchmarks();

			javaUtilXmlUncompressed(loggingEvents);

			betweenBenchmarks();

			javaUtilXmlCompressed(loggingEvents);
		}

		betweenBenchmarks();

		serializationUncompressed(loggingEvents);

		betweenBenchmarks();

		serializationCompressed(loggingEvents);

		betweenBenchmarks();

		streamingSerialization(loggingEvents);


		betweenBenchmarks();

		setUp();
		protobufUncompressedAdd(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		protobufUncompressedAddAll(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		protobufUncompressedGet(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		protobufCompressedAdd(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		protobufCompressedAddAll(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		protobufCompressedGet(loggingEvents);
		tearDown();


		if(benchmarkXml)
		{
			betweenBenchmarks();

			setUp();
			xmlUncompressedAdd(loggingEvents);
			tearDown();


			betweenBenchmarks();

			setUp();
			xmlUncompressedAddAll(loggingEvents);
			tearDown();


			betweenBenchmarks();

			setUp();
			xmlUncompressedGet(loggingEvents);
			tearDown();


			betweenBenchmarks();

			setUp();
			xmlCompressedAdd(loggingEvents);
			tearDown();


			betweenBenchmarks();

			setUp();
			xmlCompressedAddAll(loggingEvents);
			tearDown();


			betweenBenchmarks();

			setUp();
			xmlCompressedGet(loggingEvents);
			tearDown();

		}

		betweenBenchmarks();

		setUp();
		serializationUncompressedAdd(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		serializationUncompressedAddAll(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		serializationUncompressedGet(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		serializationCompressedAdd(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		serializationCompressedAddAll(loggingEvents);
		tearDown();


		betweenBenchmarks();

		setUp();
		serializationCompressedGet(loggingEvents);
		tearDown();
	}
	// ###

	private void startTest()
	{
		time = System.nanoTime();
	}

	private void stopTest(String name, String action, int amount)
	{
		long expired = System.nanoTime() - time;
		long length = dataFile.length();
		logBenchmark(name, action, length, amount, expired);
	}

	private void logBenchmark(String name, String action, long size, int amount, long expiredNanos)
	{
		double fraction = (double) expiredNanos / 1000000000;
		double eventsFraction = ((double) amount) / fraction;
		if(logger.isDebugEnabled()) logger.debug("{}: expired={}s", name, fraction);
		if(logger.isDebugEnabled()) logger.debug("{}: events/s={}", name, eventsFraction);
		if(logger.isDebugEnabled()) logger.debug("{}: size={} bytes", name, size);
		long eventAverage = size / amount;

		String formattedAverage = HumanReadable.getHumanReadableSize(eventAverage, true, false) + "bytes";
		String formattedLength = HumanReadable.getHumanReadableSize(size, true, false) + "bytes";
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("#,##0.0#", symbols);
		String formattedEvents = format.format(eventsFraction);
		String formattedFraction = format.format(fraction);
		if(logger.isDebugEnabled()) logger.debug("average={}/event", name, formattedAverage);

		if(logger.isInfoEnabled())
		{
			logger
				.info("|| {} || {} || {} || {} || {} || {} ({}) || {} ||",
					new Object[]{name, action, amount, formattedFraction, formattedEvents, formattedLength, size, formattedAverage});
		}

	}

	private void stopTest(String name, String action, long length, int amount)
	{
		long expired = System.nanoTime() - time;
		logBenchmark(name, action, length, amount, expired);
	}

	private CodecFileBuffer<EventWrapper<LoggingEvent>> createFileBuffer(TestFormat format, boolean compressing)
	{
		Map<String, String> metaData = new HashMap<String, String>();

		if(format == TestFormat.JAVA_UTIL_XML)
		{
			metaData.put(FileConstants.CONTENT_FORMAT_KEY, CONTENT_FORMAT_VALUE_JAVA_BEANS_XML);
		}
		else if(format == TestFormat.PROTOBUF)
		{
			metaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
		}
		/*
		else if(format == TestFormat.LILITH_XML)
		{
			metaData.put(FileConstants.CONTENT_FORMAT_KEY, CONTENT_FORMAT_VALUE_LILITH_XML);
		}
		*/
		else
		{
			metaData.put(FileConstants.CONTENT_FORMAT_KEY, CONTENT_FORMAT_VALUE_SERIALIZED);
		}

		if(compressing)
		{
			metaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);
		}
		else
		{
			//metaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);
		}

		CodecFileBuffer<EventWrapper<LoggingEvent>> result = new CodecFileBuffer<EventWrapper<LoggingEvent>>(magicValue, false, metaData, null, dataFile, indexFile);

		FileHeader fileHeader = result.getFileHeader();
		MetaData actualMetaData = fileHeader.getMetaData();
		Map<String, String> md = actualMetaData.getData();
		boolean compressed = false;

		String formatStr = null;
		if(md != null)
		{
			compressed = FileConstants.COMPRESSION_VALUE_GZIP.equals(md.get(FileConstants.COMPRESSION_KEY));
			formatStr = md.get(FileConstants.CONTENT_FORMAT_KEY);
		}

		Codec<EventWrapper<LoggingEvent>> codec;
		if(CONTENT_FORMAT_VALUE_JAVA_BEANS_XML.equals(formatStr))
		{
			if(compressed)
			{
				codec = new CompressingLoggingEventWrapperXmlCodec();
			}
			else
			{
				codec = new LoggingEventWrapperXmlCodec();
			}
		}
		else if(FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF.equals(formatStr))
		{
			if(compressed)
			{
				codec = new CompressingLoggingEventWrapperProtobufCodec();

			}
			else
			{
				codec = new LoggingEventWrapperProtobufCodec();
			}
		}
		/*
		else if(CONTENT_FORMAT_VALUE_LILITH_XML.equals(formatStr))
		{
			if(compressed)
			{
				codec = ...
			}
			else
			{
				codec = ...
			}
		}
		*/
		else
		{
			if(compressed)
			{
				codec = new CompressingSerializableCodec<EventWrapper<LoggingEvent>>();
			}
			else
			{
				codec = new SerializableCodec<EventWrapper<LoggingEvent>>();
			}
		}
		result.setCodec(codec);

		if(logger.isDebugEnabled()) logger.debug("Created file buffer: {}", result);
		return result;
	}

	private static LoggingEvent createLoggingEvent(int counter)
	{

		LoggingEvent result = new LoggingEvent();
		result
			.setMessage(new Message("messagePattern-" + counter + ": {} {}", new String[]{"param1-" + counter, "param2-" + counter}));
		result.setLevel(LoggingEvent.Level.INFO);
		result.setLogger("logger-" + counter);
		result.setMarker(new Marker("marker-" + counter));
		Map<String, String> mdc = new HashMap<String, String>();
		mdc.put("key-" + counter, "value-" + counter);
		result.setMdc(mdc);
		result
			.setNdc(new Message[]{new Message("ndcMessagePattern-" + counter + ": {} {}", new String[]{"ndcParam1-" + counter, "ndcParam2-" + counter})});

		result.setThreadInfo(new ThreadInfo(null, "threadName-" + counter, null, null));
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
		result.setTimeStamp(1234567890000L);
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

	public static void betweenBenchmarks()
		throws InterruptedException
	{
		if(executeGcBetween)
		{
			Runtime runtime = Runtime.getRuntime();
			System.out.println("freeMemory before gc: " + runtime.freeMemory());
			System.gc();
			System.out.println("Sleeping a while...");
			Thread.sleep(1000); // give the gc a chance to kick in.
			System.out.println("freeMemory after gc : " + runtime.freeMemory());
		}
	}

	public static void main(String[] args)
		throws Exception
	{
		final Logger logger = LoggerFactory.getLogger(Benchmark.class);

		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long totalMemory = runtime.totalMemory();
		if(maxMemory != totalMemory)
		{
			System.out.println("maxMemory=" + runtime.maxMemory());
			System.out.println("totalMemory=" + runtime.totalMemory());
			System.out.println("\nmaxMemory and totalMemory differ, please restart with options '-Xms512m -Xmx512m'.");
			System.exit(0);
		}
		boolean benchmarkXml=false;
		boolean noExceptions=false;
		boolean noCallstack=false;
		if(args != null)
		{
			for(String current : args)
			{
				if("-gc".equals(current))
				{
					executeGcBetween = true;
				}
				if("-xml".equals(current))
				{
					benchmarkXml=true;
				}
				if("-nc".equals(current))
				{
					noCallstack=true;
				}
				if("-ne".equals(current))
				{
					noExceptions=true;
				}
			}
		}
		System.out.print("Creating events... ");
		System.out.flush();
		List<EventWrapper<LoggingEvent>> loggingEvents = createDataSet(2000);
		if(noCallstack)
		{
			System.out.println("Removing callstacks...");
			for(EventWrapper<LoggingEvent> current:loggingEvents)
			{
				current.getEvent().setCallStack(null);
			}
		}
		if(noExceptions)
		{
			System.out.println("Removing exceptions...");
			for(EventWrapper<LoggingEvent> current:loggingEvents)
			{
				current.getEvent().setThrowable(null);
			}
		}
		System.out.println("done!");
		Benchmark benchmark = new Benchmark();

		for(int i = 0; i < 3; i++)
		{
			// yes, JIT, please optimize...
			benchmark.runBenchmarks(loggingEvents, benchmarkXml);
		}
		System.out.println("\n\n\n#######################################################################");
		System.out.println("And now for the real thing... JIT should have had enough time by now :p");
		System.out.println("#######################################################################\n\n\n");
		logger
			.info("|| Name || Action || Amount || seconds || Operations/s || total size (raw size) || size/element ||");
		benchmark.runBenchmarks(loggingEvents, benchmarkXml);
	}
}
