resolvers += Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
resolvers += "HMRC Releases" at "https://dl.bintray.com/hmrc/releases"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.9")
addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "2.10.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "2.1.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("uk.gov.hmrc" % "sbt-artifactory" % "1.6.0")
