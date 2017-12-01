import device.{AbstractKaaEndpoint, EndpointID}
import org.kaaproject.kaa.client.notification.{NotificationListener}
import xfd.jenkins.{Build, Project}


class JenkinsKaaEndpoint extends AbstractKaaEndpoint[Project] {

  override def id = EndpointID("abc123")
  var client = setKaaClient()

  override def endpointProfile = Project.newBuilder()
      .setName("myTeam")
      .build()

  client.addNotificationListener(1l, new NotificationListener() {
    override def onNotification(topicId: Long, buildStatus: Build): Unit = {
      val msg = (buildStatus.getStatus, buildStatus.getProgress) match {
        case ("building", progress) => s"Building ($progress)"
        case (status, _) => s"Build status: ${status}"
    }
      println(msg)
    }
  })
}