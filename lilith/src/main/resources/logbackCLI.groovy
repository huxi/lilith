import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.jul.LevelChangePropagator
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.WARN

// statusListener(OnConsoleStatusListener)

context.name = 'Lilith'

LevelChangePropagator levelChangePropagator = new LevelChangePropagator()
levelChangePropagator.setContext(context)
levelChangePropagator.start()
context.addListener(levelChangePropagator)

appender('CONSOLE', ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = '%-5level - %msg%n'
  }
}
root(WARN, ['CONSOLE'])
