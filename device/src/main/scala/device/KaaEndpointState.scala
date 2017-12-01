package device

case class KaaEndpointState[
PROFILE: Profile[_],
CONFIGURATION: Configuration[_]
] private(
  profile: Option[PROFILE],
  configuration: Option[CONFIGURATION],
  listeners: Seq[EndpointID]
)

object KaaEndpointState {
  val blank = KaaEndpointState(
    profile = None,
    configuration = None,
    listeners = Nil
  )
  
  def updated(state: KaaEndpointState: Profile, C: Configuration], diff: KaaEndpointState) =
    KaaEndpointState(
      profile = diff.profile orElse state.profile,
      configuration = diff.configuration orElse state.configuration,
      listeners = diff.listeners
    )
}
