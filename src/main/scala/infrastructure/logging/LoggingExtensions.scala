package irka.grilleEncoder
package infrastructure.logging

import zio.*
//import zio.json.EncoderOps

object LoggingExtensions {
  /** Extension methods for ZIO effects to add logging capabilities */
  extension [R, E, A](zio: ZIO[R, E, A]) {

    def logErrorWithoutTrace(errorMsg: E => String): ZIO[R, E, A] =
      zio.tapError(e => ZIO.logWarning(errorMsg(e)))

    //    def logErrorWithJson[T: zio.json.JsonEncoder](prefix: String, obj: T): ZIO[R, E, A] =
    //      zio.tapError(e => ZIO.logWarning(s"$prefix: ${obj.toJson} - Error: ${e.toString}"))
  }
}