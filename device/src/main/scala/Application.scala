import org.kaaproject.kaa.client.{DesktopKaaPlatformContext, Kaa, KaaClient, SimpleKaaClientStateListener}
import org.kaaproject.kaa.client.notification.NotificationTopicListListener
import org.kaaproject.kaa.common.endpoint.gen.Topic

object Application extends App {
  println("Coucou")

  val kaaClient: KaaClient = Kaa.newClient(
    new DesktopKaaPlatformContext(),
    new SimpleKaaClientStateListener(),
    true)

  // Add listener// Add listener
  kaaClient.addTopicListListener(new NotificationTopicListListener() {
    def onListUpdated(topics: java.util.List[Topic]): Unit = {
      topics.forEach { topic =>
        printf("Id: %s, name: %s, type: %s", topic.getId, topic.getName, topic.getSubscriptionType)
      }
    }
  })

  kaaClient.subt
}
