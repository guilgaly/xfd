import device.{AbstractKaaEndpoint, EndpointID}
import org.kaaproject.kaa.client.notification.NotificationListener
import xfd.jenkins.{Build, Project}


case class JenkinsKaaEndpoint(id: EndpointID) extends AbstractKaaEndpoint[Project] {

  override val stateListener = new JenkinsKaaEPStateListener
  override val kaaClient = setKaaClient()

  private val deviceClient = new DeviceClient

  override def endpointProfile = Project.newBuilder()
      .setName("myTeam")
      .build()

  val notificationListener = new NotificationListener {
    override def onNotification(topicId: Long, buildStatus: Build): Unit = {
      (buildStatus.getStatus, buildStatus.getProgress) match {
        case ("building", progress) => deviceClient.building(progress)
        case ("fail", _) => deviceClient.fail()
        case ("success", _) => deviceClient.success()
        case (status, _) => println(s"Wrong status: $status")
      }
      ()
    }
  }

  protected class JenkinsKaaEPStateListener extends DefaultAbstractKaaEPStateListener {
    override def onStarted(): Unit = {
      super.onStarted()
      println("Adding notification listener.")
      kaaClient.addNotificationListener(1l, notificationListener)
    }
  }
}