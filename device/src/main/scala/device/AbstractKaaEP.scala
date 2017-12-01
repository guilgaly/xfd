package device

import java.io.IOException

import org.apache.avro.specific.SpecificRecordBase
import org.kaaproject.kaa.client._
import org.kaaproject.kaa.client.configuration.base.ConfigurationListener
import org.kaaproject.kaa.client.configuration.base.SimpleConfigurationStorage
import org.kaaproject.kaa.client.event.FindEventListenersCallback
import org.kaaproject.kaa.client.event.registration.UserAttachCallback
import org.kaaproject.kaa.client.exceptions.KaaException
import org.kaaproject.kaa.client.exceptions.KaaRuntimeException
import org.kaaproject.kaa.client.logging.DefaultLogUploadStrategy
import org.kaaproject.kaa.client.logging.LogStorageStatus
import org.kaaproject.kaa.client.logging.LogUploadStrategy
import org.kaaproject.kaa.client.logging.LogUploadStrategyDecision
import org.kaaproject.kaa.common.endpoint.gen.{SyncResponseResultType, UserAttachResponse}
import org.kaaproject.kaa.client.{KaaClient, KaaClientStateListener}
import org.kaaproject.kaa.client.profile.ProfileContainer
import xfd.profile.Project

//case class EndpointProfile[P <: SpecificRecordBase](record: P)
//case class EndpointConfiguration[C <: SpecificRecordBase](state: C)
//case class KaaEndpointState[PROFILE: EndpointProfile] private(
//  profile: Option[PROFILE],
////  configuration: Option[CONFIGURATION],
//  listeners: Seq[EndpointID]
//) {
//  def updateWith(diff: KaaEndpointState[PROFILE]) = this.copy(
//    profile = diff.profile orElse this.profile,
//    listeners = diff.listeners
//  )
//}

trait KaaEndpoint[
P <: SpecificRecordBase
] extends Endpoint {

//  type Profile = EndpointProfile[P]
//  type Configuration = EndpointConfiguration[SpecificRecordBase]
//  type State = KaaEndpointState[Profile]

  type AccessToken = String
  type ExternalId = String
  
//  var state: KaaEndpointState[Profile]
  var client: KaaClient

  def stateListener: KaaClientStateListener
  def logUploadStrategy: LogUploadStrategy
  def endpointProfile: Project

  //  def configurationListener: ConfigurationListener
  //  val emitsEvents: Seq[KaaEvent]
}

object KaaEndpoint {
  val ENDPOINTS_DIR = "xdf/endpoints"
}

abstract class AbtractKaaEndpoint[P <: SpecificRecordBase] extends KaaEndpoint[P] {
  
  val endpointDir: String = s"${KaaEndpoint.ENDPOINTS_DIR}/$id"
  val stateListener: KaaClientStateListener = new DefaultAbstractKaaEPStateListener()

  var client = setKaaClient()
//  var state = KaaEndpointState[Profile](None, Seq.empty)

  private def setKaaClient(): KaaClient = {
    val properties: KaaClientProperties = new KaaClientProperties()
    properties.setWorkingDirectory(endpointDir)
    println(s"EP working directory: ${properties.getWorkingDirectory}")

    val context: DesktopKaaPlatformContext = new DesktopKaaPlatformContext(properties)

    try {
      val newClient = Kaa.newClient(context, stateListener, true)
      newClient.setLogUploadStrategy(logUploadStrategy)
      newClient.setConfigurationStorage(new SimpleConfigurationStorage(context, endpointDir.concat("/configuration.datum")))
      newClient.setProfileContainer(getProfileContainer)
      newClient
    } catch {
      case e: Exception => {
        throw new Error(s"Could not configure Kaa endpoint for EP [$id]", e)
      }
    }
  }
  
  protected def attachUser(externalId: ExternalId, accessToken: AccessToken): Unit = {
    println("Attaching user...")
    client.attachUser(externalId, accessToken, new UserAttachCallback() {
      override def onAttachResult(response: UserAttachResponse): Unit = {
        response.getResult match {
          case SyncResponseResultType.SUCCESS => {
            println("User successfully attached.")
//            refreshEventListeners() // not used here/now
          }
          case SyncResponseResultType.FAILURE => println(s"Error while attaching user, error ${response.getErrorCode}:\n${response.getErrorReason}")
          case SyncResponseResultType.PROFILE_RESYNC => println("User resync success.\n")
          case SyncResponseResultType.REDIRECT => println("User redirect.")
        }
      }
    })
  }

  def getProfileContainer: ProfileContainer = {
    new ProfileContainer {
      override def getProfile = endpointProfile
    }
  }
  
  /**
    * Replace the current endpoint profile and update Kaa server.
    * MUST be called at least once before starting the KaaClient.
    */
  protected def updateKaaEndpointProfile(): Unit = {
    println("Updating Kaa profile")
    try
      client.updateProfile()
    catch {
      case e: KaaRuntimeException =>
        // May occur if Kaa client state is not valid (I.E. not started)
        println(s"Could not update EP profile: ${e.getMessage}")
    }
  }

  def logUploadStrategy: LogUploadStrategy = {
    new DefaultLogUploadStrategy() {
      override def isUploadNeeded(status: LogStorageStatus): LogUploadStrategyDecision = {
        if (status.getRecordCount >= 1) LogUploadStrategyDecision.UPLOAD else LogUploadStrategyDecision.NOOP
      }
    }
  }

  /*override def configurationListener: ConfigurationListener = {
    new ConfigurationListener() {
      @Override
      def onConfigurationUpdate(emptyData: EmptyData) {
        println(s"New configuration but no handler");
      }
    }
  }*/

  protected class DefaultAbstractKaaEPStateListener extends KaaClientStateListener {
    override def onStarted(): Unit = {
      println(s"Kaa EP [$id] started.")
      attachUser("bogus", "smogus")
      // trigger find event listeners if needed/used here
    }
    
    override def onStartFailure(e: KaaException): Unit = {
      println(s"Kaa EP [$id] failed to start!")
    }
    
    override def onPaused(): Unit = {
      println(s"Kaa EP [$id] paused.")
    }
    
    override def onPauseFailure(e: KaaException): Unit = {
      println(s"Kaa EP [$id] failed to pause!")
    }
    
    override def onResume(): Unit = {
      println(s"Kaa EP [$id] resumed.")
    }
    
    override def onResumeFailure(e: KaaException): Unit = {
      println(s"Kaa EP [$id] failed to resume!")
    }
    
    override def onStopped(): Unit = {
      println(s"Kaa EP [$id] stopped.")
    }
    
    override def onStopFailure(e: KaaException): Unit = {
      println(s"Kaa EP [$id] failed to stop!")
    }
  }
}
