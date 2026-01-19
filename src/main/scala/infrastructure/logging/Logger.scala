package irka.grilleEncoder
package infrastructure.logging

import zio._
import zio.logging._
import zio.logging.LogFormat._
import zio.logging.backend.SLF4J

object Logger {
  // A customized log format with timestamp, log level, and logger name
  private val logFormat: LogFormat = LogFormat.colored

  // Logger layer with SLF4J backend
  val live: ULayer[Unit] =
    Runtime.removeDefaultLoggers >>>
      SLF4J.slf4j(logFormat)
}
