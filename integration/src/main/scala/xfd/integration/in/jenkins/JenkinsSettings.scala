package xfd.integration.in.jenkins

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

import com.typesafe.config.Config

private[jenkins] case class JenkinsSettings(conf: Config) {

  private val rootPath = "application.jenkins"

  val interval: FiniteDuration = {
    val dur = conf.getDuration(s"$rootPath.interval", TimeUnit.MILLISECONDS)
    FiniteDuration(dur, TimeUnit.MILLISECONDS)
  }

  val rootUrl: String = conf.getString(s"$rootPath.rootUrl")

  val username: String = conf.getString(s"$rootPath.username")

  val password: String = conf.getString(s"$rootPath.password")

  val jobName: String = conf.getString(s"$rootPath.jobName")
}
