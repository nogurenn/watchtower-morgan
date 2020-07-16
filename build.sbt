name := """watchtower-morgan"""
organization := "liwanag.glenn"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, FlywayPlugin)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "org.postgresql" % "postgresql" % "42.2.14"
)

// TODO: use env vars before dockerizing
flywayUrl := "jdbc:postgresql://127.0.0.1:5432/watchtower_morgan_dev"
flywayUser := "watchtower_morgan_dev"
flywayPassword := "watchtower_morgan_dev"
flywayLocations := Seq("classpath:db/migration/default")
