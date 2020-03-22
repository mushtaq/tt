import java.io.File

import sbt.librarymanagement.ModuleID

case class ModuleInfo(moduleId: ModuleID) {
  def pkgName: String = moduleId.name.split("_").head

  def jarMapping: (File, String)       = artifactMapping("jars", ".jar")
  def sourceJarMapping: (File, String) = artifactMapping("srcs", "-sources.jar")
  def pomMapping: (File, String)       = artifactMapping("poms", ".pom")

  def dep: String = s""""${moduleId.organization}" %%% "$pkgName" % "${moduleId.revision}"""".stripMargin

  def artifactMapping(ivyDir: String, suffix: String): (File, String) = {
    val artifactFile = new File(
      s"${System.getProperty("user.home")}/.ivy2/local/${moduleId.organization}/${moduleId.name}/${moduleId.revision}/$ivyDir/${moduleId.name}$suffix"
    )
    val pathPrefix = moduleId.organization.replace(".", "/")
    val mavenPath  = s"${pathPrefix}/${moduleId.name}/${moduleId.revision}/${moduleId.name}-${moduleId.revision}$suffix"
    (artifactFile, mavenPath)
  }
}
