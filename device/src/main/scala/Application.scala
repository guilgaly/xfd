import device.EndpointID

object Application extends App {
  val jenkinsKaaEndpoint = JenkinsKaaEndpoint(EndpointID("abc123"))
  jenkinsKaaEndpoint.kaaClient.start()
}
