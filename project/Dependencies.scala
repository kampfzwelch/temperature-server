import sbt._
  
// see slide 9:  http://jsuereth.com/scala/2013/06/11/effective-sbt.html
object Dependencies {
  
  private val allDeps: Seq[ModuleID] = Seq(
	"joda-time" % "joda-time" % "2.7",
  "com.google.inject" % "guice" % "4.0",
  "javax.inject" % "javax.inject" % "1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.10",
  "org.mockito" % "mockito-core" % "1.10.17" % "test"
  )
  
  private val webjarDeps: Seq[ModuleID] = Seq(
  "org.webjars" % "bootstrap" % "3.3.1",
  "org.webjars" % "angularjs" % "1.3.8",
  "org.webjars" % "angular-ui-bootstrap" % "0.12.0"
  )
  

  val allDependencies: Seq[ModuleID] = Seq(
    allDeps,
    webjarDeps
  ).flatten

}
