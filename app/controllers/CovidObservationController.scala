package controllers

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import daos.CovidObservationDao
import controllers.CovidObservationController.GetTopConfirmedRequest
import javax.inject._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

object CovidObservationController {
  case class GetTopConfirmedRequest(
      observationDate: Option[String],
      limit: Option[Int]
  )

  implicit def getTopConfirmedRequestQueryStringBindable =
    new QueryStringBindable[GetTopConfirmedRequest] {
      override def bind(
          key: String,
          params: Map[String, Seq[String]]
      ): Option[Either[String, GetTopConfirmedRequest]] = {
        for {
          observationDate <-
            QueryStringBindable
              .bindableOption[String]
              .bind("observation_date", params)
          limit <- QueryStringBindable.bindableOption[Int].bind("max_results", params)
        } yield {
          (observationDate, limit) match {
            case (Right(observationDate), Right(limit)) =>
              Right(GetTopConfirmedRequest(observationDate, limit))
            case _ => Left("Unable to bind a GetTopConfirmedRequed")
          }
        }
      }

      override def unbind(
          key: String,
          request: GetTopConfirmedRequest
      ): String = {
        Seq(
          QueryStringBindable
            .bindableOption[String]
            .unbind("observation_date", request.observationDate),
          QueryStringBindable
            .bindableOption[Int]
            .unbind("max_results", request.limit)
        ).mkString("&")
      }
    }
}

@Singleton
class CovidObservationController @Inject() (
    covidObservationDao: CovidObservationDao,
    val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BaseController
    with Logging {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] =
    Action { implicit req: Request[AnyContent] =>
      logger.info(s"GET ${req.uri}")
      Ok(views.html.index())
    }

  def getTopConfirmed(request: GetTopConfirmedRequest): Action[AnyContent] =
    Action.async { implicit req =>
      logger.info("GET ${req.uri}")

      for {
        payload <- covidObservationDao.getAllByObservationDate(
          request.observationDate.map(LocalDate.parse),
          request.limit
        )
      } yield {
        val countries = payload.map(p =>
          Json.obj(
            "country" -> p.country,
            "confirmed" -> p.confirmed,
            "deaths" -> p.dead,
            "recovered" -> p.recovered
          )
        )

        Ok(
          Json.obj(
            "observation_date" -> request.observationDate,
            "countries" -> countries
          )
        )
      }
    }
}
