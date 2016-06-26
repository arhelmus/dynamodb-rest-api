name := """dynamodb-rest-api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "com.amazonaws" % "aws-java-sdk" % "1.11.10",
  "com.github.dwhjames" %% "aws-wrap" % "0.8.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += Resolver.bintrayRepo("dwhjames", "maven")
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

startDynamoDBLocal <<= startDynamoDBLocal.dependsOn(compile in Test)
test in Test <<= (test in Test).dependsOn(startDynamoDBLocal)
testOptions in Test <+= dynamoDBLocalTestCleanup