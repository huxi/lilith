/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.log4j.xml;

public interface LoggingEventSchemaConstants
{
	/*
<log4j:event logger="de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass" timestamp="1233135682640" level="DEBUG" thread="main">
<log4j:message><![CDATA[Foo!]]></log4j:message>
<log4j:NDC><![CDATA[NDC1 NDC2]]></log4j:NDC>
<log4j:throwable><![CDATA[java.lang.RuntimeException: Hello
	at de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:18)
	at de.huxhorn.lilith.sandbox.Log4jSandbox.main(Log4jSandbox.java:38)
Caused by: java.lang.RuntimeException: Hi.
	at de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.foobar(Log4jSandbox.java:24)
	at de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass.execute(Log4jSandbox.java:14)
	... 1 more
]]></log4j:throwable>
<log4j:locationInfo class="de.huxhorn.lilith.sandbox.Log4jSandbox$InnerClass" method="execute" file="Log4jSandbox.java" line="18"/>
<log4j:properties>
<log4j:data name="key1" value="value1"/>
<log4j:data name="key2" value="value2"/>
</log4j:properties>
</log4j:event>
	
	 */
	String DEFAULT_NAMESPACE_PREFIX = "log4j";
	String NAMESPACE_URI = "http://jakarta.apache.org/log4j/";


	String LOGGING_EVENT_NODE = "event";

	String LOGGER_ATTRIBUTE = "logger";
	String TIMESTAMP_ATTRIBUTE = "timestamp";
	String LEVEL_ATTRIBUTE = "level";
	String THREAD_NAME_ATTRIBUTE = "thread";

	String MESSAGE_NODE = "message";
	String NDC_NODE = "NDC";

	String THROWABLE_NODE = "throwable";

	String LOCATION_INFO_NODE = "locationInfo";

	String CLASS_ATTRIBUTE = "class";
	String METHOD_ATTRIBUTE = "method";
	String FILE_ATTRIBUTE = "file";
	String LINE_ATTRIBUTE = "line";

	String PROPERTIES_NODE = "properties";
	String DATA_NODE = "data";
	String NAME_ATTRIBUTE = "name";
	String VALUE_ATTRIBUTE = "value";
}
