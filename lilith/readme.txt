ATTENTION!!

Lilith >= 0.9.24 isn't compatible to previous versions!!
Lilith >= 0.9.27 access events aren't compatible to previous versions!!
slf4j-1.5.0 and logback-0.9.9 are absolutely required!
It is possible, though, to use Lilith >= V0.9.24 side by side with Lumberjack V0.9.23
so a smooth transition should be possible...

Start Lilith with
java -jar lib/lilith.jar

Lilith opens:
- a standard LoggingEvent server socket on port 4560 as used by LogBACK.
- a standard AccessEvent server socket on port 4570 as used by LogBACK.
- a multiplex Lilith LoggingEvent server socket on ports 10000 (compressed)
  and 10001 (uncompressed).
- a multiplex AccessEvent server socket on ports 10010 (compressed)
  and 10011 (uncompressed).
- a multiplex Lilith LoggingEvent XML server socket on ports 10020 (compressed)
  and 10021 (uncompressed).
- a multiplex Lilith LoggingEvent XML server socket on ports 11000 that is using zero-delimited xml-events,
  as required by ActionScript XMLSocket.

Logging of zero-delimited xml event producer in Lilith has deliberately been left enabled.

If Lilith takes a long time to start up and you are using windows you should probably execute
regsvr32 /u %windir%\system32\zipfldr.dll
to disable ZIP-support in the windows file explorer. It sucks anyway...
http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6578753
http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5050516

multiplex-appender-classic Example configuration:
<dependency>
	<groupId>de.huxhorn.lilith</groupId>
	<artifactId>de.huxhorn.lilith.logback.appender.multiplex-classic</artifactId>
	<version>version</version>
	<scope>runtime</scope>
</dependency>

<appender name="multiplex" class="de.huxhorn.lilith.logback.appender.ClassicMultiplexSocketAppender">
    <Compressing>true</Compressing> <!-- will automatically use correct default port -->
    <ReconnectionDelay>10000</ReconnectionDelay>
    <IncludeCallerData>true</IncludeCallerData>
    <RemoteHosts>localhost, 10.200.55.13</RemoteHosts>
</appender>

multiplex-appender-access Example configuration:

In pom.xml:
<plugin>
	<groupId>org.mortbay.jetty</groupId>
	<artifactId>maven-jetty-plugin</artifactId>
	<configuration>
		<scanIntervalSeconds>10</scanIntervalSeconds>
		<connectors>
			<connector implementation="org.mortbay.jetty.nio.SelectChannelConnector">
				<port>8080</port>
				<maxIdleTime>60000</maxIdleTime>
			</connector>
			<connector implementation="org.mortbay.jetty.security.SslSocketConnector">
				<port>8181</port>
				<maxIdleTime>60000</maxIdleTime>
				<keystore>src/main/jetty/keystore</keystore>
				<password>testing</password>
				<keyPassword>testing</keyPassword>
			</connector>
		</connectors>
		<requestLog implementation="ch.qos.logback.access.jetty.RequestLogImpl">
			<fileName>src/main/jetty/logback-access.xml</fileName>
		</requestLog>
	</configuration>
	<dependencies>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>jcl104-over-slf4j</artifactId>
			<version>${slf4jVersion}</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>commons-logging</groupId>
			<artifactId>commons-logging</artifactId>
			<version>${commonsLoggingVersion}</version>
			<scope>provided</scope>
			<exclusions>
				<exclusion>
					<groupId>javax.servlet</groupId>
					<artifactId>servlet-api</artifactId>
				</exclusion>
				<exclusion>
					<groupId>logkit</groupId>
					<artifactId>logkit</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>log4j-over-slf4j</artifactId>
			<version>${slf4jVersion}</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>${log4jVersion}</version>
			<scope>provided</scope>
		</dependency>

		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-core</artifactId>
			<version>${logbackVersion}</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
			<version>${logbackVersion}</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-access</artifactId>
			<version>${logbackVersion}</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>janino</groupId>
			<artifactId>janino</artifactId>
			<version>2.4.3</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>de.huxhorn.lilith</groupId>
			<artifactId>de.huxhorn.lilith.logback.appender.multiplex-access</artifactId>
			<version>version</version>
			<scope>runtime</scope>
		</dependency>
	</dependencies>
</plugin>

In src/main/jetty/logback-access.xml:
<configuration>
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<layout class="ch.qos.logback.access.PatternLayout">
			<!-- <Pattern>%h %l %u %user %date "%r" %s %b</Pattern> -->
			<Pattern>combined</Pattern>
		</layout>
	</appender>
	<appender name="multiplex" class="de.huxhorn.lilith.logback.appender.AccessMultiplexSocketAppender">
		<!-- <Port>4563</Port> -->
		<Compressing>true</Compressing> <!-- will automatically use correct default port -->
		<ReconnectionDelay>30000</ReconnectionDelay>
		<RemoteHosts>localhost</RemoteHosts>
	</appender>

	<appender-ref ref="STDOUT" />
	<appender-ref ref="multiplex" />
</configuration>
