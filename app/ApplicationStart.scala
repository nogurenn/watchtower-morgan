import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, LocalDateTime, OffsetDateTime, ZoneId, ZonedDateTime}

import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.inject.ApplicationLifecycle
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import tables.Tables._
import utils.StringParser._

import scala.io.Source
import scala.util.{Failure, Success}

@Singleton
class ApplicationStart @Inject() (protected val dbConfigProvider: DatabaseConfigProvider,
                                  lifecycle: ApplicationLifecycle)(implicit ec: ExecutionContext)
  extends Logging with HasDatabaseConfigProvider[JdbcProfile] {

  logger.info("Startup code initiated")

  import profile.api._

  // Treat this as a one-off script to satisfy second requirement in an encapsulated manner:
  // 2. On startup of the web application, parse the CSV file and
  //    store the relevant data in a table named covid_observations.
  // Ideally seeding table data is done via API or an independent throwaway script.

  // not required for constructor class, but define one here and call at end
  // to make startup more explicit
  def initialize(): Unit = {
    val query = CovidObservations.length.result.flatMap {
      case 0 =>
        // populate table
        logger.info("Populating tables with initial data.")

        val datapoints = for (line <- Source.fromResource("covid_19_data.csv").getLines().drop(1)) yield { // drop header line
          val cols = csvParser.parseLine(line)

          // this is more readable versus using DateTimeFormatterBuilder.
          // it's a one-off table-seeding code so performance matters little unless dataset is large.
          val lastUpdatePatterns =
          """[M/d/uuuu H:m]
            |[M/d/uu H:m]
            |[uuuu-MM-dd'T'H:m:s]
            |[uuuu-MM-dd H:m:s]""".stripMargin.replaceAll("\n", "")

          CovidObservationsRow(
            0,
            LocalDate.parse(cols(1), DateTimeFormatter.ofPattern("MM/dd/uuuu")),  // 01/31/2020
            if (cols(2).isEmpty) None else Some(cols(2)),
            cols(3),
            LocalDateTime.parse(cols(4), DateTimeFormatter.ofPattern(lastUpdatePatterns)),
            cols(5).toDouble.toInt,
            cols(6).toDouble.toInt,   // human count is discrete. convert to int.
            cols(7).toDouble.toInt
          )
        }

        (CovidObservations ++= datapoints.to(Iterable)).map {
          case Some(n) =>
            logger.info(s"Successfully seeded $n rows.")
            n
          case None =>
            logger.error("A part of the required seed failed to be inserted.")
            0
        }
      case _ =>
        logger.info("Initial data already present. Seeding skipped.")
        DBIO.successful(0)
    }

    db.run(query)
  }

  // Shut-down hook
  lifecycle.addStopHook { () =>
    Future.successful(())
  }

  initialize()
}