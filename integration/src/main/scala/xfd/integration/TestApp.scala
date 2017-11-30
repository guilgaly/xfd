package xfd.integration

import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigFactory

object TestApp /*extends App*/ {
  val config = ConfigFactory.load()
//  println(config)
  println(config.getAnyRef("application.jenkins.interval"))
  println(config.getAnyRef("application.jenkins.interval").getClass)
  println(
    config.getDuration("application.jenkins.interval", TimeUnit.MILLISECONDS))
}
