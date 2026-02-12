package irka.grilleEncoder
package application.api.auth.password

import zio.{Task, ZIO}
import scala.util.Try

trait Secret:
  val salt: String
  val iterations: Int

object Secret:
  
  def make(envSalt: String, envIterations: Int): Secret =
    new Secret:
      override val salt: String = envSalt
      override val iterations: Int = envIterations

  def live: Task[Secret] =
    for
      _ <- ZIO.logInfo("Secret is generated")
      env <- ZIO.fromTry(Try(System.getenv))
      envSalt <- ZIO.fromTry(Try(env.get("PASSWORD_SALT")))
      envIterations <- ZIO.fromTry(Try(env.get("PASSWORD_ITERATIONS").toInt))
      secret = make(envSalt, envIterations)
    yield secret

  def salt: ZIO[Secret, Throwable, String] = ZIO.serviceWith[Secret](secret => secret.salt)
  