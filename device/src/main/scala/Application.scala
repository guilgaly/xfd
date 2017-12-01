

object Application extends App {

  val jenkinsKaaEndpoint = new JenkinsKaaEndpoint()
  jenkinsKaaEndpoint.client.start()

}
