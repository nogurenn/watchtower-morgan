import com.google.inject.AbstractModule
import daos._
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with ScalaModule {
  override def configure(): Unit = {
    bind[ApplicationStart].asEagerSingleton

    bind[CovidObservationDao].to[CovidObservationDaoImpl]
  }
}
