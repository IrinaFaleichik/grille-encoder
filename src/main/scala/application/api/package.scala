package irka.grilleEncoder
package application

import domain.errors.InvalidJson
import zio.http.{Response, Status}

package object api:
  def anyError: PartialFunction[Throwable, Response] =
    e => Response.internalServerError(s"DB error: $e.getMessage")

  def jsonParsingError: PartialFunction[Throwable, Response] =
    case e: InvalidJson => Response.text(e.getMessage).status(Status.BadRequest)

  extension (pf: PartialFunction[Throwable, Response])
    def +(other: PartialFunction[Throwable, Response]): PartialFunction[Throwable, Response] =
      pf.orElse(other)
      
      

