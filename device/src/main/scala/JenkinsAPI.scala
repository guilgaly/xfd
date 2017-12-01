import scala.util.Try

trait JenkinsAPI {
  def success(): Try[Unit]
  def fail(): Try[Unit]
  def building(progress: Int): Try[Unit]
}
