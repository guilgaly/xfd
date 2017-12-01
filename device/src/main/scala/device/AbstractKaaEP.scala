package device

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
import org.kaaproject.kaa.client.profile.ProfileContainer
import org.kaaproject.kaa.common.endpoint.gen.UserAttachResponse
import org.kaaproject.kaa.client.{KaaClient, KaaClientStateListener}
import org.kaaproject.kaa.client.profile.ProfileContainer

object KaaEndpoint {
  //  private val LOG = LoggerFactory.getLogger(classOf[AbstractKaaEP])
  val ENDPOINTS_DIR = "xdf/endpoints"
}

case class Profile[P <: ProfileContainer](container: P)
case class Configuration[C <: SpecificRecordBase](state: C)

trait KaaEndpoint[
PROFILE <: ProfileContainer,
CONFIGURATION <: SpecificRecordBase
] extends Endpoint {
  
  type AccessToken = String
  type ExternalId = String
  
  var state: KaaEndpointState[PROFILE, CONFIGURATION]
  var client: KaaClient
  
  val emitsEvents: Seq[KaaEvent]
  def stateListener: KaaClientStateListener
  def configurationListener: ConfigurationListener
}

abstract class AbtractKaaEndpoint[
PROFILE       <: ProfileContainer,
CONFIGURATION <: SpecificRecordBase
] extends KaaEndpoint[PROFILE, CONFIGURATION] {
  
  val endpointDir: String = s"${KaaEndpoint.ENDPOINTS_DIR}/$id"
  
  override def configurationListener: ConfigurationListener = {
    println("Returning default (empty) configuration listenener.")
    new ConfigurationListener() {
      override def onConfigurationUpdate(configurationRecord: CONFIGURATION) {
        println(s"New configuration but no handler: \n$configurationRecord");
      }
    }
  }
  
  protected def attachUser(externalId: ExternalId, accessToken: AccessToken): Unit = {
    client.attachUser(externalId, accessToken, new UserAttachCallback() {
      override def onAttachResult(response: UserAttachResponse): Unit = {
        response.getResult match {
          case SUCCESS => {
            LOG.info("User successfully attached.")
            refreshEventListeners()
          }
          case FAILURE => println("Error while attaching user, error {}:\n{}", response.getErrorCode, response.getErrorReason)
          case PROFILE_RESYNC => println("User resync success.\n")
          case REDIRECT => LOG.info("User redirect.")
        }
      }
    })
  }
  
  private def configureKaaEP(): Unit = {
    try { // Base properties for KaaClient
      val properties = new Nothing
      properties.setWorkingDirectory(endpointDir)
      AbstractKaaEP.LOG.debug("EP working directory: {}", properties.getWorkingDirectory)
      // Build Kaa client instance
      val context = new Nothing(properties)
      kaaClient = Kaa.newClient(context, getStateListener, true)
      // Set the log strategy provided by the implementing class.
      // Default: Kaa default strategy, upload immediately
      kaaClient.setLogUploadStrategy(getLogUploadStrategy)
      // Set the configuration storage as provided by the implementing class.
      // Default: store in EP home directory as "configuration.datum"
      kaaClient.setConfigurationStorage(new Nothing(context, endpointDir.concat("/configuration.datum")))
    } catch {
      case ex@(_: Nothing | _: Nothing) =>
        throw new Nothing(String.format("Could not configure Kaa endpoint for EP [%s]", endpointId), ex)
    }
  }
  
  /**
    * Replace the current endpoint profile and update Kaa server.
    * MUST be called at least once before starting the KaaClient (usually at the end of the subclass's constructor).
    */
  protected def updateKaaEndpointProfile(): Unit = { // Is this the first time the profile is set?
    val isPreStart = profileContainer == null
    // Set profile container
    profileContainer = getKaaEndpointProfile
    // Update/set kaa client endpoint profile
    /** Calls {@code AbstractKaaEP#getKaaEndpointProfile} which returns the most-up-to-date profile */
    kaaClient.setProfileContainer(profileContainer)
    if (isPreStart) AbstractKaaEP.LOG.info("Kaa profile set (pre-start).")
    else {
      AbstractKaaEP.LOG.info("Updating Kaa profile")
      // Update profile
      try
        kaaClient.updateProfile
      catch {
        case e: Nothing =>
          // May occur if Kaa client state is not valid (I.E. not started)
          AbstractKaaEP.LOG.warn("Could not update EP profile: {}", e.getMessage)
      }
    }
  }
  
  /**
    * Returns the default LogUploadStrategy, can be overridden for an alternative one.
    *
    * TODO: log upload strategy and persistence.
    *
    * @return LogUploadStrategy instance
    */
  private def getLogUploadStrategy = new Nothing() {
    @Override def isUploadNeeded(status: Nothing): Nothing = return if (status.getRecordCount >= 1) UPLOAD
    else NOOP
  }
  
  private def getStateListener = stateListener
  
  protected def setStateListener(): Unit = {
    AbstractKaaEP.LOG.info("Setting 'default' Kaa state listener")
    stateListener = new AbstractKaaEP#DefaultAbstractKaaEPStateListener
  }
  
  /**
    * Finds all the event listeners for the specified events (in the form of fully qualified class names).
    * Events are defined on the Kaa admin panel and their classes are generated in the Kaa SDK, where their FQNs can be obtained.
    * See subclasses for examples.
    *
    * @param fqnList the list of event FQNs
    */
  protected def findEventListeners(fqnList: Nothing): Unit = {
    AbstractKaaEP.LOG.info("EP [{}] finding event listeners.", getKaaClient.getEndpointKeyHash)
    getKaaClient.findEventListeners(fqnList, new Nothing() {
      @Override def onEventListenersReceived(listeners: Nothing): Unit = { // Clear current list and replace with the most up-to-date list.
        eventListeners.clear
        eventListeners.addAll(listeners)
        AbstractKaaEP.LOG.info("Total EP listeners: {}", listeners.size)
      }
      
      @Override
      def onRequestFailed(): Unit
      =
      {
        AbstractKaaEP.LOG.error("Failed to retrieve list of event listeners. Current list size: {}", eventListeners.size)
      }
    })
  }
  
  /**
    * Currently for development, any user ID + password will result in a successful authentication.
    *
    * @param externalId
    * @param accessToken
    */
  protected def attachUser(externalId: Nothing, accessToken: Nothing): Unit = {
    kaaClient.attachUser(externalId, accessToken, new Nothing() {
      @Override def onAttachResult(response: Nothing): Unit = {
        response.getResult match {
          case SUCCESS =>
            AbstractKaaEP.LOG.info("User successfully attached.")
            setEventListeners()
            break //todo: break is not supported
          case FAILURE =>
            AbstractKaaEP.LOG.error("Error while attaching user, error {}:\n{}", response.getErrorCode, response.getErrorReason)
            break //todo: break is not supported
          case PROFILE_RESYNC =>
            AbstractKaaEP.LOG.info("User resync success.\n")
            break //todo: break is not supported
          case REDIRECT =>
            AbstractKaaEP.LOG.info("User redirect.")
            break //todo: break is not supported
        }
      }
    })
  }
  
  protected def getConfigurationListener: Nothing = {
    AbstractKaaEP.LOG.debug("Returning default (empty) configuration listenener.")
    new Nothing() {
      @Override def onConfigurationUpdate(stationConfigurationRecord: Nothing): Unit = {
        AbstractKaaEP.LOG.warn("Configuration listener triggered without implementation!!")
      }
    }
  }
  
  def getKaaClient: Nothing = kaaClient
  
  @Override def getEndpointId: Nothing = endpointId
  
  protected class DefaultAbstractKaaEPStateListener extends Nothing {
    @Override def onStarted(): Unit = {
      AbstractKaaEP.LOG.info("Kaa EP [{}] started.", endpointId)
    }
    
    @Override def onStartFailure(e: Nothing): Unit = {
      AbstractKaaEP.LOG.error("Kaa EP [{}] failed to start!", endpointId)
    }
    
    @Override def onPaused(): Unit = {
      AbstractKaaEP.LOG.info("Kaa EP [{}] paused.", endpointId)
    }
    
    @Override def onPauseFailure(e: Nothing): Unit = {
      AbstractKaaEP.LOG.warn("Kaa EP [{}] failed to pause!", endpointId)
    }
    
    @Override def onResume(): Unit = {
      AbstractKaaEP.LOG.info("Kaa EP [{}] resumed.", endpointId)
    }
    
    @Override def onResumeFailure(e: Nothing): Unit = {
      AbstractKaaEP.LOG.warn("Kaa EP [{}] failed to resume!", endpointId)
    }
    
    @Override def onStopped(): Unit = {
      AbstractKaaEP.LOG.info("Kaa EP [{}] stopped.", endpointId)
    }
    
    @Override def onStopFailure(e: Nothing): Unit = {
      AbstractKaaEP.LOG.warn("Kaa EP [{}] failed to stop!", endpointId)
    }
  }
  
}
