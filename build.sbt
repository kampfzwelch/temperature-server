name := """temperature-server"""

version := "0.1-SNAPSHOT"

scalacOptions += "-target:jvm-1.8"

scalaVersion  := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Dependencies.allDependencies
