package device

final case class KaaEvent(name: String) extends AnyVal {
  require(name.nonEmpty)
}