# Lilith
Lilith is a logging and access event viewer for the [Logback](http://logback.qos.ch/), [log4j™](http://logging.apache.org/log4j/) or [java.util.logging](http://download.oracle.com/javase/7/docs/api/java/util/logging/package-summary.html).

It's has features roughly comparable to [Chainsaw](http://logging.apache.org/chainsaw/) for log4j™, but with an emphasis on stability, high performance and throughput. In contrast to Chainsaw, it is handling received logging events using the hard disk instead of keeping them in memory. Because of this, it is able to handle millions of events from several sources at the same time.

## Getting started...

### ...with Logback...

#### ...and logback-classic SocketAppender.

Lilith is listening for logback-classic [`SocketAppender`](http://logback.qos.ch/apidocs/ch/qos/logback/classic/net/SocketAppender.html) connections on port 4560. 

A [`SocketAppender`](http://logback.qos.ch/apidocs/ch/qos/logback/classic/net/SocketAppender.html)  establishes a connection with exactly one host that is defined in the `RemoteHost` property.

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

Using logback-classic `SocketAppender` requires `ch.qos.logback:logback-classic` as runtime dependency.

Take a look at the [Logback manual](http://logback.qos.ch/manual/) and the Lilith help for more informations.

#### ...and Lilith Classic Socket Appender.

TODO

#### ...and logback-access SocketAppender.

Lilith listens for logback-access [`SocketAppender`](http://logback.qos.ch/apidocs/ch/qos/logback/access/net/SocketAppender.html)  connections on port 4570.

A [`SocketAppender`](http://logback.qos.ch/apidocs/ch/qos/logback/access/net/SocketAppender.html)  establishes a connection with exactly one host that is defined in the `RemoteHost` property.

Add the following to your applications `logback-access.xml`:

```xml
<appender name="LogbackAccess" class="ch.qos.logback.access.net.SocketAppender"> 
    <RemoteHost>localhost</RemoteHost>
    <Port>4570</Port> 
    <ReconnectionDelay>170</ReconnectionDelay> 
    <IncludeCallerData>true</IncludeCallerData>
</appender>
<appender-ref ref="LogbackAccess" />
```

Using logback-access `SocketAppender` requires `ch.qos.logback:logback-access` as runtime dependency.

Take a look at the [Logback manual](http://logback.qos.ch/manual/) and the Lilith help for more informations.

#### ...and Lilith Access Socket Appender.

TODO

### ...with log4j.

TODO

### ...with java.util.logging.

TODO

## Building from source

To build Lilith from source, you need to first clone both [huxi/sulky](https://github.com/huxi/sulky) and [huxi/lilith](https://github.com/huxi/lilith).

### Using gradle

Both `sulky` and `lilith` are built using [gradle](http://gradle.org/).

You have two options:

1. install [gradle](http://gradle.org/) on your system (but please make sure that you are using the correct version, most likely the latest)
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

I've already developed a plugin for [IntelliJ IDEA](http://www.jetbrains.com/idea/) (my IDE of choice) myself. It is contained in the `lilith-idea-plugin` folder. This plugin opens a ServerSocket on port 11111 and expects serialized [`StackTraceElement`](http://download.oracle.com/javase/7/docs/api/java/lang/StackTraceElement.html) instances. Lilith sends those events whenever the user clicks on either a callstack, an exception or part of an exception stacktrace. The plugin is doing its best to open the source location of the received element in the IDE.

Unfortunately, I have neither time nor know-how about plugin development for either [Eclipse](http://eclipse.org/) or [Netbeans](http://netbeans.org/). It would be very nice if you could could help me out.

I'd also appreciate any help documenting Lilith.

And I'm always eager to hear your opinion.

## Thanks
Thanks to Ceki Gülcü for developing Logback, the gradle community and all persons that have been involved with Lilith development.

I'd also like to thank the developers of [Marked](http://markedapp.com/) ([@MarkedApp](https://twitter.com/#!/MarkedApp)) for a really helpful, awesome and reasonably priced product.

Similarly, [Tower - the most powerful Git client for Mac](http://www.git-tower.com/) ([@gittower](https://twitter.com/#!/gittower)) is a really nice git client.

And, of course, thanks to the [IntelliJ IDEA](http://www.jetbrains.com/idea/) ([@intellijidea](https://twitter.com/#!/intellijidea)) for the best Java IDE and their support of open source projects.

(This is all free advertisement, I don't get money from any of them.)

## Legal mumbo-jumbo
Apache Extras Companion for Apache log4j, Apache log4j, Apache, the Apache feather logo, the Apache Logging Services project logo, the log4j logo, and the Built by Maven logo are trademarks of The Apache Software Foundation. Oracle and Java are registered trademarks of Oracle and/or its affiliates. Other names may be trademarks of their respective owners.

All parts of Lilith that can be embedded into an application are LGPL/ASL dual-licensed. Lilith itself is GPL-licensed.

All Your Base Are Belong To Us.
