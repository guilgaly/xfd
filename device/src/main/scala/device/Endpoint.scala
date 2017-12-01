package device

import java.util.UUID

trait Endpoint {
  def id: EndpointID
}

final case class EndpointID(value: String) extends AnyVal

object EndpointID {
  def random(): EndpointID = EndpointID(UUID.randomUUID().toString)
}