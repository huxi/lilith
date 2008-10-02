package de.huxhorn.lilith.data.logging.xml;

import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.sulky.stax.GenericStreamReader;
import de.huxhorn.sulky.stax.StaxUtilities;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamConstants;

public class StackTraceElementReader
		implements GenericStreamReader<ExtendedStackTraceElement>, LoggingEventSchemaConstants
{
	public ExtendedStackTraceElement read(XMLStreamReader reader) throws XMLStreamException
	{
		String rootNamespace = NAMESPACE_URI;
		int type = reader.getEventType();

		if (XMLStreamConstants.START_DOCUMENT == type)
		{
			reader.nextTag();
			type = reader.getEventType();
			rootNamespace = null;
		}

		if (XMLStreamConstants.START_ELEMENT == type
				&& STACK_TRACE_ELEMENT_NODE.equals(reader.getLocalName()))
		{
			String className= StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, ST_CLASS_NAME_ATTRIBUTE);
			String methodName=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, ST_METHOD_NAME_ATTRIBUTE);
			String fileName=StaxUtilities.readAttributeValue(reader, NAMESPACE_URI, ST_FILE_NAME_ATTRIBUTE);
			reader.nextTag();
			int lineNumber=-1;
			String str = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, ST_LINE_NUMBER_NODE);
			if(str != null)
			{
				lineNumber=Integer.valueOf(str);
			}
			type = reader.getEventType();
			if (XMLStreamConstants.START_ELEMENT == type && ST_NATIVE_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
			{
				lineNumber = ExtendedStackTraceElement.NATIVE_METHOD;
				reader.nextTag(); // close native
				reader.nextTag();
			}
			String codeLocation = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, ST_CODE_LOCATION_NODE);
			String version = StaxUtilities.readSimpleTextNodeIfAvailable(reader, NAMESPACE_URI, ST_VERSION_NODE);
			type = reader.getEventType();
			boolean exact=false;
			if (XMLStreamConstants.START_ELEMENT == type && ST_EXACT_NODE.equals(reader.getLocalName()) && NAMESPACE_URI.equals(reader.getNamespaceURI()))
			{
				exact=true;
				reader.nextTag(); // close exact
				reader.nextTag();
			}

			reader.require(XMLStreamConstants.END_ELEMENT, rootNamespace, STACK_TRACE_ELEMENT_NODE);
			return new ExtendedStackTraceElement(className, methodName, fileName, lineNumber, codeLocation, version, exact);
		}
		return null;
	}
}
