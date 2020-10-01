name := """amink-mtg-app"""
organization := "amink"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += ws
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.16"
libraryDependencies += jdbc

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "amink.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "amink.binders._"
