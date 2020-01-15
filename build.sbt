name := "rate-limiting"

version := "0.1"

organization := "bertrand"

scalaVersion := "2.12.10"

val Http4sVersion = "0.20.15"
val CirceVersion = "0.11.1"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"

libraryDependencies ++= Seq(
  // Web server
  "org.http4s"    %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"    %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s"    %% "http4s-circe" % Http4sVersion,
  "org.http4s"    %% "http4s-dsl" % Http4sVersion,
  // JSON encoding and decoding
  "io.circe"      %% "circe-generic" % CirceVersion,
  // Logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  // Tests
  "org.specs2"    %% "specs2-core"         % Specs2Version % "test",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)

addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3")

addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")

scalacOptions ++= Seq(
  "-deprecation", // Warn about deprecated features
  "-explaintypes", // Explain type errors in more detail
  "-encoding", "UTF-8", // Specify character encoding used by source files
  "-feature", // Emit warning and location for usages of features that should be imported explicitly
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds", // Allow higher-kinded types
  "-unchecked", // Enable additional warnings where generated code depends on assumptions
  // "-Xfatal-warnings", // Fail on warnings
  "-Xlint:_", // Enable all available style warnings
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver
  "-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-unused-import",
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
)

scalacOptions in Test --= Seq(
  "-Xlint:_",
  "-Ywarn-unused-import",
)
