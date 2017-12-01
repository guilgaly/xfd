import scala.util.{Success, Try}
import sys.process._

class DeviceClient extends JenkinsAPI {

  type CmdResult = Try[Unit]

  private val executor: String = "python"
  private val executable: String = "led"

  private def exec(cmd: String): CmdResult = Try {
    val eval = s"$executor $executable $cmd"
    println(eval)
    eval.!
    ()
  }

  def success(): CmdResult = exec("success")
  def fail(): CmdResult = exec("fail")
  def building(progress: Int): CmdResult = exec(s"build $progress")
}
