package irka.grilleEncoder
package application.api.auth.password

import zio.{ZIO, ZLayer}

import scala.util.Try

trait Secret:
  val salt: String
  val iterations: Int

object Secret:

  private final class SecretLive(override val salt: String, override val iterations: Int) extends Secret

  def make(salt: String, iterations: Int): Secret =
    SecretLive(salt, iterations)

  // todo add a fatal error if secret is not created
  def live: ZLayer[Any, Throwable, Secret] = ZLayer.fromZIO:
    for
      _ <- ZIO.logInfo("Creating a secret")
      env <- ZIO.fromTry(Try(System.getenv))
      envSalt <- ZIO.fromTry(Try(env.get("PASSWORD_SALT")))
      envIterations <- ZIO.fromTry(Try(env.get("PASSWORD_ITERATIONS")))
      envIterationsInt <- ZIO.fromTry(Try(envIterations.toInt))
      secret = make(envSalt, envIterationsInt)
      _ <- ZIO.logInfo("Secret is created")
    yield secret

//  def salt: ZIO[Secret, Throwable, String] = ZIO.serviceWith[Secret](secret => secret.salt)
