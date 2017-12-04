package xfd.integration.out

import com.typesafe.config.Config

case class KaaSettings(conf: Config) {

  private val rootPath = "application.kaa"

  val notificationUrl: String = conf.getString(s"$rootPath.notificationUrl")

  val username: String = conf.getString(s"$rootPath.username")

  val password: String = conf.getString(s"$rootPath.password")
}
