package xfd.integration.in.jenkins.model

import scala.collection.immutable

import enumeratum.{Enum, EnumEntry}

sealed trait BuildResult extends EnumEntry

private[jenkins] object BuildResult extends Enum[BuildResult] {

  case object FAILURE extends BuildResult

  case object UNSTABLE extends BuildResult

  case object REBUILDING extends BuildResult

  case object BUILDING extends BuildResult

  /** This means a job was already running and has been aborted. */
  case object ABORTED extends BuildResult

  case object SUCCESS extends BuildResult

  /** ? */
  case object UNKNOWN extends BuildResult

  /** This is returned if a job has never been built. */
  case object NOT_BUILT extends BuildResult

  /**
    * This will be the result of a job in cases where it has been cancelled
    * during the time in the queue.
    */
  case object CANCELLED extends BuildResult

  val values: immutable.IndexedSeq[BuildResult] = findValues
}
