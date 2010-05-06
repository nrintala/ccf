package ccf.transport.json

import com.twitter.json.Json

object JsonFormatter extends Formatter {
  def formatRequest(request: Request): String = Json.build(toMap(request)).toString
  def formatResponse(response: Response): String = error("Not implemented")
  private def toMap(request: Request) = request.content match {
    case Some(c) => Map("headers" -> request.headers, "content" -> c)
    case None    => Map("headers" -> request.headers)
  }
}
