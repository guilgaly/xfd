package xfd.integration.core.model

import scala.collection.immutable

import enumeratum.{Enum, EnumEntry}

case class CiStatus(
    masterBuild: BuildStatus
)

sealed trait BuildStatus extends EnumEntry
object BuildStatus extends Enum[BuildStatus] {
  case object Unknown extends BuildStatus
  case object Success extends BuildStatus
  case object Failed extends BuildStatus

  override def values: immutable.IndexedSeq[BuildStatus] = findValues
}
