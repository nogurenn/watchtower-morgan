name := """watchtower-morgan"""
organization := "liwanag.glenn"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, FlywayPlugin)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,

  // database driver + Slick ORM
  "org.postgresql" % "postgresql" % "42.2.14",
  "com.typesafe.play" %% "play-slick" % "5.0.0",

)

lazy val dbHost = sys.env.getOrElse("PLAY_DB_HOST", "127.0.0.1")
flywayUrl := s"jdbc:postgresql://${dbHost}:5432/watchtower_morgan_dev"
flywayUser := "watchtower_morgan_dev"
flywayPassword := "watchtower_morgan_dev"
flywayLocations := Seq("classpath:db/migration/default")
