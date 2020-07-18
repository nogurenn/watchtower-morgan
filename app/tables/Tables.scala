package tables
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import helpers.{localDateTimeToTimestamp, localDateToDate}
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = CovidObservations.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table CovidObservations
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param observationDate Database column observation_date SqlType(date)
   *  @param locality Database column locality SqlType(varchar), Length(255,true), Default(None)
   *  @param country Database column country SqlType(varchar), Length(64,true)
   *  @param lastUpdate Database column last_update SqlType(timestamp)
   *  @param confirmed Database column confirmed SqlType(int4)
   *  @param dead Database column dead SqlType(int4)
   *  @param recovered Database column recovered SqlType(int4) */
  case class CovidObservationsRow(id: Long, observationDate: java.time.LocalDate, locality: Option[String] = None, country: String, lastUpdate: java.time.LocalDateTime, confirmed: Int, dead: Int, recovered: Int)
  /** GetResult implicit for fetching CovidObservationsRow objects using plain SQL queries */
  implicit def GetResultCovidObservationsRow(implicit e0: GR[Long], e1: GR[java.time.LocalDate], e2: GR[Option[String]], e3: GR[String], e4: GR[java.time.LocalDateTime], e5: GR[Int], e6: GR[Option[java.time.LocalDateTime]]): GR[CovidObservationsRow] = GR{
    prs => import prs._
    CovidObservationsRow.tupled((<<[Long], <<[java.time.LocalDate], <<?[String], <<[String], <<[java.time.LocalDateTime], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table covid_observations. Objects of this class serve as prototypes for rows in queries. */
  class CovidObservations(_tableTag: Tag) extends profile.api.Table[CovidObservationsRow](_tableTag, "covid_observations") {
    def * = (id, observationDate, locality, country, lastUpdate, confirmed, dead, recovered) <> (CovidObservationsRow.tupled, CovidObservationsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(observationDate), locality, Rep.Some(country), Rep.Some(lastUpdate), Rep.Some(confirmed), Rep.Some(dead), Rep.Some(recovered)).shaped.<>({r=>import r._; _1.map(_=> CovidObservationsRow.tupled((_1.get, _2.get, _3, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported.")))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column observation_date SqlType(date) */
    val observationDate: Rep[java.time.LocalDate] = column[java.time.LocalDate]("observation_date")
    /** Database column locality SqlType(varchar), Length(255,true), Default(None) */
    val locality: Rep[Option[String]] = column[Option[String]]("locality", O.Length(255,varying=true), O.Default(None))
    /** Database column country SqlType(varchar), Length(64,true) */
    val country: Rep[String] = column[String]("country", O.Length(64,varying=true))
    /** Database column last_update SqlType(timestamp) */
    val lastUpdate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("last_update")
    /** Database column confirmed SqlType(int4) */
    val confirmed: Rep[Int] = column[Int]("confirmed")
    /** Database column dead SqlType(int4) */
    val dead: Rep[Int] = column[Int]("dead")
    /** Database column recovered SqlType(int4) */
    val recovered: Rep[Int] = column[Int]("recovered")
  }
  /** Collection-like TableQuery object for table CovidObservations */
  lazy val CovidObservations = new TableQuery(tag => new CovidObservations(tag))
}
