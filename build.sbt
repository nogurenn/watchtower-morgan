import slick.codegen.SourceCodeGenerator
import slick.{model => m}

name := """watchtower-morgan"""
organization := "liwanag.glenn"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, FlywayPlugin, CodegenPlugin)

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  "net.codingwell" %% "scala-guice" % "4.2.11",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,

  // database driver + Slick ORM
  "org.postgresql" % "postgresql" % "42.2.14",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "com.typesafe.slick" %% "slick-codegen" % "3.3.2",

  // use csv parser instead of regex
  "com.univocity" % "univocity-parsers" % "2.8.4"
)


// DATABASE
lazy val dbHost = sys.env.getOrElse("PLAY_DB_HOST", "127.0.0.1")
lazy val dbPort = sys.env.getOrElse("PLAY_DB_PORT", "5432")
lazy val dbName = sys.env.getOrElse("PLAY_DB_NAME", "watchtower_morgan_dev")
lazy val dbUrl = s"jdbc:postgresql://${dbHost}:${dbPort}/${dbName}?currentSchema=public"
lazy val dbUser = sys.env.getOrElse("PLAY_DB_USER", "watchtower_morgan_dev")
lazy val dbPassword = sys.env.getOrElse("PLAY_DB_PASS", "watchtower_morgan_dev")
// -- migrations
flywayUrl := dbUrl
flywayUser := dbUser
flywayPassword := dbPassword
flywayLocations := Seq("classpath:db/migration/default")
// -- model generation
slickCodegenDatabaseUrl := dbUrl
slickCodegenDatabaseUser := dbUser
slickCodegenDatabasePassword := dbPassword
slickCodegenDriver := slick.jdbc.PostgresProfile
slickCodegenJdbcDriver := "org.postgresql.Driver"
slickCodegenOutputPackage := "tables"
slickCodegenExcludedTables := Seq("flyway_schema_history")
slickCodegenCodeGenerator := { (model: m.Model) =>
  new SourceCodeGenerator(model) {
    override def code =
      "import helpers.{localDateTimeToTimestamp, localDateToDate}\n" + super.code
    override def Table = new Table(_) {
      override def Column = new Column(_) {
        override def rawType = model.tpe match {
          case "java.sql.Timestamp" => "java.time.LocalDateTime"  // kill j.s.Timestamp
          case "java.sql.Date" => "java.time.LocalDate"           // kill j.s.Date
          case _ =>
            super.rawType
        }
      }
    }
  }
}
slickCodegenOutputDir := (scalaSource in Compile).value
//sourceGenerators in Compile += slickCodegen.taskValue
