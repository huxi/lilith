import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.jul.LevelChangePropagator
import ch.qos.logback.classic.net.SocketAppender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.util.Duration
import de.huxhorn.lilith.appender.InternalLilithAppender
import de.huxhorn.lilith.logback.appender.json.ClassicJsonMultiplexSocketAppender
import de.huxhorn.lilith.logback.appender.ClassicMultiplexSocketAppender
import de.huxhorn.lilith.logback.appender.xml.ClassicXmlMultiplexSocketAppender
import de.huxhorn.lilith.logback.appender.json.ZeroDelimitedClassicJsonMultiplexSocketAppender
import de.huxhorn.lilith.logback.appender.xml.ZeroDelimitedClassicXmlMultiplexSocketAppender

import static ch.qos.logback.classic.Level.ALL
import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.TRACE
import static ch.qos.logback.classic.Level.WARN

// statusListener(OnConsoleStatusListener)

context.name = 'Lilith'
context.putProperty('foo','bar')
context.setPackagingDataEnabled(true)

LevelChangePropagator levelChangePropagator = new LevelChangePropagator()
levelChangePropagator.setContext(context)
levelChangePropagator.start()
context.addListener(levelChangePropagator)

def delay = new Duration(200)

appender('CONSOLE', ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = '%-5level - %msg%n'
  }
}
appender('Lilith', InternalLilithAppender)
appender('MultiplexClassicCompressed', ClassicMultiplexSocketAppender) {
  compressing = true
  applicationIdentifier = 'MultiplexClassicCompressed'
  reconnectionDelay = delay.milliseconds
  includeCallerData = true
  remoteHost = 'localhost'
}
appender('MultiplexClassicCompressed-noUUID', ClassicMultiplexSocketAppender) {
  compressing = true
  applicationIdentifier = 'MultiplexClassicCompressed-noUUID'
  reconnectionDelay = delay.milliseconds
  includeCallerData = true
  creatingUUID = false
  remoteHost = 'localhost'
}
appender('MultiplexClassicUncompressed', ClassicMultiplexSocketAppender) {
  compressing = false
  applicationIdentifier = 'MultiplexClassicUncompressed'
  reconnectionDelay = delay.milliseconds
  includeCallerData = true
  remoteHost = 'localhost'
}
appender('MultiplexClassicXmlCompressed', ClassicXmlMultiplexSocketAppender) {
  compressing = true
  applicationIdentifier = 'MultiplexClassicXmlCompressed'
  reconnectionDelay = delay.milliseconds
  includeCallerData = true
  remoteHost = 'localhost'
}
appender('MultiplexClassicXmlUncompressed', ClassicXmlMultiplexSocketAppender) {
  compressing = false
  applicationIdentifier = 'MultiplexClassicXmlUncompressed'
  reconnectionDelay = delay.milliseconds
  includeCallerData = true
  remoteHost = 'localhost'
}
appender('ZeroDelimitedMultiplexClassicXml', ZeroDelimitedClassicXmlMultiplexSocketAppender) {
  applicationIdentifier = 'ZeroDelimitedMultiplexClassicXml'
  reconnectionDelay = delay.milliseconds
  includeCallerData = true
  remoteHost = 'localhost'
}
appender('MultiplexClassicJsonCompressed', ClassicJsonMultiplexSocketAppender) {
  compressing = true
  applicationIdentifier = 'MultiplexClassicJsonCompressed'
  reconnectionDelay = delay.milliseconds
  includeCallerData = true
  remoteHost = 'localhost'
}
appender('MultiplexClassicJsonUncompressed', ClassicJsonMultiplexSocketAppender) {
  compressing = false
  applicationIdentifier = 'MultiplexClassicJsonUncompressed'
  reconnectionDelay = delay.milliseconds
  includeCallerData = true
  remoteHost = 'localhost'
}
appender('ZeroDelimitedMultiplexClassicJson', ZeroDelimitedClassicJsonMultiplexSocketAppender) {
  applicationIdentifier = 'ZeroDelimitedMultiplexClassicJson'
  reconnectionDelay = delay.milliseconds
  includeCallerData = true
  remoteHost = 'localhost'
}
appender('LogbackClassic', SocketAppender) {
  remoteHost = 'localhost'
  port = 4560
  reconnectionDelay = delay
  includeCallerData = true
}

root(INFO, ['CONSOLE', 'Lilith'])

logger('de.huxhorn.lilith', INFO)
logger('de.huxhorn.lilith.Lilith', INFO)
logger('de.huxhorn.lilith.debug', TRACE, ['MultiplexClassicCompressed', 'MultiplexClassicUncompressed', 'MultiplexClassicXmlCompressed', 'MultiplexClassicXmlUncompressed', 'ZeroDelimitedMultiplexClassicXml', 'MultiplexClassicJsonCompressed', 'MultiplexClassicJsonUncompressed', 'ZeroDelimitedMultiplexClassicJson', 'LogbackClassic', 'MultiplexClassicCompressed-noUUID'], false)
logger('de.huxhorn.lilith.engine.FileBufferFactory', WARN)
logger('de.huxhorn.lilith.engine.impl.eventproducer.MessageBasedEventProducer', WARN)
logger('de.huxhorn.lilith.engine.impl.eventproducer.SerializableEventProducer', WARN)
logger('de.huxhorn.lilith.engine.impl.eventproducer.ZeroDelimitedEventProducer', WARN)
logger('de.huxhorn.lilith.engine.impl.sourcemanager.EventPoller', WARN)
logger('de.huxhorn.lilith.engine.impl.sourcemanager.SourceManagerImpl', WARN)
logger('de.huxhorn.lilith.engine.xml.eventproducer.LilithXmlStreamLoggingEventProducer', WARN)
logger('de.huxhorn.lilith.eventhandlers.FileDumpEventHandler', WARN)
logger('de.huxhorn.lilith.eventhandlers.FileSplitterEventHandler', WARN)
logger('de.huxhorn.lilith.eventhandlers.RrdLoggingEventHandler', INFO)
logger('de.huxhorn.lilith.services.details', WARN)
logger('de.huxhorn.lilith.services.sender', WARN)
logger('de.huxhorn.lilith.swing.AboutPanel', WARN)
logger('de.huxhorn.lilith.swing.ApplicationPreferences', WARN)
logger('de.huxhorn.lilith.swing.CheckForUpdateDialog', WARN)
logger('de.huxhorn.lilith.swing.ComboPaneViewContainer', WARN)
logger('de.huxhorn.lilith.swing.EventWrapperViewPanel', WARN)
logger('de.huxhorn.lilith.swing.HelpFrame', WARN)
logger('de.huxhorn.lilith.swing.MainFrame$ColorsCollectionRunnable', INFO)
logger('de.huxhorn.lilith.swing.MainFrame$MyApplicationListener', INFO)
logger('de.huxhorn.lilith.swing.MainFrame', INFO)
logger('de.huxhorn.lilith.swing.RendererConstants', WARN)
logger('de.huxhorn.lilith.swing.SplashScreen', WARN)
logger('de.huxhorn.lilith.swing.TipOfTheDayDialog', WARN)
logger('de.huxhorn.lilith.swing.ViewActions', INFO)
logger('de.huxhorn.lilith.swing.ViewContainerFrame', WARN)
logger('de.huxhorn.lilith.swing.ViewContainerInternalFrame', WARN)
logger('de.huxhorn.lilith.swing.ViewManager', WARN)
logger('de.huxhorn.lilith.swing.callables.IndexingCallable', WARN)
logger('de.huxhorn.lilith.swing.preferences', WARN)
logger('de.huxhorn.lilith.swing.table.EventWrapperViewTable', INFO)
logger('de.huxhorn.lilith.swing.taskmanager', INFO)
logger('de.huxhorn.lilith.swing.taskmanager.table', WARN)
logger('de.huxhorn.lilith.swing.xhtml', DEBUG)
logger('de.huxhorn.lilith.tools', WARN)
logger('de.huxhorn.lilith.tools.CreateMd5Command', INFO)
logger('de.huxhorn.lilith.tools.ImportExportCommand', INFO)
logger('de.huxhorn.lilith.tray', WARN)
logger('de.huxhorn.sulky', INFO)
logger('de.huxhorn.sulky.buffers.SoftReferenceCachingBuffer', WARN)
logger('de.huxhorn.sulky.buffers.filtering.FilteringBuffer', INFO)
logger('de.huxhorn.sulky.groovy', WARN)
logger('de.huxhorn.sulky.logging.LoggingPropertyChangeListener', DEBUG)
logger('de.huxhorn.sulky.sounds.jlayer.JLayerSounds', WARN)
logger('de.huxhorn.sulky.swing.KeyStrokes', INFO)
logger('de.huxhorn.sulky.swing.Windows', WARN)
logger('httpclient.wire', WARN)
logger('java', WARN)
logger('javax', WARN)
logger('org.apache.commons.httpclient', WARN)
logger('org.springframework', INFO)
logger('org.thymeleaf', WARN)
logger('org.thymeleaf.TemplateEngine.cache.TEMPLATE_CACHE', TRACE)
logger('org.xhtmlrenderer.cascade', WARN)
logger('org.xhtmlrenderer.config', WARN)
logger('org.xhtmlrenderer.css-parse', WARN)
logger('org.xhtmlrenderer.exception', ALL)
logger('org.xhtmlrenderer.general', WARN)
logger('org.xhtmlrenderer.init', WARN)
logger('org.xhtmlrenderer.junit', WARN)
logger('org.xhtmlrenderer.layout', WARN)
logger('org.xhtmlrenderer.load', ALL)
logger('org.xhtmlrenderer.load.xml-entities', ALL)
logger('org.xhtmlrenderer.match', WARN)
logger('org.xhtmlrenderer.render', WARN)
logger('sun', WARN)
