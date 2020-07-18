import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import daos.CovidObservationDao
import javax.inject.{Inject, Singleton}
import models.CovidObservation
import play.api.Logging
import play.api.inject.ApplicationLifecycle

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import utils.StringParser._

import scala.concurrent.duration.Duration
import scala.io.Source
import scala.util.{Failure, Success}

@Singleton
class ApplicationStart @Inject()(lifecycle: ApplicationLifecycle,
                                 covidObservationDao: CovidObservationDao)(implicit ec: ExecutionContext)
  extends Logging {

  logger.info("Startup code initiated")

  // Treat this as a one-off script to satisfy second requirement in an encapsulated manner:
  // 2. On startup of the web application, parse the CSV file and
  //    store the relevant data in a table named covid_observations.
  // Ideally seeding table data is done via API or an independent throwaway script.

  // not required for constructor class, but define one here and call at end
  // to make startup more explicit
  def initialize(): Unit = {
    covidObservationDao.count.map {
      case 0 =>
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

          CovidObservation(
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

        val insertCount: Future[Int] = covidObservationDao.batchInsert(datapoints.toSeq)
        logger.info("Awaiting first-time seeding of covid_observations table.")

        insertCount onComplete {
          case Success(n) => logger.info(s"Seeded covid_observations with $n rows.")
          case Failure(_) => logger.warn("Failed to insert seed data.")
        }

        Await.result(insertCount, 5.seconds)
      case _ =>
        logger.info("Table to seed is not empty. Seeding skipped.")
    }
  }

  // Shut-down hook
  lifecycle.addStopHook { () =>
    Future.successful(())
  }

  initialize()
}