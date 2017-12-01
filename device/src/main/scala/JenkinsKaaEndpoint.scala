import device.{AbtractKaaEndpoint, EndpointID}
import org.kaaproject.kaa.client.notification.NotificationTopicListListener
import org.kaaproject.kaa.client.profile.ProfileContainer
import org.kaaproject.kaa.common.endpoint.gen.Topic
import xfd.profile.Project


class JenkinsKaaEndpoint extends AbtractKaaEndpoint[Project] {

  override def id = EndpointID("abc123")

  override def endpointProfile = Project.newBuilder()
      .setName("myTeam")
      .build()

  client.addTopicListListener(new NotificationTopicListListener() {
    def onListUpdated(topics: java.util.List[Topic]): Unit = {
      topics.forEach { topic =>
        printf("Id: %s, name: %s, type: %s", topic.getId, topic.getName, topic.getSubscriptionType)
      }
    }
  })
}