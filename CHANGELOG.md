# Changelog
All notable changes to this project will be documented in this file.

## [8.3.0][unreleased] - TBD

### Changed
- Demand Java 8 `1.8.0_191`.

### Fixed
- Groovy 2.5.5, Spring 5.1.4, commons-lang 3.8.1, jackson 2.9.8, commons-text 1.5, flying-saucer 9.1.16, aspectj 1.9.2, woodstox 5.2.0

## [8.2.0] - 2018-08-09

### Added
- Added "Find previous active" and "Find next active" buttons to toolbar. 
- Added "Find previous" and "Find next" buttons to toolbar.
- Added lots of missing mnemonics.
- If the connection is lost then Message/RequestURI in table will now show "Connection closed." instead of nothing.
- Added smooth horizontal table scrolling option that is enabled by default.
- Added support for Log4j 2 `JsonLayout`, `YamlLayout` and `XmlLayout`. `SerializedLayout` has been deprecated in log4j2 2.9.0 so you should use one of the other options instead.
- Added Automatic-Module-Names for artifacts where appropriate. See [Automatic-Module-Name: Calling all Java Library Maintainers](http://branchandbound.net/blog/java/2017/12/automatic-module-name/).

### Changed
- "Clean all inactive logs" is now less noisy in the Lilith log.
- Changed icons for "Find previous active" and "Find next active". They now differ from "Find previous" and "Find next" as they should.
- Refactored actions and icon handling.
- Don't add null events to global logs.
- Unchecking "Enable global logs." in Preferences is now deleting existing global log files automatically.
- Keyboard help will now always be up-to-date.
- Demand Java 8 `1.8.0_181`.

### Deprecated
- Nothing.

### Removed
- "Previous" and "Next" buttons in find panel.
- "Pause" action. Pausing only paused updating of the table, not receiving of events. This was confusing (even me) and served no actual purpose. This action was a left-over from the early days of Lilith when it was used for debugging during development.

### Fixed
- All L&F support mac screen menu bar with Java 9 or higher.
- Zero-delimited event receivers did not add a `null` event when end of stream was reached.
- Fixed initial enabled state of "Go to source".
- Fixed enabled state of "Edit" menu. Mustn't be disabled anymore because "Paste StackTraceElement" is always available.
- Fixed enabled state of "Copy selection".
- Menu entries related to global logs are now disabled if "Enable global logs." is unchecked in Preferences.
- Added more dependencies and entries to the deserialization whitelist. This is essentially necessary because `logback-access` does not have an `AccessEventVO`. See also [LOGBACK-1182 - Problem deserializing AccessEvent.](http://jira.qos.ch/browse/LOGBACK-1182).
- Not all event producers expect a heartbeat.
- Made sure that "You have changed the look & feel." and "You have changed the application path." dialogs aren't hidden by the preferences dialog.
- Fixed java executable detection in Windows bat file. Thanks, [tha2015](https://github.com/tha2015)!
- Logback 1.2.3, log4j2 2.11.1, Groovy 2.5.1, jackson 2.9.6, spring 5.0.8, protobuf 3.6.1, junique 1.0.4, jcommander 1.72, commons-lang 3.7, commons-text 1.4, commons-io 2.6, flying-saucer 9.1.14, glazedlists 1.10.0, aspectj 1.9.1, httpcore 4.4.10, httpclient 4.5.6, woodstox 5.1.0
- Fixed several split package issues. Because of this, some classes have changed package names:
  - the two most commonly used classes `de.huxhorn.lilith.logback.appender.ClassicMultiplexSocketAppender` and `de.huxhorn.lilith.logback.encoder.ClassicLilithEncoder` have not been moved. 
  - `de.huxhorn.lilith.logback.encoder.AccessLilithEncoder` changed to `de.huxhorn.lilith.logback.encoder.access.AccessLilithEncoder`.
  - `de.huxhorn.lilith.logback.appender.AccessMultiplexSocketAppender` changed to `de.huxhorn.lilith.logback.appender.access.AccessMultiplexSocketAppender`.
  - `de.huxhorn.lilith.logback.appender.ClassicJsonMultiplexSocketAppender` changed to `de.huxhorn.lilith.logback.appender.json.ClassicJsonMultiplexSocketAppender`.
  - `de.huxhorn.lilith.logback.appender.ZeroDelimitedClassicJsonMultiplexSocketAppender` changed to `de.huxhorn.lilith.logback.appender.json.ZeroDelimitedClassicJsonMultiplexSocketAppender`.
  - `de.huxhorn.lilith.logback.appender.ClassicXmlMultiplexSocketAppender` changed to `de.huxhorn.lilith.logback.appender.xml.ClassicXmlMultiplexSocketAppender`.
  - `de.huxhorn.lilith.logback.appender.ZeroDelimitedClassicXmlMultiplexSocketAppender` changed to `de.huxhorn.lilith.logback.appender.xml.ZeroDelimitedClassicXmlMultiplexSocketAppender`.

### Security
- Nothing.

### Known issues
- logback-access `AccessEvent` sent by `SocketAppender` isn't guaranteed to be deserializable at the moment. You can use the Lilith Multiplex Socket Appender in the meantime.
  See [LOGBACK-1182 - Problem deserializing AccessEvent.](http://jira.qos.ch/browse/LOGBACK-1182).
- Logback 1.1.0 introduced some message formatting regressions.
  See [LOGBACK-1183 - Message formatting regression](http://jira.qos.ch/browse/LOGBACK-1183).
- <del>Binary Lilith log files will only work in case of `append=false`.</del> Implemented a workaround.
  See [LOGBACK-1257 - Invalid files in case of append=true and Encoder with non-null headerBytes() / footerBytes()](https://jira.qos.ch/browse/LOGBACK-1257)
- log4j 1 won't be able to send Java 9 `ClassLoaderName`, `ModuleName` and `ModuleVersion` for the call location of the event. Search `LOG4J_MODULE` in the source to take a look at the problem. Since [Log4j 1 End-Of-Life](https://blogs.apache.org/foundation/entry/apache_logging_services_project_announces) has been announced 2015-08-06, chances are pretty slim that this will be fixed. Upgrade to [log4j 2](http://logging.apache.org/log4j/2.x/) or [Logback](https://logback.qos.ch/).
- Flying Saucer related issues:
  - Selection in the HTML view is currently somewhat buggy, especially in case of scaled view.
    See [Issue 79: SelectionHighlighter not compatible with ScalableXHTMLPanel](https://code.google.com/archive/p/flying-saucer/issues/79).
  - Jumping to anchors is currently not supported so the navigation in help and details view isn't as good as it could be.
    See [Issue 105: URLs with anchors](https://code.google.com/archive/p/flying-saucer/issues/105).
- [glazedlists #485 - AutoCompleteSupport: Arrowing down on the popup and pressing enter fails to update combobox](https://github.com/glazedlists/glazedlists/issues/485) is happening in the find panel on macOS. Select the correct entry with the mouse as a workaround.


---

## [8.1.1] - 2017-03-17

### Added
- Added dependency versions to Troubleshooting section of preferences. This was suggested in [issue #29](https://github.com/huxi/lilith/issues/29) by [@Pesegato](https://github.com/Pesegato).

### Changed
- Nothing.

### Deprecated
- Nothing.

### Removed
- Usage of sulky-io IOUtilities.

### Fixed
- cglib 3.2.5, slf4j 1.7.25, logback 1.2.2

### Security
- Nothing.

### Known issues
- logback-access `AccessEvent` sent by `SocketAppender` isn't guaranteed to be deserializable at the moment. You can use the Lilith Multiplex Socket Appender in the meantime.
  See [LOGBACK-1182 - Problem deserializing AccessEvent.](http://jira.qos.ch/browse/LOGBACK-1182).
- Logback 1.1.0 introduced some message formatting regressions.
  See [LOGBACK-1183 - Message formatting regression](http://jira.qos.ch/browse/LOGBACK-1183).
- <del>Binary Lilith log files will only work in case of `append=false`.</del> Implemented a workaround.
  See [LOGBACK-1257 - Invalid files in case of append=true and Encoder with non-null headerBytes() / footerBytes()](https://jira.qos.ch/browse/LOGBACK-1257)
- log4j 1 won't be able to send `ClassLoaderName`, `ModuleName` and `ModuleVersion` for the call location of the event. Search `LOG4J_MODULE` in the source to take a look at the problem. Since [Log4j 1 End-Of-Life](https://blogs.apache.org/foundation/entry/apache_logging_services_project_announces) has been announced 2015-08-06, chances are pretty slim that this will be fixed. Upgrade to [log4j 2](http://logging.apache.org/log4j/2.x/) or [Logback](https://logback.qos.ch/).
- Flying Saucer related issues:
  - Selection in the HTML view is currently somewhat buggy, especially in case of scaled view.
    See [Issue 79: SelectionHighlighter not compatible with ScalableXHTMLPanel](https://code.google.com/archive/p/flying-saucer/issues/79).
  - Jumping to anchors is currently not supported so the navigation in help and details view isn't as good as it could be.
    See [Issue 105: URLs with anchors](https://code.google.com/archive/p/flying-saucer/issues/105).
- [GLAZEDLISTS-469 - AutoCompleteSupport: Arrowing down on the popup and pressing enter fails to update combobox](https://java.net/jira/browse/GLAZEDLISTS-469) is happening in the find panel on macOS. Select the correct entry with the mouse as a workaround.


---

## [8.1.0] - 2017-03-15

### Added
- Added `TemporalAccessor` support to `SafeString`.
- Added log4j2 `Marker` support.
- Added "Copy logger name" accelerator "command shift N".
- Added "Copy message" accelerator "command shift C".
- Added "Copy message pattern" accelerator "command shift alt C".
- Added "Copy Throwable name" action with accelerator "command shift alt T".
- Added "Throwable" condition.
- Added "Focus Throwables"/"Exclude Throwables" matching events with any Throwable.
- Added "Focus Throwable"/"Exclude Throwable" matching events with a specific Throwable class name.
- Added "Copy call location" accelerator "command shift S".
- Added "Copy call stack" accelerator "command shift alt S".
- Added "Copy request URL".
- Added "Copy request headers".
- Added "Copy request parameters".
- Added "Copy response headers".
- Added thread priority to thread info of logging event.
- Added "Copy thread name".
- Added "Copy thread group name".
- Added "ThreadName" condition.
- Added "ThreadGroupName" condition.
- Added "Focus Thread name"/"Exclude Thread name".
- Added "Focus Thread group name"/"Exclude Thread group name".
- Added "Focus Request URI"/"Exclude Request URI" menus.
- Added lots of filter tests.
- Added "Focus Request Parameter"/"Exclude Request Parameter".
- Added "Focus Request Header"/"Exclude Request Header".
- Added "Focus Response Header"/"Exclude Response Header".
- Added alternative behavior for "MDC", "Request Parameter", "Request Header" and "Response Header" Focus/Exclude menu items. Pressing `Alt` while selecting Action will match any value for the given key.
- Added support for `ClassLoaderName`, `ModuleName` and `ModuleVersion` in Java 9 `StackTraceElement`.
- Added some more tips.
- Added "Show unfiltered" accelerator "command U". Also added the action to the "Search" menu.
- Added "Go to source" accelerator "command D". Also added the action to the "Edit" menu.

### Changed
- Using `java.time.format` instead of `SimpleDateFormat`.
- Don't create copy of whitelist Set in `WhitelistObjectInputStream`.
- Demand Java 8 `1.8.0_121`.
- Added option to start application even if Java version requirements are not met.
- Changed accelerator of "Copy Throwable" from "command shift alt T" to "command shift T".
- Changed specification of HTTP status code 451 from draft to RFC 7725.
- Changed meaning of `ThrowableCondition`. Condition now evaluates to true if search string is one of the `Throwable` contained in the hierarchy (including cause, suppressed) instead of only checking the root exception.
- `EventContainsCondition` is now evaluating `Throwable`, NDC and any contained `StackTraceElement` values.
- `appendTo` methods of `ThrowableInfo` and `ExtendedStackTraceElement` now throw a `NullPointerException` if `StringBuilder` is `null`.
- `ExtendedStackTraceElement.appendExtended` is now private.
- `ExtendedStackTraceElement.toString(true)` is now printing `"na"` instead of empty string if codeLocation or version is null.
- `HttpStatusTypeCondition` understands more input. "SUCCESSFUL", "su", "2", "2X" and " 2x " will all evaluate to `HttpStatus.Type.SUCCESSFUL`.
- `CallLocationCondition` understands more input. "at " and whitespace is now automatically removed.
- `HttpRemoteUserCondition` is less strict. String is first trimmed for both condition and remote user of event. Empty string and "-" are both considered "no user name" and the condition matches accordingly.
- `MDCContainsCondition` without value will now match if the MDC of an event contains any value (even null) for the given key.
- `SafeString`/`MessageFormatter` changes. Those only have an effect if Lilith appenders are used.
   - `String` instances contained in `Collection`, `Map` or `Object[]` are now wrapped in apostrophes. This means that an empty `Set` will look differently than one containing an empty `String`. Similarly, a `null` element will look differently than `'null'`.
   - `Map` instances are now formatted in Groovy style (`[key:value, key2:value2]`) instead of Java style (`{key=value, key2=value2}`).
   - `byte[]`, `Byte[]` and `Byte` are now converted to hex values. Because `[0xCA, 0xFE, 0xBA, 0xBE]` has better readability than `[-54, -2, -70, -66]`.
   - This is not a compatibility contest. It's about usability.
- "Paste StackTraceElement" (command shift V) is now much more effective. It parses the text from the clipboard and opens the first `StackTraceElement` it finds in IDEA (if the necessary IDEA plugin is installed). Parsing is much more lenient.
- Using `SafeString` for "Copy MDC".
- Details view is now showing all available thread information.
- XML is now handled by Woodstox.
- Moved "Logger" in "Focus"/"Exclude" menus from bottom to top.
- `applicationUUID` is now actually a [ULID](https://github.com/alizain/ulid). The existing methods `setCreatingUUID`/`isCreatingUUID` and `getUUID` in the multiplex appenders are not renamed for compatibility reasons. ULID generation is handled in the new `de.huxhorn.sulky:de.huxhorn.sulky.ulid` module.
- Activated automatic graphics switching on Mac, i.e. don't demand the high-performance, energy-hungry GPU. This fixes [issue #27](https://github.com/huxi/lilith/issues/27). Thanks to Nikita Belenkiy for the detailed issue.
- Enhanced preferences dialog. This fixes [issue #17](https://github.com/huxi/lilith/issues/17).

### Deprecated
- Nothing.

### Removed
- Removed `SourceInfo` and related classes. They were all unused and also terrible.
- Removed `LoggingEvents` and related classes. Same as above.
- Removed `AccessEvents` and related classes. Same as above.
- Removed unused methods `getTextColor()`, `getBackgroundColor()` and `getBorderColor()` of class `SavedCondition`.
- Removed unused c'tors in `AbstractFilterAction`, `AbstractLoggingFilterAction` and `AbstractAccessFilterAction`.
- Removed RRD statistics.
- Removed unused SenderService and jMDNS.
- Removed unused and broken `UserNotificationLoggingEventHandler` and `UserNotificationAccessEventHandler`.
- Removed macify to ensure Java 9 compatibility.
- Removed `de.huxhorn.lilith.jul-slf4j-handler`. Use `org.slf4j:jul-to-slf4j` and enable `ch.qos.logback.classic.jul.LevelChangePropagator` instead.
- Removed Substance look & feel. The [Insubstantial](https://github.com/Insubstantial/insubstantial/) fork is broken in Java 9 and isn't maintained anymore. The just-revived original [substance](https://github.com/kirill-grouchnikov/substance) won't provide Maven artifacts because those are just ["the latest chic"](https://github.com/kirill-grouchnikov/substance/issues/20#issuecomment-282442844).
- Removed JGoodies look & feel. The current version is broken in Java 9 and the next version will be closed-source and payware.

### Fixed
- Make frames entirely visible after selecting them from the Windows Menu.
- Added some more classes to deserialization whitelist. The missing classes prevented deserialization of some log4j2 events. This fixes [issue #21](https://github.com/huxi/lilith/issues/21).
- Added another class to deserialization whitelist. The missing class prevented deserialization of logback 1.2.x events.
- Fixed NPE in LoggingEventProtobufEncoder. This fixes [issue #22](https://github.com/huxi/lilith/issues/22).
- Fixed NPE in CheckForUpdateRunnable in case of broken network connection.
- Fixed handling of invalid XML created by `java.util.logging.XMLFormatter`. This fixes [issue #26](https://github.com/huxi/lilith/issues/26).
- Being less strict about the required Java version. It seems certain Linux distros have a Java version string like `1.8.0_66-internal` which is - strictly speaking - less than `1.8.0_66` since `-internal` is a pre-release identifier. Lilith will now accept versions like this if ignoring the pre-release identifier satisfies the version requirement.
- Making sure selected event is reset when last view is closed.
- log4j2 2.8.1, slf4j 1.7.24, Logback 1.2.1, jackson 2.8.7, Spring 4.3.7, Groovy 2.4.9, aspectj 1.8.10, httpclient 4.5.3, httpcore 4.4.6, commons-io 2.5, Thymeleaf 2.1.5, protobuf 3.2.0, jcommander 1.64, commons-lang3 3.5, flying-saucer 9.1.4

### Security
- Nothing.

### Known issues
- logback-access `AccessEvent` sent by `SocketAppender` isn't guaranteed to be deserializable at the moment. You can use the Lilith Multiplex Socket Appender in the meantime.
  See [LOGBACK-1182 - Problem deserializing AccessEvent.](http://jira.qos.ch/browse/LOGBACK-1182).
- Logback 1.1.0 introduced some message formatting regressions.
  See [LOGBACK-1183 - Message formatting regression](http://jira.qos.ch/browse/LOGBACK-1183).
- <del>Binary Lilith log files will only work in case of `append=false`.</del> Implemented a workaround.
  See [LOGBACK-1257 - Invalid files in case of append=true and Encoder with non-null headerBytes() / footerBytes()](https://jira.qos.ch/browse/LOGBACK-1257)
- log4j 1 won't be able to send `ClassLoaderName`, `ModuleName` and `ModuleVersion` for the call location of the event. Search `LOG4J_MODULE` in the source to take a look at the problem. Since [Log4j 1 End-Of-Life](https://blogs.apache.org/foundation/entry/apache_logging_services_project_announces) has been announced 2015-08-06, chances are pretty slim that this will be fixed. Upgrade to [log4j 2](http://logging.apache.org/log4j/2.x/) or [Logback](https://logback.qos.ch/).
- Flying Saucer related issues:
  - Selection in the HTML view is currently somewhat buggy, especially in case of scaled view.
    See [Issue 79: SelectionHighlighter not compatible with ScalableXHTMLPanel](https://code.google.com/archive/p/flying-saucer/issues/79).
  - Jumping to anchors is currently not supported so the navigation in help and details view isn't as good as it could be.
    See [Issue 105: URLs with anchors](https://code.google.com/archive/p/flying-saucer/issues/105).
- [GLAZEDLISTS-469 - AutoCompleteSupport: Arrowing down on the popup and pressing enter fails to update combobox](https://java.net/jira/browse/GLAZEDLISTS-469) is happening in the find panel on macOS. Select the correct entry with the mouse as a workaround.


---

## [8.0.0] - 2015-11-15

### Added
- `CHANGELOG.md` in the spirit of [Keep a CHANGELOG](http://keepachangelog.com/).
- Rough `TODO.md` listing some things that should be done.
- Error dialog if Lilith is started with Java prior to `1.8.0_66`.
- Added "Clear view" shortcut Cmd-K. K for clear.
- Added lilith.version.bundle to system properties.
- Displaying release notes of "newzest version" if already available.
- Option to ignore the secondary identifier of event sources, ignoring by default.

### Changed
- Requires Java 8.
- left-aligned most table cells.
- Better view icons in `Window` menu. They now represent the state of the view, e.g. whether the connection is still alive and if a window of the view is already open.
- Using `EventQueue` instead of `SwingUtilities`.
- Using `java.time.format` instead of `SimpleDateFormat` or `joda-time`.
- Renamed "Previous tab" to "Previous view" and "Next tab" to "Next view".
- Changed "Next view" shortcut from Cmd-K to Cmd-J and "Previous view" shortcut from Cmd-J to Cmd-shift-J.
- Switched "Next view" and "Previous view" in "View" menu.
- Better error message in case of broken detailsView.
- `SimpleSendBytesService.DEFAULT_POLL_INTERVALL` renamed to `SimpleSendBytesService.DEFAULT_POLL_INTERVAL`.
- Removed `BufferedOutputStream` wrapper in `SocketDataOutputStreamFactory`. `BufferedOutputStream` prevented `TimeoutOutputStream` from working reliably.
- `reconnectionDelay` in multiplex appenders is now `long` instead of `int`.
- Using Groovy for Lilith logging configuration.
- Recompressed images with latest [ImageOptim](https://imageoptim.com/) version.
- Better app icon on Mac. Thanks to Christian Balog!
- Tried to ensure that license dialog is always visible.
- Changed default of `Show primary identifier even for named sources.` preferences to `false`.
- Enhanced `Open inactive log...` dialog.

### Deprecated
- Nothing.

### Removed
- Some `HttpStatus` enum values have been renamed. Code explicitly using them would need to be changed.
- `TroubleshootingPanel.reset(ViewContainer<?> container)`
- joda-time dependency.
- stax-api and stax dependencies.

### Fixed
- Updated `HttpStatus` enum to [RFC 7231](https://tools.ietf.org/html/rfc7231), [RFC 7232](https://tools.ietf.org/html/rfc7232), [RFC 7233](https://tools.ietf.org/html/rfc7233), [RFC 7235](https://tools.ietf.org/html/rfc7235) and [RFC 7238](https://tools.ietf.org/html/rfc7238). Some enum values have been renamed in the process.
- sulky `SafeString` is now always printing a `Date` as an ISO8601-DateTime with timezone UTC.
- Using "127.0.0.1" instead of "localhost" in `SerializingGoToSource` to prevent IPv6 SNAFU.
- Conditions-Focus/Exclude menu of detached windows are now updated on saved condition change.
- Preventing useless focus traversal warnings in EventWrapperViewPanel and FindPanel.
- Reduced log level of broken stream message in event producers.
- Fixed initialization of "Attach/Detach" action.
- Fixed statistics in case of Java != 1.6
- Fixed "Focus" and "Exclude" menu tooltips on Mac. Those displayed HTML source if system menu bar was used.
- Fixed [Groovy](http://groovy-lang.org/) links in help.
- SLF4J 1.7.13, Logback 1.1.3, Spring 4.2.2, Groovy 2.4.5, commons-codec 1.9, commons-lang3 3.4, httpclient 4.5.1, httpcore 4.4.4, jackson 2.6.3, log4j2 2.4.1, aspectj 1.8.7, Thymeleaf 2.1.4, substance 7.3, protobuf 2.6.1, jcommander 1.48, glazedlists 1.9.1, servlet-api 3.1.0, flying-saucer 9.0.6, cglib 3.1, rrd4j 2.2.1

### Security
- Keep your Java version up-to-date. Lilith now demands the latest Java version.
- Implemented whitelisting of classes allowed for deserialization to circumvent issues described in [AppSecCali 2015: Marshalling Pickles - how deserializing objects will ruin your day](http://frohoff.github.io/appseccali-marshalling-pickles/).
  - [Apache Commons statement to widespread Java object de-serialisation vulnerability](https://blogs.apache.org/foundation/entry/apache_commons_statement_to_widespread)
  - [What Do WebLogic, WebSphere, JBoss, Jenkins, OpenNMS, and Your Application Have in Common? This Vulnerability.](http://foxglovesecurity.com/2015/11/06/what-do-weblogic-websphere-jboss-jenkins-opennms-and-your-application-have-in-common-this-vulnerability/)
  - [Look-ahead Java deserialization](http://www.ibm.com/developerworks/library/se-lookahead/)


---

## [0.9.44] - 2014-04-21

### Added
- Added alternative behavior for Focus/Exclude actions.
  By default, those actions are replacing the current views filter, if available, with the new combined filter. Hold shift to create a new view instead.
- Added corresponding "Focus" and "Exclude" menus to the "Search" menu.
- Added two Substance look&feels to the mix.
- Added "Paste StackTraceElement" (Cmd-shift V) which opens the respective source code in the IDE, if a proper plugin is installed.
- Added fishing-for-compliments technology.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- The table of the view will now always receive the focus if the selected view changes.
- Status text is now properly updated in case of a replaced filter. This was a bug in the sulky `BufferTableModel`.
- Renamed "Named" in the find combo to "Saved".
- Renamed "Add condition..." to "Save condition..." and moved it from the "View" to the "Search" menu. Also added it to the popup menu.
- Significantly enhanced tooltips of various condition-related components. They now show a pretty-printed string representation of the condition.
- Enhanced "Focus" and "Exclude" popup menus.
- Status text of main window is now properly updated on change of white/blacklisted list name.
- Enhanced profiling output of TracingAspect.
- Fixed of-by-one error in message renderer `[+x lines]`. Again.
- `servlet-api` dependency of `de.huxhorn.lilith.logback.servlet` is now "provided".
- `ClassicLilithEncoder` is now `Encoder<ILoggingEvent>` instead of `Encoder<LoggingEvent>`.
- Fixed suppressed handling while parsing Throwables. This is also a partial workaround for [LOGBACK-328 - XMLLayout has several inconsistencies as compared to the log4j XMLLayout it emulates](http://jira.qos.ch/browse/LOGBACK-328).
- Groovy 2.2.2, Logback 1.1.2, SLF4J 1.7.7, Jackson 2.3.3, log4j2 2.0-beta9, rrd4j 2.2, aspectj 1.8.0, flying-saucer 9.0.4, Spring 4.0.2, macify 1.6, httpcore 4.3.2, httpclient 4.3.3, JCommander 1.35, servlet-api 3.0.1, jgoodies-looks 2.5.3, commons-lang3 3.3.2


---

## [0.9.43] - 2013-04-29

### Added
- Added ability to reconnect views after disconnect. All multiplex appenders are now creating a `UUID` upon startup and add that id to the logger context.
  To revert to the old behavior, i.e. one view for every single connection, you can disable usage of `UUID` in the configuration of the appender.
  `<CreatingUUID>false</CreatingUUID>`
  This was suggested by Joe.
- Added HTTP status codes 208, 226, 308, 420, 451, 508, 598 & 599.
- Added `filter` command that filters all events of an input file that match a given condition into an output file. This is faster than filtering in Lilith itself since the UI isn't eating up CPU while filtering. This was suggested by Joe.
- Added command line option to specify a custom logback configuration.
- Added minimal logback configuration for command line commands.
- Added support for log4j2.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Logback 1.0.12 introduced a change that broke `LogbackShutdownServletContextListener`. Implemented `ContextHelper` to be compatible with both 1.0.12 and lower.
- SLF4J 1.7.5, Logback 1.0.12, log4j 1.2.17, log4j2 2.0-beta9, Groovy 2.1.3, commons-io 2.4, commons-lang3 3.1, httpcore 4.2.4, httpclient 4.2.5, aspectj 1.7.2, jackson 2.2.0, jcommander 1.30, Spring 3.2.2.RELEASE, protobuf-java 2.5.0, jgoodies-looks 2.5.2, glazedlists 1.9.0, cglib 3.0, junit 4.11


---

## [0.9.42.1] - 2012-03-12

### Added
- Nothing.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Fixing broken dependencies in poms.


---

## [0.9.42] - 2012-03-12

### Added
- Added support for new Java SE 7 try-with-resources statement suppressed Throwables. See [Better Resource Management with Java SE 7: Beyond Syntactic Sugar](http://www.oracle.com/technetwork/articles/java/trywithresources-401775.html).
- Added "Focus..." and "Exclude..." menus to popup.
- Added "Copy event as JSON" and "Copy event as XML". This was requested by Joe.
- Added `lilith.timestamp.milliseconds` system property.
- Added option for "wrapped exception style" in details view. This was suggested by Tomasz Nurkiewicz in [LOGBACK-547 -  Exception stack trace printing starting from root cause](http://jira.qos.ch/browse/LOGBACK-547) .
- Added nottingham-draft HTTP status codes. See [Additional HTTP Status Codes - draft-nottingham-http-new-status-04](https://tools.ietf.org/html/draft-nottingham-http-new-status-04).

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- `tail` and `cat` are now fully supporting both `LoggingEvent` and `AccessEvent`.
- Restored Logback `SocketAppender` in Lilith logging configuration. It's now triggered during debug actions, again.
- After many runs that replace opened log file, Updating task failed with "Negative seek offset". This was reported by Jeff Jensen (jeffjensen).
- "Edit", "Copy..." and "Filter..." menus are only enabled if it makes sense.
- Preventing multiple warning log-entries in case of broken groovy files (`detailsView.groovy`, Conditions, `ClipboardFormatter`). Instead, a warning is only emitted once for every file change. Instances aren't recreated in that case, either, so this also enhances the performance and lowers the CPU usage during general use.
- Views are now properly updated upon preferences change.
- Changed `NDC` to use varargs.
- Relaxed namespace handling of all XML readers. This enables retrieval of old (1.3) Lilith XML Events. This fixes a problem reported by Ekkehard Gentz.
- All KeyStrokes are now managed globally. This includes validation of the used KeyStrokes which detected some mistakes:
    - "Close all tabs." is now Ctrl+Alt+Shift+ W.
    - "Find previous active match" is now Ctrl+L.
    - "Find next active match" is now Ctrl+Shift+L.
- "Copy Throwable" does now have the shortcut "command shift alt T". This was suggested by snstanton.
- SLF4J 1.6.4, Logback 1.0.1, Groovy 1.8.6, commons-lang 3.0.1, protobuf-java 2.4.1, jackson 1.9.2, jcommander 1.23, aspectj 1.6.11, cglib 2.2.2, httpclient 4.1.2, httpcore 4.1.3, commons-codec 1.5, JUnit 4.10, Spring 3.1.1.RELEASE


---

## [0.9.41] - 2011-05-02

### Added
- Minimize to system tray.
  Added support for (optional, default is on) system tray icon. Double-clicking the icon hides/shows all windows. The menu also contains a Quit action. If system tray icon is active (supported and enabled) then closing the main frame does not exit the application. This will now hide all windows, instead. This was requested by Adrien Sales and Joe.
- Implemented custom "Copy to clipboard" functionality using Groovy. This was suggested by Joe.
- Added support for cat/tail of Lilith AccessEvent files.
- Importing gzipped java.util.logging or log4j xml is now supported.
- Using JComboBox & CardLayout instead of JTabbedPane in Preferences Dialog.
- Added messagePattern.contains condition and "Copy message pattern" action. Using message pattern for filtering is faster than using message.
- Using  Mensch font by @robey (Robey Pointer) as the monospace font of the HTML View.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- The NDC behavior has changed a bit since it does not inherit the NDC  of the parent threads anymore. This is actually a better behavior. Inheriting does not make much sense for an NDC - in contrast to MDC where it is very helpful - since it resembles a contextual "stacktrace".
  I never documented the previous behavior, anyway. Problem? ;)
- Fixed two classloader-leak-issues that prevented proper unloading of webapps upon undeploy/redeploy. This issue was discovered and reported by Andy Goossens. Thank you very much!
    1. The threads startet by the Lilith multiplex appenders did stop but I didn't bother to wait until they finished doing so. This has been fixed now. All threads are destroyed before stop() returns.
    2. I had an issue in the NDC implementation that was very comparable to the one that caused LBCLASSIC-183. I was putting an instance of a class loaded by the webapp classloader into a ThreadLocal. I fixed this by using two ThreadLocals, a `ThreadLocal<String>` and a `ThreadLocal<String[]>`, instead of my own class.
- Better generation of message tooltip. Content is not tailed anymore.
- Colors of logging level and access status type are now configurable.
- "Go to Source" is now executed asynchronously.
- Detached windows were using the popup menu of the main frame. They are now using the popup menu of the detached frame as originally intended. This bug was found by Dimi.
- HTML View is now anti-aliasing the fonts.
- Logback 0.9.28, AspectJ 1.6.10, Jackson 1.7.1, JCommander 1.17, JGoodies Looks 2.2.2, Groovy 1.8.0, commons-lang 2.6, commons-io 2.0.1, httpclient 4.1, httpcore 4.1


---

## [0.9.40] - 2010-11-11

### Added
- Color-Schemes (as used by saved conditions, for example) can now be defined partially, i.e. one condition can set a border while the text-color might be defined by a different condition.
- Added preferences to check for pre-release versions in addition to release versions. Off by default.
- Added -T/--print-timestamp command line argument that prints the timestamp and date of build.
- SNAPSHOT pre-releases will now contain the date and time of the build in the window title.
- Added support for java.util.logging SocketHandler. Lilith is listening on port 11020 for incoming connections. I'd still recommend to switch to SLF4J/Logback, though!
- Added JSON-Appenders and JSON-Receivers.
  Lilith is listening for message-based JSON-Events on port 10030 (uncompressed) and 10031 (compressed). Zero-delimited JSON-Events are consumed on port 11010.
- Added support for Log4j SocketAppender. Lilith is listening on port 4445 for incoming connections. I'd still recommend to switch to SLF4J/Logback, though!
- Added "Find previous active" (*command* T) and "Find next active" (*command* shift T) functionality to quickly jump to events that match any active condition.
- Added "Export" functionality which exports the currently selected view into a Lilith file.
- Added default condition name to preferences. It's preselected for every new view. This was suggested by Lothar Cezanne.
- Added --export-preferences `<file>` and --import-preferences `<file>` commandline options to be able to export and import all preferences. This was requested by Gareth Doutch (gdoutch).
- Added an additional executable lilith-all.jar containing all dependencies as another download option. This was requested by Adrien Sales.
- Added ability to define a sound for WARN-level events, but without any assigned default sound. This was requested by Adrien Sales.
- Added F1 as shortcut for Help. This was requested by Adrien Sales.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Maps, e.g. MDC, are now sorted by key-value in the details view. This was suggested by Joe.
- If the used version is a SNAPSHOT, Lilith will always check for a new SNAPSHOT pre-release regardless of the "Check for updates on startup." settings.
- Enhanced "Check for update" functionality.
  No more false update warnings in case of SNAPSHOT pre-releases. A new release takes precedence over a new pre-release.
- Replaced Commons-CLI with JCommander. Very nice replacement.
  I'd like to take this opportunity to thank Cédric Beust for his great (and fast!) support. You should seriously check JCommander out if your application is handling command line arguments at all.
- Fixed glitch in case of "Find next"/"Find prev" while search is already running. Additional searches are now ignored.
- Polling events every 100ms instead of every 2s. This results in less congestion and a much snappier feeling.
- Fixed a caching issue that was likely responsible for some oddity. Again, in case of files that are recreated by `<append>false</append>`. This issue was raised by Jeff Jensen (jeffjensen).
- Fixed another issue that could lead to exception while checking for update of files that are recreated by `<append>false</append>`. This issue was raised by Jeff Jensen (jeffjensen).
- Fixed issue that could lead to exception while checking for update of files that are recreated by `<append>false</append>`. This issue was raised by Jeff Jensen (jeffjensen).
- Fixed the regression that the internal Lilith log wouldn't show up in the menu anymore.
- Changed update interval of opened files from 5 seconds to 1 second.
- Spring 3.0.5.RELEASE, Groovy 1.7.5, SLF4J 1.6.1, 0.9.26


---

## [0.9.39] - 2010-05-12

### Added
- Added some error messages.
- Added "recent files" menu. This was requested by Jeff Jensen (jeffjensen).
- Added special icons for views opened from a file, different ones for refreshing (Lilith) and non-refreshing (imported) ones. They arguably suck a little bit. ;)
- Added automatic refreshing of opened Lilith files. This should work but isn't tested very thoroughly, yet. This was requested by Jeff Jensen (jeffjensen).
- Added cat (-c) and tail (-t, -f) functionality to Lilith so it's possible to print entries of binary Lilith log-files to the console. Number of printed entries can be configured using -n. The format string of for the event can be given at runtime.
  Unfortunately, this is only possible with Logback-Classic, not Logback-Access. The reason for this shortcoming is [LOGBACK-322 - Please add IAccessEvent (and AccessEventVO) similar to the LoggingEvent counterparts ](http://jira.qos.ch/browse/LOGBACK-322).
  The following parts of LoggingEvent aren't supported yet:
    - LoggerContextVO
    - IThrowableProxy
    - Marker
- Added option to maximize internal frames by default. This is actually very good idea! This was suggested by snstanton.
- Added contextName and/or applicationIdentifier to the title of the frames. This was suggested by Alfred & Joe.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- lilith.bat does not strictly require a %JAVA_HOME% environment variable anymore. This was requested by Gareth Doutch (gdoutch).
- Removed LogbackClassic appender from default logback.xml of Lilith to prevent warning during startup. Those would be especially annoying in case of cat or tail.
  This is a workaround for [LOGBACK-628 - SocketAppender is reporting an WARN if connection could not be established](http://jira.qos.ch/browse/LOGBACK-628).
- Changed autocomplete of find combos from uber-annoying swingx to much less annoying Glazed Lists.
  This annoyed Alfred and me.
- Neither "Window" menu nor status bar did update properly if "Automatically open new views on connection." was deselected. This bug was found by Joe.
- Changed the license of all files previously licensed as LGPLv3 to both LGPLv3 and ASLv2 instead. Use whichever license suits you better.
  This was requested by Ekkehard Gentz.
- It wasn't possible to cancel an exit-request executed by closing the main frame. This bug was found by Gareth Doutch (gdoutch).
- Replaced all `<layout>` with `<encoder>` in logback*.xml. See [Logback error messages and their meanings](http://logback.qos.ch/codes.html#layoutInsteadOfEncoder) for info.
- Logback 0.9.21, SLF4J 1.6.1, commons-cli 1.2, httpclient 4.0.1, Groovy 1.7.4, JCommander 1.4


---

## [0.9.38] - 2010-03-26

### Added
- Added help about using Lilith encoders in FileAppender.
- Added detection of outdated index files and the option to reindex a log file in that case. This is helpful while reopening log files created by a FileAppender.
- Added new modules `de.huxhorn.lilith.logback.encoder.classic` and `de.huxhorn.lilith.logback.encoder.access` to support writing of Lilith logfiles using Logback `FileAppender`.
  Thanks to Ceki for supporting this!
- Added some more tips.
- Added shortcut for "Close all" action.
- Added the ability to clear a filtered view. Doing this clears the original view and resets the filtered view. This was suggested by Joe.
- Added "Uncaught Exception" debug action.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Views aren't opened anymore in case of connection closed for black- or not whitelisted sources. This was reported by Vlad Satanovski.
- Moved `BufferTableModel` to new module sulky-buffers-table.
- Moved `RowBasedTableModel` and PersistentTableColumnModel to module sulky-swing.
- Implemented the workaround for Apple Problem ID #7240026 suggested by GalaJon. Thanks a lot! See http://bit.ly/5vF5M2
- Of-by-one error in message renderer [+x lines].
- Lilith log is now showing uncaught exceptions as errors.
- The find panel has been heavily refactored.
  Instead of a text-field it now features a combobox containing the previous 15 searches that were used for filtered views. Additionally, there's a new filter type "Named" that populates the combobox with named/saved conditions. Similarly, selecting "Level>=" populates the combobox with the correct values from TRACE to ERROR. The comboboxes, both filter type selection and filter text, feature autocompletion of entered text.
  This was suggested by Joe.
- Groovy 1.7.0, Logback 0.9.19, SLF4J 1.5.11, Protobuf 2.3.0


---

## [0.9.37] - 2009-11-11

### Added
- Prevented duplicate start of application by the same user.
  Duplicate startup by a different user is still possible but quite useless until the event receivers are configurable. This was requested by Joe.
- Added option to disable the creation of statistics.
- Added "Copy call location" action. This is quite useful in combination with the CallLocation condition.
- Added CallLocation condition that returns true if the first element of the call stack equals the given `StackTraceElement`.
- Added option to disable global logs. This was requested by Joe.
- The Appenders are now supporting the "Adder-Idiom" that is common for Logback appenders. You can now use multiple `<RemoteHost>` tags to define the remote hosts.
- Implemented optional "Tip of the Day" dialog.
- Added "Level>=" as a native default condition to increase performance.
- Preferences dialog can now be closed by pressing Enter. This was suggested by Vlad Satanovski.
- Added "Execute GC" to Troubleshooting.
- Added option to show/hide the statusbar.
- Added option to show/hide the toolbar. This was suggested by Scott Stanton, Ekkehard Gentz and Joe.
- Added "Context" column for logging events.
- Added "Troubleshooting..." menu item in "Help" menu as a shortcut to the throubleshooting section of Preferences.
- Added new command "Copy Properties" to Troubleshooting section of Preferences.
- Added persistent zoom-in (*command* .) and zoom-out (*command* shift .) of details view.

### Deprecated
- Nothing.

### Removed
- Removed useless Statistics action for current source. Statistics can be obtained using "Window" &gt; "Statistics".

### Fixed
- Change of active Conditions was not repainting views. This was only the case in internal betas. Reported by Alfred.
- Removed additivity from the debug loggers. Debug logger events won't show up in the Lilith log or the console.
- Enhanced debug dialog. Better layout and tooltips.
- Preventing saved conditions with duplicate name or condition.
- Replaced JCheckboxMenuItem in Window menu with a JMenuItem containing an Icon if the entry has a frame.
- Toolbar of mainframe was not switched active after attach.
- Toolbar of mainframe stayed active after detach.
- Removed appBuildNumber from title. It was annoying at best.
- Enhanced "Check for update" dialog.
- Copy actions are now working correctly in case of external frames. This was reported by Matthias "Alfred" Neumann.
- Jumping to the unfiltered event didn't work the first time if Tail was enabled. This was reported by Joe.
- Table and message view are now having reasonable preferred sizes (needed because of splitpane).
- Replaced tabs of event view with a combobox.
  The combobox is only visible if there are filtered views. There's also more room for the condition string. Something like this was suggested by Scott Stanton and Joe.
- Updated keyboard help. Better symbol for "Ctrl"/"cmd".
- Events did sometimes show up twice in filtered views. This was caused by a classic off-by-one error. Reported by Joe.
- Fixed a remaining hang in the multiplex appenders in case of certain network problems.
- Updated protobuf to 2.2.0.
- Updated Groovy to 1.6.5.
- Updated Logback to 0.9.17.
  This fixes an issue that was caused by a Logback bug. Thanks for the fast fix, Ceki!


---

## [0.9.36] - 2009-07-20

### Added
- Added new messageRegex example groovy condition.
- Implemented Drag and Drop of Lilith files.
- Option to disable splash screen. This was requested by Thorbjoern Ravn Andersen.
- Option to ask before exit. This was requested by Lothar Cezanne.
- Added "Startup & Shutdown" pane to preferences dialog.
- Added "Troubleshooting" pane to preferences dialog.
- Added new option "Show stacktrace of Throwables" so non-technical people can disable it to just see the exception and, if available, message. Clicking on the name or message of an exception will now also open the code in IDEA.
- Added option to color the entire row of a table according to the level/status of the event as requested by Scott Stanton.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Added missing contact page to help.
- Some locks and blocking queues are fair now.
- Changed shortcut of "Import" to "command shift I". It collided with "Add condition...", previously. This was reported by Scott Stanton.
- MultiplexSocketAppenderBase is now extending UnsynchronizedAppenderBase instead of AppenderBase, as suggested by Maarten Bosteels and Ralph Goers.
- "Copy call stack" and "Copy Throwable" are now prepending the `StackTraceElement`s with "\tat " as requested. The detailsView is also displaying "at " before any `StackTraceElement`.
- Updated Groovy to 1.6.3
- Changes to support Logback 0.9.16. Updated SLF4J to 1.5.8.


---

## [0.9.35] - 2009-05-01

### Added
- Added header to Lilith file format.
- Using protobuf for the Lilith file format and transfer. This results in big performance improvements.
- Opening *.lilith files
- Importing *.lilith files without *.index.
- Import of LOG4J XML files.
- Import of java.util.logging XML files.
- Added timeStampMillis to Lilith XML Event.
- LoggingEvent: added ThreadInfo.
- LoggingEvent: added LoggerContext.
- Added Copy action for selected content in details view.
- Added Copy action for selected content in help.
- Install example groovy conditions if conditions folder is newly created.
- Implemented a task-manager for searching, filtering and importing.
- Added support for NDC to LoggingEvent datatype.
- Created NDC for use with Lilith appenders.
- Added new table row "NDC" to logging table.
- NDC support in detailsView.groovy
- Added "Copy MDC" and "Copy NDC" actions.
- Added icon for mainframe and Mac app.
- Added output including port number in case of BindException during startup.
- Added progress indicator to statusbar in case of running tasks.

### Deprecated
- Dedicated sulky-tasks module. Deprecated previous implementation.

### Removed
- Nothing.

### Fixed
- SEVERE: The `StackTraceElement`s of the deepest nested Throwable were silently ignored.
- Updated Logging XML Schema.
- Removal of obsolete `*.ljlogging` and `*.ljaccess` files.
- MessageFormatter: Special handling of array in case of a single placeholder.
- MessageFormatter: Special handling of `java.util.Date`. It's now converted to ISO 8601 representation.
- Known problems and FAQ in help did not reflect the latest version.
- In case of `#groovy#<scriptname>` the script received that string as search string.
  This has been fixed, now "" is used as search string.
- Detailsview displayed an error message if the file was just empty.
- In case of a new view, select first event if scroll to bottom is not enabled.
- Updated LogbackLoggingAdapter to use the NDC of events.
- Better handling of malformed saved table layouts. Invalid column names are ignored, missing names added.
- Updated XML Schema to include NDC.
- Implemented XML I/O of NDC.
- DetailsView: Looks a lot nicer, now.
- DetailsView: Better support for multi-line messages of Throwables.
- Better help including more links, symbols for keys.
- Renamed "Show/Hide" to "Columns" as suggested by Joe.
- Changed some licenses from GPLv3 to LGPLv3. Some poms didn't override the license appropriately.
  Added some missing license infos.
- Groovy 1.6.2


---

## [0.9.34] - 2009-01-04

### Added
- Added unit-tests for all datatypes.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Triple-fixed problem during auto-update of detailsView in case of malformed `history.txt`.


---

## [0.9.33] - 2009-01-03

### Added
- Added help about groovy filters.
- Copy marker (Suggested by Ekke)
- Copy callstack (Suggested by Ekke)
- new pre style as suggested by Alexander Kosenkov. Thanks a lot!
- Support for Map and Collection containing Arrays in MessageFormatter.
- Ability to save and reset layout of the tables.
  There are 4 table layouts: logging, loggingGlobal, access, accessGlobal.
  The global layouts are used in the global views "global (Logging)" and "global (Access)" while the non-global ones are used in ordinary views, as well as in "Lilith (Logging)".
  Functionality is available in View -> Layout and popup on table header.
- Ability to select look and feel in Preferences. Yes, the preferences dialog needs a lot of work :p
- JGoodies Looks added as a look and feel alternative.
- New class SaveCondition to serialize a condition together with a name, color infos and active setting. This is a preparation for a proper (auto) filters.
- Added message.contains, logger.startsWith and logger.equals conditions as well as the ability to select them in the find panel.
- `GroovyCondition` does now support a searchString. See help.
- Show condition name in filtered tab if condition is a saved condition.
- Documentation of Sources and Source-Lists.
- Documentation of Conditions.
- Ability to use saved conditions using #condition#&lt;conditionName&gt; in the text field of the search panel.
- Adding all available groovy conditions to combo box.
- Added "!" (Not) toggle button to search panel that negates the current condition.
- Implemented Conditions tab in Preferences.
- Use all active conditions to find out colors of a table row. Use current colors if none is matching.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Support for recursive Maps and Collections in `MessageFormatter`. This would previously throw a `StackOverflowError`.
- Better string representation for recursive `Map`, `Collection` and `Object[]`.
  It will now print `[...fully.qualified.ClassName@identityHash...]`.
- Support for Exception during toString().
  It will now result in `[!!!fully.qualified.ClassName=>fully.qualified.Throwable:ThrowableMessage!!!]`.
- Don't reset prefs after accepting license. Also added -L option to flush licensed for easier debugging.
- Improved general section of preferences dialog but I'm still not very happy about it.
- Exceptions in `SerializableDeserializer` are now silently ignored.
- Refactored conditions.
- Updated `detailsView.groovy` to prevent line-break in table headers so the width of the headers stays the same.
- Disabled "Clear" in case of a filtered buffer. Previous functionality was pretty useless.
- Cropping the message popup to a sane sizes...
- Supporting conditions with empty argument. This is necessary because it might make sense for groovy conditions.
- Added `EventIdentifier` that will be needed for caching of condition results.
  Changed `EventWrapper` to use `EventIdentifier` instead of Source ID + localId. Since this changes serialization anyway I took the opportunity to also add `omittedElements` to `ThrowableInfo`. Updated xml IO accordingly.
- Updated and uploaded new logging schema.
- Jumping to bottom in table+scrollToBottom even if table did not change.
- "Reset" in preferences does now work as expected, i.e. it resets the dialog to the previous preferences.
- Updated groovy dependency to 1.6-RC-1.
- Support for Logback 0.9.14.


---

## [0.9.32] - 2008-10-21

### Added
- Added documentation about `java.lang.OutOfMemoryError: PermGen space` problem and implemented `LogbackShutdownServletContextListener` for proper shut down of logback.
- Implemented `StackTraceElement` XML reader and writer to prepare for java-independent stack traces, e.g. C#. This will later be used for IDE integration like the IDEA plugin.
- "Show full Callstack." option.
- "Clean logs on exit." option.
- Prepared for transfer size statistics. You can't see anything, yet. Added todos to relevant places.
- Using Nimbus PLAF if available (and not Mac).
- Checksum-check of existing groovy and css files. Auto-update if not changed manually and newer version is available, e.g. `detailsView.groovy` in this new version of Lilith.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- sulky: Made sure that `TimeoutOutputStream-Watchdog-Thread` is stopped under all circumstances. Added testcases for every scenario.
- sulky: Fixed a stupid problem that could result in a deadlock in Lilith internal log view.
- Implemented changed message formatting logic introduced in SLF4J 1.5.3.
- Prepared for logback 0.9.10. This required changes to `LoggingEvent` and contained `StackTraceElement`s. Serialized `LoggingEvents` are not compatible to previous version.
- New xml schema 1.1 to support logback 0.9.10 features, i.e. CodeLocation, Version and Exact.
- Removed mac-specific UserNotification[..]EventConsumer because it crashes the app if J2SE 6 is used. It didn't work, anyway :p
- `detailsView.groovy` does now support CodeLocation, Version and Exact as well as null eventwrappers that can happen if deserialization fails.
- "Clean all inactive logs" on another thread.
- Made sure that every TimeoutOutputStream is *always* properly closed in the multiplex-appenders. This should finally fix `java.lang.OutOfMemoryError: PermGen space` problems in webapps, at least those caused by Lilith. :p
  For the record: Do *NOT* use IOUtils.closeQuietly in multithreaded webapp code!!!
- Accept license once for every version.
- Updated groovy dependency to 1.5.7.
- Updated SLF4J dependency to 1.5.5.


---

## [0.9.31] - 2008-08-11

### Added
- Implemented internal Lilith logging.
- Printing command line arguments if started verbose (-v).
- Added lilith.bat and lilith shell script.

### Deprecated
- Nothing.

### Removed
- Removed some debug logs from SerializingFileBuffer so basic initialization is guaranteed to *not* emit any logging events.

### Fixed
- XML appenders are now sending the message pattern instead of the formatted message.
- Removed shutdown hook from JmDNS. This fixes the shutdown deadlock but Lilith is still crashing badly on Mac OS X because of JmDNS problems.
- Disabled bonjour by default because it's just too unstable :(
  Activate it using -b command line argument.
- Updated assembly file to a more sane behavior, i.e. bin with sh and bat, lib with jar, LICENSE, README


---

## [0.9.30] - 2008-08-01

### Added
- Added support for Exceptions in case of parameter array, i.e. `log.debug("{} {}", new Object[]{"One", "Two", new Throwable())` will both evaluate the parameters in the message and the additional `Throwable` as the `Throwable` of the `LoggingEvent`.
  See [Bug 70 - "logging a stack trace along with a parameterized string" solution proposal](http://bugzilla.slf4j.org/show_bug.cgi?id=70).
  This feature will only work if one of the Lilith appenders is used, not with the original `ch.qos.logback.classic.net.SocketAppender`. (Reported as a Lilith bug by Alfred.)

### Deprecated
- Nothing.

### Removed
- Removed Napkin option. Not really needed anymore and I'm too lazy to add the 3rd party dependency. ;)

### Fixed
- Implemented own message formatting in MessageFormatter that does not rely on slf4j anymore.
- LoggingEvent.getMessage() does now return a lazily initialized formatted log message. There's no `setMessage` method anymore. Instead, there are `set/getMessagePattern` methods.
  All code in Lilith has been changed to not perform the message formatting manually but using `getMessage` instead.
  People that have not changed `[user.home]/.lilith/detailsView/detailsView.groovy` themselves should simply delete that file and restart Lilith. While this is not strictly necessary it will result in increased performance.
- Updated Macify dependency to 1.1.
- Cleanup of poms and dependencies for submission to central maven repository.
- Incorporated latest message formatting changes from slf4j rev 1086 + fixes. Added tests. Arrays of any type are now converted to a proper string representation using `Arrays.toString` and `Arrays.deepToString`.


---

## [0.9.29] - 2008-07-02

### Added
- Added SCM and distribution info to pom files. Source is now available in SVN repository at sourceforge.net.
- Added blurb about temporary repositories for sulky and lilith artifacts. http://sulky.sourceforge.net/repository/ and http://lilith.sourceforge.net/repository/ can be used until the artifacts are deployed to the central maven repository. Unfortunately, some dependencies aren't available in the central repository so I'll have some more work to do...
- Added remaining license infos to sources.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Implemented string representation of ThrowableInfo for "Copy Throwable".
- Updated license to simply show GPL3 without my additional comments :)
- Remove source name does actually work. (Reported by Alfred.)


---

## 0.9.28 - 2008-06-30

### Added
- Added option to show source identifier even in case of named source. Default is on because it's really useful, imho.
- Add Preferences Toolbar-Button
- Check for Update...
- Update available dialog (minimal).
- Open URL implemented for Windoze and Mac OS X.
- Close/Minimize all (other) windows. (Requested by Lothar.)

### Deprecated
- Nothing.

### Removed
- Removed "Clean and remove inactive views".

### Fixed
- Added special handling of OutOfMemoryError handling to AbstractMessageBasedEventProducer. It now tries to skip the amount of bytes while logging a warning about the problem. The stream isn't closed anymore in that case. This might help if huge events are logged but Lilith does not have enough memory to receive them.
- Reduced internal event buffer from 10.000 events to 1.000 events. This was done to make Lilith less memory hungry. It is still possible to run into OutOfMemoryErrors if unusually large events are received very fast.
- Refactoring: only use a single popup menu for event-related actions. Lower memory consumption.
- Refactoring: Edit menu-item containing the Copy actions.
- Refactoring: Only one message formatter instance for all views. Lower memory consumption.
- Yet another XML fix. See `SimpleXml.replaceNonValidXMLCharacters` for details.
- Refactoring: Major restructuring of appenders and event senders. There was a concurrency problem in certain VMs.
- send event on different thread.
- JmDNS for multiple interfaces
- Don't play sounds etc. if source is not in whitelist/blacklisted. Black/whitelisting is now handled on global queue level. This is more efficient anyway. (Reported by Alfred.)
- Changed all artifactIds - again! Closed beta is something beautiful :)


---

## 0.9.27 - 2008-04-30

### Added
- Implemented saving of conditions. Currently *command*-I.
- Added access event datatype and changed code to use it instead of logback one.
- Details view is now using a groovy script for generation of details xhtml.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Mac-like colors in table rows.
- Disabled auto-resize of tables.
- Debug-Dialog logging is done from different threads.
- Regression: Reimplemented event senders.
- Implement *some* copy-paste of events... "Copy event" does not work as intended because HTML clip support in Java is broken!
- global lilith version, global sulky version.


---

## 0.9.26.1 - 2008-04-08

### Added
- Nothing.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Some SimpleXml.escape calls were still missing...


---

## 0.9.26 - 2008-04-08

### Added
- Extended help significantly.
- Writing malformed messages to `${appDir}/errors/[timestamp]`. Shouldn't happen anymore, though.
- IDEA integration using Lilith IDEA Plugin. It's now possible to jump to the source of a logging event or `Throwable` stacktrace element. Shift-Doubleclick an event as a shortcut. This has been implemented quickly (i.e. just inside of the event dispatch thread) but will be enhanced later. It's working, though. :)

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Event Message and help is now proper XHTML.
- Using FlyingSaucer for message view and help. This has the downside that copy-paste doesn't work right now.
- Fixed sillyness in ZeroDelimitedXml[..]. The zero-byte is NOT added to the string anymore. Did I mention that I hate 'while' and 'do..while' loops and that 'for(;;)' is the only way to go instead?
- Added replacement of zero-byte by space in SimpleXml.escape. This is a simple workaround for the previously mentioned ZeroDelimetedXml[..] bug (which is also fixed!) but makes sense anyway.
- The stylesheet of the message view is now located in `${appDir}/messageView/messageView.css`. `${appDir}/messageView` is the document root of the message view, so you could also put other files there.


---

## 0.9.25 - 2008-04-03

### Added
- Redirecting `System.err` to `${appDir}/errors.log`
- Added support for autostart. Files contained in `${appDir}/autostart` are automatically executed on startup.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Disabled napkin by default. *enable* using option -n.
- Move (internal) frame to front and unminimize if selected from view menu.
- File -> "Clean all inactive logs" closes both logging and access views.
- Removed ex.printStackTrace from AbstractMessageBasedEventProducer. I missed that one ;)


---

## 0.9.24 - 2008-04-01

### Added
- Implemented black- and whitelisting of sources. See Preferences -> Source Filtering.
- Preferences: Source Filtering
- Preferences: Source Lists
- Created sulky and lilith project on sourceforge.
- Disable bonjour with -b commandline option.
- Added java.util.logging bridge. This was necessary to debug JmDNS. *sigh*
- Added license infos to sources.
- Added heartbeat to multiplexers and receivers
- Added eventsource and logging data types and xml.
- Added XML logging appender and producers. Also implemented special version for use with ActionScript `XMLSocket`.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Renamed from LOGBack Lumberjack to Lilith.
- Status-Bar of MainFrame shows Source filtering status.
- Using new logging data types for transfer.
- Major refactoring again...
- Patched JmDNS 2.0 to fix deadlock/livelock-problem. Patch is included in miscJars directory.
- Using JmDNS 2.0 instead of 1.0. New version was released 3 days ago and I had serious hope that it would fix the deadlock... but it just happens less often.
- Using groovy 1.5.3 instead of 1.5.0.
- slf4j-1.5.0 & logback-0.9.9


---

## 0.9.23 - 2007-12-20

### Added
- Napkin LAF until 1.0. Deactivate with -n.
- Preferences-SourceNames: Sort table by source/name. Only available using Java 1.6.
- Preferences-SourceNames: Shortcut to edit source name for source of current view (*command*-B).
- Preferences-general: Added option to focus window in case of new views, disabled (!!) by default.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Fixed deadlock when closing main window... It still sometimes happens but is a JmDns problem.
- Using groovy 1.5 instead of 1.0.


---

## 0.9.22 - 2007-11-11

### Added
- Preferences-SourceNames: "Add/Edit Source-Name"-Dialog.
- Basic IM "Send event" via Bonjour

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Nothing.


---

## 0.9.21 - 2007-11-04

### Added
- Shortcuts for Attach and Disconnect

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Mac: Fixed glasspane
- Mac: Fixed statistics.
- Mac: Fixed internal frame icon
- UI-Change: Removed menu and toolbar from internal frames, update menu/toolbar in mainframe.
- UI-Change: Toolbar in frame instead of event view
- Glasspane in case of internal frames is set to visible in case of inactive frame. Implemented special handling of this case.


---

## 0.9.20 - 2007-10-28

### Added
- Added `ClassicMultiplexSocketAppender`-Hack to circumvent [Bug 100 - A Marker does not have children, but secondary or sub-markers.](http://bugzilla.slf4j.org/show_bug.cgi?id=100).
- Added alarm sound to `AccessEvent`s with server error status-code.
- jump to event in unfiltered tab in case of double-click on filtered event.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- UI-Change: Removed Progress and Cancel of search from search panel. Using glasspane overlay instead.


---

## 0.9.19.1 - 2007-10-25

### Added
- Nothing.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Removed JSE6 dependency from sulky-swing.


---

## 0.9.19 - 2007-10-24

### Added
- Support for Access-Logs.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Major refactoring. New module lilith-engine.


---

## 0.9.18 - 2007-10-18

### Added
- Added simply groovy-conditions. Will be enhanced later.
- Multiplexing Appender support.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Fixed xml-escaping of exceptions in both message and tooltip.


---

## 0.9.17 - 2007-10-09

### Added
- Nothing.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Fixed critical bug in RrdEventConsumer.


---

## 0.9.16 - 2007-10-05

### Added
- Nothing.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- new memory stats.
- smaller status bars.
- reordered message view, added thread-name.
- improved statistics.
- about-panel and dialog.


---

## 0.9.15 - 2007-09-27

### Added
- Nothing.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Fixed SoftReferenceCachingBuffer.
- Removed unnecessary initialization in OpenPreviousDialog c'tor


---

## 0.9.14 - 2007-09-24

### Added
- Nothing.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- NPE in MainFrame.collectInactive if there are neither logs nor log-directory (thanks, Johannes ;))
- Fixed JSE6 dependency in StatisticsDialog...
- Updated logback and slf4j.


---

## 0.9.13 - 2007-09-11

### Added
- Nothing.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Check thread creation and destruction
- Some views seemed to be global...
- Fixed about 5 different memory leaks and thread problems.
- Fixed local id of connection closed "event".


---

## 0.9.12 - 2007-08-31

### Added
- Log informations in "Open previous" dialog.
- Added source selection combo to statistics dialog.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Number format in number of events of LoggingViewPanel.
- Automatically remove inactive views from menu upon close.
- Further statistics improvements. Fixed a memory leak, too.


---

## 0.9.11 - 2007-08-24

### Added
- Added "Open inactive log..." dialog and functionality.
- Replace filter (SHIFT-ENTER)
- Focus-visualization for table and message-pane.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Most likely fixed Vlads strange "no more connections" problem...
- Efficient statistics.
- Removed -G option and always start GUI instead.


---

## 0.9.10 - 2007-08-17

### Added
- About dialog.
- Statistics by source (using rrd4j).
- Active splitter-files have an .active file while active - auto-closed on delete. This is needed for later feature "Open inactive".
- Added previous image save path to ApplicationPreferences.
- Preferences option if new connections should open their view automatically.
- Preferences option if closed connections should close their view automatically.
- Added previous sound load path to ApplicationPreferences.
- Preferences-Option to change Application-Base-Path (ABP). Remember startup-ABP and use that throughout the runtime of the application.
- Create a file ".previous.app.path" containing the absolute pervious application base path (PABP). If such a file exists during startup, all files are moved from PABP to ABP.
- Add Application-Base-Path (ABP) to preferences dialog in General.
- On change of the application path, display a dialog to inform the user that the change will take place upon next restart, giving the option to Exit or Continue.
- Implemented splash to show progress of the app-path-move-operation.
- Added "Open inactive log..." menu item. No functionality yet. [*command*-SHIFT-O]
- Added "Clean all inactive logs" menu item. [*command*-SHIFT-X]
- Added "Disconnect" to LoggingViewPanel because of LOGBack-lingering-connection-problem.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Start threads after UI is initialized.
- Moved Splitter- and Global-Logfiles to subdirectories in ABP.
- Fixed checkboxes in views-menu.
- Fix sorting of views in views menu in case of source names.
- Focus traversal in LoggingViewPanel.
- Shortcuts if message-view is focused.


---

## 0.9.9 - 2007-08-08

### Added
- HelpIcon in Help-Frames.
- Added shortcuts for "Remove", "Clean and Remove" and global view.
- Focus selection actions for message and events.
- Source Names (Aliases) for primary sources, e.g. "Localhost" for "127.0.0.1", "Test-Server", "Live-Server"...
- SoundLocations and SourceNames are written to application path (<user.home>/.lumberjack).

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Fixed view-menu display-garbage in case of new/closed connection while opened.
- Message-Text in case of no selected event.
- Split project into sub-projects.
- Close of unfiltered view just after creation.
- Preferences dialog can be used to set sounds and source names.
- Nicer Prefs.


---

## 0.9.8 - 2007-08-01

### Added
- ESCAPE closes find panel.
- Enter in Find-Textfield creates new filtered view.
- FindAction shows find view and focuses textfield.
- Filter of view is changed on edit.
- Tango icons for LoggingViewState/Global icons of frames.
- COMMAND-J selects previous, COMMAND-K the next tab.
- COMMAND-G finds previous, COMMAND-SHIFT-G finds next. Shortcuts of find next and find previous have been switched since find previous is far more common than find next.
- COMMAND-Q exits.
- Keyboard help.

### Deprecated
- Nothing.

### Removed
- Nothing.

### Fixed
- Centered frame after detach.
- FilteredBuffer didn't stop filtering after dispose :(
- ProgressBar-update after filtering was canceled.
- Close filter actions are properly initialized with tabindex.
- Finished switch to Tango icons.
- Use default scrollToBottom for filtered views.
- Prevented icon-change in case of filtered view.

[unreleased]: https://github.com/huxi/lilith/compare/v8.2.0...HEAD
[8.2.0]: https://github.com/huxi/lilith/compare/v8.1.1...v8.2.0
[8.1.1]: https://github.com/huxi/lilith/compare/v8.1.0...v8.1.1
[8.1.0]: https://github.com/huxi/lilith/compare/v8.0.0...v8.1.0
[8.0.0]: https://github.com/huxi/lilith/compare/v0.9.44...v8.0.0
[0.9.44]: https://github.com/huxi/lilith/compare/v0.9.43...v0.9.44
[0.9.43]: https://github.com/huxi/lilith/compare/v0.9.42.1...v0.9.43
[0.9.42.1]: https://github.com/huxi/lilith/compare/v0.9.42...v0.9.42.1
[0.9.42]: https://github.com/huxi/lilith/compare/v0.9.41...v0.9.42
[0.9.41]: https://github.com/huxi/lilith/compare/v0.9.40...v0.9.41
[0.9.40]: https://github.com/huxi/lilith/compare/v0.9.39...v0.9.40
[0.9.39]: https://github.com/huxi/lilith/compare/v0.9.38...v0.9.39
[0.9.38]: https://github.com/huxi/lilith/compare/v0.9.37...v0.9.38
[0.9.37]: https://github.com/huxi/lilith/compare/v0.9.36...v0.9.37
[0.9.36]: https://github.com/huxi/lilith/compare/v0.9.35...v0.9.36
[0.9.35]: https://github.com/huxi/lilith/compare/v0.9.34...v0.9.35
[0.9.34]: https://github.com/huxi/lilith/compare/v0.9.33...v0.9.34
[0.9.33]: https://github.com/huxi/lilith/compare/v0.9.32...v0.9.33
[0.9.32]: https://github.com/huxi/lilith/compare/v0.9.31...v0.9.32
[0.9.31]: https://github.com/huxi/lilith/compare/v0.9.30...v0.9.31
[0.9.30]: https://github.com/huxi/lilith/compare/v0.9.29...v0.9.30
[0.9.29]: https://github.com/huxi/lilith/compare/init...v0.9.29
