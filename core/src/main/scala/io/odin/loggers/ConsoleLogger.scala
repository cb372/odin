package io.odin.loggers

import java.io.PrintStream

import cats.effect.{Sync, Timer}
import cats.syntax.all._
import io.odin.formatter.Formatter
import io.odin.{Level, Logger, LoggerMessage}

case class ConsoleLogger[F[_]: Timer](
    formatter: Formatter,
    out: PrintStream,
    err: PrintStream,
    override val minLevel: Level
)(implicit F: Sync[F])
    extends DefaultLogger[F](minLevel) {
  private def println(out: PrintStream, msg: LoggerMessage, formatter: Formatter): F[Unit] =
    F.delay(out.println(formatter.format(msg)))

  def log(msg: LoggerMessage): F[Unit] =
    if (msg.level < Level.Warn) {
      println(out, msg, formatter)
    } else {
      println(err, msg, formatter)
    }
}

object ConsoleLogger {
  def apply[F[_]: Timer: Sync](formatter: Formatter, minLevel: Level): Logger[F] =
    ConsoleLogger(formatter, scala.Console.out, scala.Console.err, minLevel)
}
