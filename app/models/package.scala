import java.time.{LocalDate, LocalDateTime}

import play.api.libs.json.{Json, Reads, Writes}

package object models {

  case class CovidObservation(id: Long,
                              observationDate: LocalDate,
                              locality: Option[String],
                              country: String,
                              lastUpdate: LocalDateTime,
                              confirmed: Int,
                              dead: Int,
                              recovered: Int)

  implicit val readsCovidObservation: Reads[CovidObservation] = Json.reads[CovidObservation]
  implicit val writesCovidObservation: Writes[CovidObservation] = Json.writes[CovidObservation]

}
