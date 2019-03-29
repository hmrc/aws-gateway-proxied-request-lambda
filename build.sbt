import sbt.Keys._
import sbt._
import uk.gov.hmrc.DefaultBuildSettings.targetJvm
import uk.gov.hmrc.SbtArtifactory.autoImport.makePublicallyAvailableOnBintray
import uk.gov.hmrc.{SbtArtifactory, SbtAutoBuildPlugin}
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName = "aws-gateway-proxied-request-lambda"
lazy val appDependencies: Seq[ModuleID] = compileDependencies ++ testDependencies

lazy val jacksonVersion = "2.9.8"

lazy val compileDependencies = Seq(
  "io.github.mkotsur" %% "aws-lambda-scala" % "0.1.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
)

lazy val testScope: String = "test"

lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % testScope,
  "org.pegdown" % "pegdown" % "1.6.0" % testScope,
  "org.mockito" % "mockito-core" % "2.25.1" % testScope
)

lazy val library = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    scalaVersion := "2.11.11",
    name := appName,
    majorVersion := 0,
    makePublicallyAvailableOnBintray := true,
    targetJvm := "jvm-1.8",
    crossScalaVersions := Seq("2.11.11"),
    scalacOptions += "-Ypartial-unification",
    libraryDependencies ++= appDependencies,
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases")
    )
  )

// Coverage configuration
coverageMinimum := 90
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;uk.gov.hmrc.BuildInfo"
