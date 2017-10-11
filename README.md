# Lilith [![Build Status](https://travis-ci.org/huxi/lilith.png?branch=master)](https://travis-ci.org/huxi/lilith) [![Coverage Status](https://coveralls.io/repos/huxi/lilith/badge.png)](https://coveralls.io/r/huxi/lilith) [![Maven Central](https://img.shields.io/maven-central/v/de.huxhorn.lilith/de.huxhorn.lilith.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.huxhorn.lilith%22)
Lilith ([@lilithapp](https://twitter.com/#!/lilithapp)) is a logging and access event viewer for [Logback][], [log4j][]™, [Log4j 2][log4j2]™ and [java.util.logging][jul].

![Lilith][img-lilith]

It has features roughly comparable to [Chainsaw][] for log4j™, but with an emphasis on stability, high performance and throughput. In contrast to Chainsaw, it is handling received logging events using the hard disk instead of keeping them in memory. Because of this, it is able to handle millions of events from several sources at the same time.

## Getting started...

### ...with Logback...

#### ...and logback-classic SocketAppender.

Lilith is listening for logback-classic [`SocketAppender`][lcsa] connections on port 4445 or port 4560. 

A [`SocketAppender`][lcsa]  establishes a connection with exactly one host that is defined in the `RemoteHost` property.

Add the following to your applications `logback.xml`:

```xml
<appender name="LogbackClassic" class="ch.qos.logback.classic.net.SocketAppender">
  <RemoteHost>localhost</RemoteHost>
  <Port>4560</Port>
  <ReconnectionDelay>170</ReconnectionDelay>
  <IncludeCallerData>true</IncludeCallerData>
</appender>
```

You also have to attach the appender to some logger, e.g. the root logger...

```xml
<root level="INFO">
  <appender-ref ref="LogbackClassic"/>
</root>
```

... or a specific logger...

```xml
<logger name="foo.Bar" level="DEBUG">
  <appender-ref ref="LogbackClassic"/>
</logger>
```

Using logback-classic [`SocketAppender`][lcsa] requires `ch.qos.logback:logback-classic` as runtime dependency.

Take a look at the [Logback manual][logbackm] and the Lilith help for more information.

#### ...and Lilith ClassicMultiplexSocketAppender.

Lilith is listening for Lilith [`ClassicMultiplexSocketAppender`][cmsa] connections on port 10000 (compressed) and 10001 (uncompressed). 

The Lilith [`ClassicMultiplexSocketAppender`][cmsa] is a replacement for the logback-classic [`SocketAppender`][lcsa]. 

This appender, in contrast to logbacks, supports `logger.debug("{} {}", new Object[]{foo, bar, throwable)`, i.e. if the last given parameter of a log message is a `Throwable` and it is not used up in the message pattern then it will be used as the `Throwable` of the `LoggingEvent`, similar to `logger.debug(""+foo+" "+bar, throwable)`.

While logbacks appender is stream-based, i.e. it streams logging events using an `ObjectOutputStream`, the Lilith appender is message based, i.e. it sends logging events one after the other as single messages.

A message consists of an integer that specifies the number of bytes of the following event, followed by the bytes of the serialized event.

This has several benefits:

* Sending to multiple remote hosts is supported while the event is only serialized once.
* Events can (and should) be compressed using GZIP.
* The appender supports heartbeat and timeout. 
  * The event receiver can find out that the event sender connection died if a heartbeat is missing.
  * The event sender can find out that the event receiver connection died by means of a timeout This means that an application won't stop (at least not for very long) in case of network problem.

The multiplex appenders are now creating a UUID be default. This enables Lilith to reattach a connection to an existing view after the connection has been lost for some reason. It has the advantage that already executing filters won't have to be restarted for every new connection. The previous behavior can be enforced by disabling the creation of the UUID by means of `<CreatingUUID>false</CreatingUUID>` in the Logback configuration.
  
Add the following to your applications `logback.xml`:

```xml
<appender name="multiplex" class="de.huxhorn.lilith.logback.appender.ClassicMultiplexSocketAppender">
  <Compressing>true</Compressing>
  <!-- will automatically use correct default port -->
  <!-- Default port for compressed is 10000 and uncompressed 10001 -->
  <ReconnectionDelay>10000</ReconnectionDelay>
  <IncludeCallerData>true</IncludeCallerData>
  <RemoteHosts>localhost, 10.200.55.13</RemoteHosts>
  <!-- Alternatively:
  <RemoteHost>localhost</RemoteHost>
  <RemoteHost>10.200.55.13</RemoteHost>
  -->
  <!--
  Optional:
  <CreatingUUID>false</CreatingUUID>
  -->
</appender>
```

You also have to attach the appender to some logger, e.g. the root logger...

```xml
<root level="INFO">
  <appender-ref ref="multiplex"/>
</root>
```

... or a specific logger...

```xml
<logger name="foo.Bar" level="DEBUG">
  <appender-ref ref="multiplex"/>
</logger>
```

Using Lilith [`ClassicMultiplexSocketAppender`][cmsa] requires `de.huxhorn.lilith:de.huxhorn.lilith.logback.appender.multiplex-classic` as runtime dependency.

Take a look at the [Logback manual][logbackm] and the Lilith help for more information.

#### ...and logback-classic with FileAppender.

Starting with Lilith 0.9.38 and Logback 0.9.19, you can write Lilith files directly from within Logback.

Those files can be opened by Lilith using either the Open command or drag&drop.

You can also use the `tail` and `cat` command available in the executable Lilith jar. This has the huge advantage that you can decide about the [layout pattern][lcpl] of the log file output at the time you are executing the above commands instead of while defining the file appender.

```
  Commands:
    cat      Cat the given file.
      Usage: cat [options] 'cat' the given Lilith logfile.
        Options:
          -n, --number-of-lines   number of entries printed by 'cat'.
                                  Default: -1
          -p, --pattern           pattern used by 'cat'. See
                                  http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout and
                                  http://logback.qos.ch/manual/layouts.html#AccessPatternLayout

    tail      Tail the given file.
      Usage: tail [options] 'tail' the given Lilith logfile.
        Options:
          -f, --keep-running      keep tailing the given Lilith logfile.
                                  Default: false
          -n, --number-of-lines   number of entries printed by 'tail'.
                                  Default: 20
          -p, --pattern           pattern used by 'tail'. See
                                  http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout and
                                  http://logback.qos.ch/manual/layouts.html#AccessPatternLayout
```

Add the following to your applications `logback.xml`:

```xml
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
  <file>classic.lilith</file>
  <encoder class="de.huxhorn.lilith.logback.encoder.ClassicLilithEncoder">
    <IncludeCallerData>true</IncludeCallerData>
  </encoder>
</appender>
```

You also have to attach the appender to some logger, e.g. the root logger...

```xml
<root level="INFO">
  <appender-ref ref="FILE"/>
</root>
```

... or a specific logger...

```xml
<logger name="foo.Bar" level="DEBUG">
  <appender-ref ref="FILE"/>
</logger>
```

Using Lilith [`ClassicLilithEncoder`][cle] requires `de.huxhorn.lilith:de.huxhorn.lilith.logback.encoder.classic` as runtime dependency.

Take a look at the [Logback manual][logbackm] and the Lilith help for more information.


#### ...and logback-access SocketAppender.

Lilith listens for logback-access [`SocketAppender`][lasa] connections on port 4570.

A [`SocketAppender`][lasa]  establishes a connection with exactly one host that is defined in the `RemoteHost` property.

Add the following to your applications `logback-access.xml`:

```xml
<appender name="LogbackAccess" class="ch.qos.logback.access.net.SocketAppender"> 
  <RemoteHost>localhost</RemoteHost>
  <Port>4570</Port> 
  <ReconnectionDelay>170</ReconnectionDelay> 
  <IncludeCallerData>true</IncludeCallerData>
</appender>
```

and

```xml
<appender-ref ref="LogbackAccess" />
```

Using logback-access [`SocketAppender`][lasa] requires `ch.qos.logback:logback-access` as runtime dependency.

Take a look at the [Logback manual][logbackm] and the Lilith help for more information.

#### ...and Lilith AccessMultiplexSocketAppender.

Lilith is listening for Lilith [`AccessMultiplexSocketAppender`][amsa] connections on port 10010 (compressed) and 10011 (uncompressed). 

The Lilith [`AccessMultiplexSocketAppender`][amsa] is a replacement for the logback-access [`SocketAppender`][lasa]. 

While logbacks appender is stream-based, i.e. it streams logging events using an `ObjectOutputStream`, the Lilith appender is message based, i.e. it sends logging events one after the other as single messages.

A message consists of an integer that specifies the number of bytes of the following event, followed by the bytes of the serialized event.

This has several benefits:

* Sending to multiple remote hosts is supported while the event is only serialized once.
* Events can (and should) be compressed using GZIP.
* The appender supports heartbeat and timeout. 
  * The event receiver can find out that the event sender connection died if a heartbeat is missing.
  * The event sender can find out that the event receiver connection died by means of a timeout This means that an application won't stop (at least not for very long) in case of network problem.

Add the following to your applications `logback-access.xml`:

```xml
<appender name="multiplex" class="de.huxhorn.lilith.logback.appender.access.AccessMultiplexSocketAppender">
  <Compressing>true</Compressing> <!-- will automatically use correct default port -->
  <!-- Default port for compressed is 10010 and uncompressed 10011 -->
  <ReconnectionDelay>30000</ReconnectionDelay>
  <RemoteHosts>localhost</RemoteHosts>
</appender>
```

and

```xml
<appender-ref ref="multiplex" />
```

Using Lilith [`AccessMultiplexSocketAppender`][amsa] requires `de.huxhorn.lilith:de.huxhorn.lilith.logback.appender.multiplex-access` as runtime dependency.

Take a look at the [Logback manual][logbackm] and the Lilith help for more information.

#### ...and logback-access with FileAppender.

Starting with Lilith 0.9.38 and Logback 0.9.19, you can write Lilith files directly from within Logback.

Those files can be opened by Lilith using either the Open command or drag&drop.

You can also use the `tail` and `cat` command available in the executable Lilith jar. This has the huge advantage that you can decide about the [layout pattern][lapl] of the log file output at the time you are executing the above commands instead of while defining the file appender.

```
  Commands:
    cat      Cat the given file.
      Usage: cat [options] 'cat' the given Lilith logfile.
        Options:
          -n, --number-of-lines   number of entries printed by 'cat'.
                                  Default: -1
          -p, --pattern           pattern used by 'cat'. See
                                  http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout and
                                  http://logback.qos.ch/manual/layouts.html#AccessPatternLayout

    tail      Tail the given file.
      Usage: tail [options] 'tail' the given Lilith logfile.
        Options:
          -f, --keep-running      keep tailing the given Lilith logfile.
                                  Default: false
          -n, --number-of-lines   number of entries printed by 'tail'.
                                  Default: 20
          -p, --pattern           pattern used by 'tail'. See
                                  http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout and
                                  http://logback.qos.ch/manual/layouts.html#AccessPatternLayout
```

Add the following to your applications `logback-access.xml`:

```xml
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
  <file>classic.lilith</file>
  <encoder class="de.huxhorn.lilith.logback.encoder.access.AccessLilithEncoder">
    <IncludeCallerData>true</IncludeCallerData>
  </encoder>
</appender>

<appender-ref ref="FILE" />
```

Using Lilith [`AccessLilithEncoder`][ale] requires `de.huxhorn.lilith:de.huxhorn.lilith.logback.encoder.access` as runtime dependency.

Take a look at the [Logback manual][logbackm] and the Lilith help for more information.

### ...with Log4j 2™.
Lilith is listening for [Log4j 2][log4j2]™ [`SocketAppender`][log4j2sa] connections...
 
- ...using `SerializedLayout` on port 4445 or port 4560.
- ...using `JsonLayout` on port 12000.
- ...using `YamlLayout` on port 12010.
- ...using `XmlLayout` on port 12020.


```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration status="debug">
  <appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} --- %msg%n"/>
    </Console>
    <Socket name="Socket-JSON" host="localhost" port="12000" protocol="TCP">
      <JsonLayout includeNullDelimiter="true" />
    </Socket>
    <Socket name="Socket-YAML" host="localhost" port="12010" protocol="TCP">
      <YamlLayout includeNullDelimiter="true" />
    </Socket>
    <Socket name="Socket-XML" host="localhost" port="12020" protocol="TCP">
      <XmlLayout includeNullDelimiter="true" />
    </Socket>
    <Socket name="Socket-Serialized" host="localhost" port="4560" protocol="TCP">
      <SerializedLayout />
    </Socket>
  </appenders>
  <loggers>
    <root level="all">
      <appender-ref ref="Console"/>
      <appender-ref ref="Socket-JSON"/>
      <appender-ref ref="Socket-YAML"/>
      <appender-ref ref="Socket-XML"/>
      <appender-ref ref="Socket-Serialized"/>
    </root>
  </loggers>
</configuration>
```

`SerializedLayout` has been deprecated in Log4j2 2.9.0. You should instead use one one of the other options.

Take a look at the [Log4j 2™ manual][log4j2m] for more information.

### ...with log4j™.
Lilith is listening for [log4j][]™ [`SocketAppender`][log4jsa] connections on port 4445 or port 4560.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
  <appender name="socket" class="org.apache.log4j.net.SocketAppender">
    <param name="Application" value="YourApplication"/>
    <param name="locationInfo" value="true"/>
    <param name="Port" value="4560"/>
    <param name="RemoteHost" value="127.0.0.1"/>
    <param name="ReconnectionDelay" value="10"/>
  </appender>
    
  <root>
    <priority value ="ALL" />
    <appender-ref ref="socket" />
  </root>
</log4j:configuration>
```

Take a look at the [log4j™ manual][log4jm] for more information.

### ...with java.util.logging.SocketHandler.

Some people are forced to use [java.util.logging][jul]. If you are one of those poor souls, you have my deepest sympathy. Seriously.

Lilith is listening for [java.util.logging][jul] [`SocketHandler`][julsh] connections on port 11020.

If you are not exactly forced to keep using [java.util.logging][jul] I'd seriously suggest to consider a switch to the [SLF4J][]/[Logback][] combination.

The only missing feature that [java.util.logging][jul] has to offer are user-defined log-levels, albeit very, very poorly implemented. If you are actually using this feature then you are risking a memory leak. It would be a wise decision to use the [SLF4J][]-Marker-support instead. 

The [java.util.logging][jul] `SocketHandler` does also have one major downside compared to all alternatives:
   
* It will only send the top-most exception of an exception-hierarchy.
  You won't be able to see the root-cause(s) of an exception.
* Suppressed exceptions, as introduced in Java 7, are also ignored.

Check out this [list of reasons to prefer logback over log4j™][reasons] for a quite detailed overview of the advantages you'll get if you decide to take the dive. While this page isn't related to [java.util.logging][jul] it will still give you a very good summary of all the nice features you'll get.

Last but not least, you'd be able to use the Lilith [`ClassicMultiplexSocketAppender`][cmsa], a partially asynchronous appender designed for high-performance multiple-recipient usage in a live environment.

Configure [java.util.logging][jul] as usual and define a [`SocketHandler`][julsh] connecting to port 11020.

```java
Logger rootLogger=Logger.getLogger("");
try {
    SocketHandler fh = new SocketHandler("127.0.0.1", 11020);
    fh.setEncoding("UTF-8");
    fh.setFormatter(new XMLFormatter());
    rootLogger.addHandler(fh);
} catch(IOException ex) {
    System.out.println("Couldn't connect the SocketHandler. Nope, no reconnect. What a fail.");
    ex.printStackTrace();
}
```

## Building from source

To build Lilith from source, you need to first clone both [huxi/sulky](https://github.com/huxi/sulky) and [huxi/lilith](https://github.com/huxi/lilith).

### Using Gradle

Both `sulky` and `lilith` are built using [Gradle][].

You have two options:

1. install [Gradle][] on your system (but please make sure that you are using the correct version, most likely the latest)
1. Use the gradlew wrapper scripts, either `./gradlew` (unix-like systems) or `gradlew.bat` (Windows) 

Using the `gradlew` wrapper has the advantage that it always uses the correct gradle version but has the disadvantage that it is harder to use in submodules compared to a gradle installation in the path.

If you want to rebuild a submodule using `gradlew`, you have to change into the respective sub-directory and execute `gradlew` with the correct amount of `../` prepended to the command.

Whenever I use `gradle` in the remainder of the document, I refer to either the locally installed `gradle` in your path or the `gradlew` wrapper script as explained above.

### Building sulky

Change into your local `sulky` directory and execute `gradle`. 

This builds and tests all `sulky` modules.

Executing `gradle javadocZip`/`gradle sourceZip` creates a zip containing all javadocs/sources in `build/distributions`. 

### Building lilith
Building `lilith` requires that you've built `sulky` first.

Then change into your local `lilith` directory and execute `gradle`.

This creates all Lilith artifacts in `lilith/build/distributions` and `lilith/build/libs`.

Executing `gradle javadocZip`/`gradle sourceZip` creates a zip containing all javadocs/sources in `build/distributions`. 

## Help me!

I'm looking for help concerning the development of IDE plugins.

I've already developed a plugin for [IntelliJ IDEA][idea] (my IDE of choice) myself. It is contained in the `lilith-idea-plugin` folder. This plugin opens a ServerSocket on port 11111 and expects serialized [`StackTraceElement`][ste] instances. Lilith sends those events whenever the user clicks on either a callstack, an exception or part of an exception stacktrace. The plugin is doing its best to open the source location of the received element in the IDE.

Unfortunately, I have neither time nor know-how about plugin development for either [Eclipse][] or [NetBeans][]. It would be very nice if you could could help me out.

I'd also appreciate any help documenting Lilith.

And I'm always eager to hear your opinion.

## Thanks
Thanks to Ceki Gülcü ([@ceki](https://twitter.com/#!/ceki)) for developing [Logback][], the [Gradle][] ([@Gradleware](https://twitter.com/Gradleware)) community and all persons that have been involved with Lilith development.

I'd also like to thank the developers of [Marked][] ([@MarkedApp](https://twitter.com/#!/MarkedApp)) for a really helpful, awesome and reasonably priced product.

Similarly, [Tower][] ([@gittower](https://twitter.com/#!/gittower)) is a really nice git client.

And, of course, thanks to the [IntelliJ IDEA][idea] ([@intellijidea](https://twitter.com/#!/intellijidea)) for the best Java IDE and their support of open source projects.

(This is all free advertisement, I don't get money from any of them.)

## Legal mumbo-jumbo
Apache Extras Companion for Apache log4j, Apache log4j, Apache, the Apache feather logo, the Apache Logging Services project logo, the log4j logo, and the Built by Maven logo are trademarks of The Apache Software Foundation. Oracle and Java are registered trademarks of Oracle and/or its affiliates. Other names may be trademarks of their respective owners.

All parts of Lilith that can be embedded into an application are [LGPL][]/[ASL][] dual-licensed. Lilith itself is [GPL][]-licensed.

All Your Base Are Belong To Us.

[ale]: http://lilithapp.com/javadoc/de/huxhorn/lilith/logback/encoder/access/AccessLilithEncoder.html "de.huxhorn.lilith.logback.encoder.access.AccessLilithEncoder"
[amsa]: http://lilithapp.com/javadoc/de/huxhorn/lilith/logback/appender/access/AccessMultiplexSocketAppender.html "de.huxhorn.lilith.logback.appender.access.AccessMultiplexSocketAppender"
[asl]: http://www.apache.org/licenses/LICENSE-2.0.html "Apache License, Version 2.0"
[chainsaw]: http://logging.apache.org/chainsaw/ "Chainsaw"
[cle]: http://lilithapp.com/javadoc/de/huxhorn/lilith/logback/encoder/ClassicLilithEncoder.html "de.huxhorn.lilith.logback.encoder.ClassicLilithEncoder"
[cmsa]: http://lilithapp.com/javadoc/de/huxhorn/lilith/logback/appender/ClassicMultiplexSocketAppender.html "de.huxhorn.lilith.logback.appender.ClassicMultiplexSocketAppender"
[eclipse]: http://eclipse.org/ "Eclipse IDE"
[gpl]: http://www.gnu.org/licenses/gpl.html "The GNU General Public License v3.0"
[gradle]: http://gradle.org/ "Gradle - A better way to build."
[idea]: http://www.jetbrains.com/idea/ "IntelliJ IDEA - The Most Intelligent Java IDE"
[jul]: http://download.oracle.com/javase/7/docs/api/java/util/logging/package-summary.html "java.util.logging package"
[julsh]: http://docs.oracle.com/javase/7/docs/api/java/util/logging/SocketHandler.html "java.util.logging.SocketHandler"
[lapl]: http://logback.qos.ch/manual/layouts.html#AccessPatternLayout "logback-access PatternLayout"
[lasa]: http://logback.qos.ch/apidocs/ch/qos/logback/access/net/SocketAppender.html "ch.qos.logback.access.net.SocketAppender" 
[lcpl]: http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout "logback-classic PatternLayout"
[lcsa]: http://logback.qos.ch/apidocs/ch/qos/logback/classic/net/SocketAppender.html "ch.qos.logback.classic.net.SocketAppender"
[lgpl]: http://www.gnu.org/licenses/lgpl.html "GNU Lesser General Public License v3.0"
[log4j2]: http://logging.apache.org/log4j/2.x/ "Apache Log4j 2™"
[log4j2m]: http://logging.apache.org/log4j/2.x/manual/index.html "Log4j 2™ manual"
[log4j2sa]: http://logging.apache.org/log4j/2.x/manual/appenders.html#SocketAppender "SocketAppender"
[log4j]: http://logging.apache.org/log4j/ "Apache log4j™"
[log4jm]: http://logging.apache.org/log4j/1.2/manual.html "log4j™ manual"
[log4jsa]: http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/net/SocketAppender.html "org.apache.log4j.net.SocketAppender"
[logback]: http://logback.qos.ch/ "Logback - the generic, reliable, fast & flexible logging framework"
[logbackm]: http://logback.qos.ch/manual/ "Logback manual"
[marked]: http://markedapp.com/ "marked - Markdown Preview for any text editor."
[netbeans]: http://netbeans.org/ "NetBeans IDE"
[reasons]: http://logback.qos.ch/reasonsToSwitch.html "Reasons to prefer logback over log4j"
[slf4j]: http://www.slf4j.org/ "Simple Logging Facade for Java (SLF4J)"
[ste]: http://download.oracle.com/javase/7/docs/api/java/lang/StackTraceElement.html "java.lang.StackTraceElement"
[tower]: http://www.git-tower.com/ "Tower - the most powerful Git client for Mac"

[img-lilith]: https://github.com/huxi/lilith/raw/master/images/lilith.png "Lilith - a logging and access event viewer for Logback, log4j™ and java.util.logging."
