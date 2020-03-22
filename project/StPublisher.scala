import Extensions._
import org.json4s.JValue
import sbt._
import sbt.internal.util.ManagedLogger

class StPublisher(repo: bintry.Client#Repo)(implicit logger: ManagedLogger) {

  def publishAll(org: String, moduleIds: Set[ModuleID]): Set[String] = {
    val remotePackages: Seq[String] = getPackages
    remotePackages.mkString(", ").log("existing remote packages")

    val selectedModules = moduleIds.filter(_.organization == org)

    selectedModules.foreach { moduleId =>
      val moduleInfo = ModuleInfo(moduleId)
      val pkgName    = moduleInfo.pkgName
      if (!remotePackages.contains(pkgName)) {
        createPackage(pkgName).log("created package")
      } else {
        pkgName.log(s"package exists")
      }

      val latestVersion = getLatestVersion(pkgName).log(s"latest version for ${pkgName}")
      if (!latestVersion.contains(moduleId.revision)) {
        List(moduleInfo.jarMapping, moduleInfo.sourceJarMapping, moduleInfo.pomMapping).foreach {
          case (artifactFile, mavenPath) =>
            mavenPath.log("uploading")
            uploadAndPublish(pkgName, artifactFile, mavenPath).pretty.log()
        }
      } else {
        moduleId.revision.log(s"this version is already uploaded for ${pkgName}")
      }
    }

    selectedModules.map(x => ModuleInfo(x).dep)
  }

  def getPackages: List[String] = repo.packages()().get.map(_.name)

  def uploadAndPublish(pkgName: String, artifactFile: File, mavenPath: String): JValue =
    repo
      .get(pkgName)
      .mvnUpload(mavenPath, artifactFile)
      .publish(true)(dispatch.as.json4s.Json)
      .get

  def getLatestVersion(pkgName: String): Option[String] = repo.get(pkgName)().get.versions.headOption

  def createPackage(pkgName: String): bintry.Package =
    repo
      .createPackage(pkgName)
      .licenses("Apache-2.0")
      .vcs("https://github.com/mushtaq/typings")()
      .get
}
