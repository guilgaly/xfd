

object Application extends App {
  println("Coucou")

  val jenkinsKaaEndpoint = new JenkinsKaaEndpoint()
  jenkinsKaaEndpoint.client.start()
}
