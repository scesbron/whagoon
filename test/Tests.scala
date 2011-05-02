import java.util.Date
import play._
import play.test._

import org.scalatest._
import org.scalatest.junit._
import org.scalatest.matchers._

import models._
import play.db.anorm._

class BasicTests extends UnitFlatSpec with ShouldMatchers with BeforeAndAfterEach {

  override def beforeEach() {
      Fixtures.deleteDatabase()
  }

  it should "create and retrieve a Environment" in {

      Environment.create(Environment(NotAssigned, "production"))

      val production = Environment.find("name={name}").on("name" -> "production").first()

      production should not be (None)
      production.get.name should be ("production")
  }
  it should "create a WebloDomain" in {

    val env = Environment.create(Environment(NotAssigned, "production"))
    WebloDomain.create(WebloDomain(NotAssigned, "dlip00", "55.11.109.106", "13100", "weblogic", "weblogic", env.get.id()))

    WebloDomain.count().single() should be (1)

    val domains = WebloDomain.find("env_id={id}").on("id" -> env.get.id()).as(WebloDomain*)

    domains.length should be (1)

    val firstDomain = domains.headOption

    firstDomain should not be (None)
    firstDomain.get.env_id should be (env.get.id())
    firstDomain.get.name should be ("dlip00")
    firstDomain.get.host should be ("55.11.109.106")
    firstDomain.get.port should be ("13100")
    firstDomain.get.username should be ("weblogic")
    firstDomain.get.password should be ("weblogic")
  }
  // TODO Tester une contrainte unique sur le nom
  it should "create a WebloServer" in {

    val env = Environment.create(Environment(NotAssigned, "production"))
    val domain = WebloDomain.create(WebloDomain(NotAssigned, "dlip00", "55.11.109.106", "13100", "weblogic", "weblogic", env.get.id()))
    WebloServer.create(WebloServer(NotAssigned, "dlip00_c04_s01", "55.11.109.106", "13132", domain.get.id()))
    WebloServer.create(WebloServer(NotAssigned, "dlip00_c04_s02", "55.11.109.107", "13134", domain.get.id()))
    WebloServer.count().single() should be (2)

    val servers = WebloServer.find("domain_id={id}").on("id" -> domain.get.id()).as(WebloServer*)

    servers.length should be (2)

    val firstServer = servers.headOption

    firstServer should not be (None)
    firstServer.get.domain_id should be (domain.get.id())
    firstServer.get.name should be ("dlip00_c04_s01")
    firstServer.get.host should be ("55.11.109.106")
    firstServer.get.port should be ("13132")
  }
  it should "load a complex graph from Yaml" in {

    Yaml[List[Any]]("data.yml").foreach {
      _ match {
        case e:Environment => Environment.create(e)
        case d:WebloDomain => WebloDomain.create(d)
        case s:WebloServer => WebloServer.create(s)
      }
    }

    Environment.count().single() should be (1)
    WebloDomain.count().single() should be (1)
    WebloServer.count().single() should be (2)

    val Some((domain, servers)) = WebloDomain.withServers(1)
    domain.name should be ("dd1s00")
    servers.length should be (2)
  }
}