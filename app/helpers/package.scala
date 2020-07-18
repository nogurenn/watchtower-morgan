import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import slick.jdbc.PostgresProfile.api._
import java.sql.{Date, Timestamp}
import java.time.{LocalDateTime, LocalDate}

package object helpers {
  implicit val localDateTimeToTimestamp: BaseColumnType[LocalDateTime] =
    MappedColumnType.base[LocalDateTime, Timestamp](
      ldt => Timestamp.valueOf(ldt),
      ts => ts.toLocalDateTime
    )

  implicit val localDateToDate: BaseColumnType[LocalDate] =
    MappedColumnType.base[LocalDate, Date](
      ld => Date.valueOf(ld),
      d => d.toLocalDate
    )
}
