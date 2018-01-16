name := "workshops"

version := "1.0"

scalaVersion := "2.12.1"

val akkaHttpVersion = "10.0.5"
resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "io.monix" %% "monix" % "2.3.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)