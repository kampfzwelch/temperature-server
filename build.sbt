name := """temperature-server"""

version := "0.1-SNAPSHOT"

scalacOptions += "-target:jvm-1.8"

scalaVersion  := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
	"joda-time" % "joda-time" % "2.7",
  "com.google.inject" % "guice" % "4.0",
  "javax.inject" % "javax.inject" % "1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.10",
  "org.webjars" % "bootstrap" % "3.3.1",
  "org.webjars" % "angularjs" % "1.3.8",
  "org.webjars" % "angular-ui-bootstrap" % "0.12.0",
  "org.mockito" % "mockito-core" % "1.10.17" % "test")
