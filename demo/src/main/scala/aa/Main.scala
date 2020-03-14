package aa

import java.io.File

import bintry._
import dispatch.Defaults._
import dispatch._
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.writePretty

import scala.util.{Failure, Success}

object Main {
  val bty: Client = Client("mausamy", "4934b5bfa581174c94e2054818850fd770728796")
  val repo: bty.Repo = bty.repo("mausamy", "publish-dependencies")
  implicit val jsonFormats: DefaultFormats.type = DefaultFormats

  implicit class RichCompletion(x: Client.Completion[_]) {
    def run(): Unit = x(as.json4s.Json).onComplete {
      case Success(a)         => println(writePretty(a))
      case Failure(exception) => exception.printStackTrace()
    }
  }

  def main(args: Array[String]): Unit = {
    repo
      .createPackage("std")
      .licenses("MIT")
      .vcs("abc")
      .desc("to test publishing")
      .run()

    repo
      .get("std")
      .createVersion("3.8-7f1790")
      .run()

    repo
      .get("std")
      .version("3.8-7f1790")
      .upload("", new File(""))
    repo
      .get("std")
      .mvnUpload("com/mausamy", new File("std_sjs0.6_2.13.zip"))
      .run()
  }
}
