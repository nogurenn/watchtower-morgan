package daos

import java.time.LocalDate

import javax.inject.{Inject, Singleton}
import models.CovidObservation
import play.api.Logging
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import tables.Tables.{CovidObservations, CovidObservationsRow}

trait CovidObservationDao {
  def getAll: Future[Seq[CovidObservation]]

  def getAllByObservationDate(
      observationDate: Option[LocalDate] = None,
      limit: Option[Int]
  ): Future[Seq[(String, Int, Int, Int)]]

  def count: Future[Int]

  def batchInsert(data: Seq[CovidObservation]): Future[Int]
}

object CovidObservationDao {}

@Singleton
class CovidObservationDaoImpl @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
    extends CovidObservationDao
    with Logging
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private def modelToRow(m: CovidObservation): CovidObservationsRow = {
    CovidObservationsRow(
      m.id,
      m.observationDate,
      m.locality,
      m.country,
      m.lastUpdate,
      m.confirmed,
      m.dead,
      m.recovered
    )
  }

  private def rowToModel(r: CovidObservationsRow): CovidObservation = {
    CovidObservation(
      r.id,
      r.observationDate,
      r.locality,
      r.country,
      r.lastUpdate,
      r.confirmed,
      r.dead,
      r.recovered
    )
  }

  def getAll: Future[Seq[CovidObservation]] =
    db.run(CovidObservations.result).map(_.map(rowToModel))

  /**
    * Perform joins, filters, sorts at the db level as much as possible as an infrastructure decision.
    * Abstract sorting and filtering when app grows.
    *
    * Select all by `observationDate`. order by `confirmed` DESC, limit `n`
    */
  def getAllByObservationDate(
      observationDate: Option[LocalDate] = None,
      limit: Option[Int] = None
  ): Future[Seq[(String, Int, Int, Int)]] = {
    logger.info(
      s"getAllByObservationDate: ${observationDate.toString},${limit.toString}"
    )
    db.run {
      val filtered = CovidObservations
        .filterIf(observationDate.isDefined)(_.observationDate === observationDate.get)
        .groupBy(_.country)
        .map {
          case (country, r) =>
            (
              country,
              r.map(_.confirmed).sum.getOrElse(0),
              r.map(_.dead).sum.getOrElse(0),
              r.map(_.recovered).sum.getOrElse(0)
            )
        }
        .sortBy(_._2.desc)

      val limited = limit match {
        case None    => filtered
        case Some(n) => filtered.take(n)
      }

      limited.result
    }
  }

  def count: Future[Int] = db.run(CovidObservations.length.result)

  def batchInsert(data: Seq[CovidObservation]): Future[Int] = {
    logger.info(s"batchInsert: ${data.length} incoming rows.")
    db.run(CovidObservations ++= data.map(modelToRow)).map {
      case None =>
        logger.error(s"batchInsert: Partial or total failure. Check db logs.")
        0
      case Some(n) =>
        logger.info(s"batchInsert: $n rows created.")
        n
    }
  }
}
