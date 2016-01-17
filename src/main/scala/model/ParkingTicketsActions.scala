package model

import DatabaseConfig._
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._
import scala.slick.driver.H2Driver.simple._

case class Infraction(infractionCode: Int, infractionDescription: String, count: Long, amount: Double)
case class InfractionSummary(province: String, infractionStatus: List[Infraction])
case class MonthlyFine(month: Int, fineCount: Long, fineAmount: Double)
case class FineSummary(province: String, monthyStatus: List[MonthlyFine])

object JsonProtocols extends DefaultJsonProtocol {
  implicit val infractionFormat= jsonFormat4(Infraction)
  implicit val infractionSummaryFormat = jsonFormat2(InfractionSummary)
  implicit val fineFormat = jsonFormat3(MonthlyFine)
  implicit val fineSummaryProtocol = jsonFormat2(FineSummary)
}

//TODO add unit test
trait ParkingTicketsActions {

  def getProvinceInfractionSummary(province: String): InfractionSummary = {
    getInfractionSummary(_.province === province)
  }

  def getAllInfractionSummary(): InfractionSummary =  {
    getInfractionSummary(_ => true)
  }

  def getInfractionSummary(f: ParkingTickets => Column[Boolean]) = {
    db.withSession { implicit session =>
      val infractionByCode = for {
        ((code, desc), c) <- table.filter(f).groupBy(col => (col.code, col.description))
      } yield (code, desc, c.length, c.map(_.fineAmount).sum)

      val topTenInfractions = infractionByCode.sortBy(_._3.desc).take(10).list()
      val infractions = topTenInfractions.map {
        case ((code, desc, count, amount)) => Infraction(code, desc, count, amount.get)
      }
      InfractionSummary("all", infractions)
    }
  }

  def getProvinceFineSummary(province: String): FineSummary = {
    getAllFineSummary(_.province === province)
  }

  def getAllFineSummary(): FineSummary = {
    getAllFineSummary(_ => true)
  }

  def getAllFineSummary(f: ParkingTickets => Column[Boolean]): FineSummary = {
    db.withSession { implicit session =>
      val fineByMonth = for {
        (month, c) <- table.filter(f).groupBy(_.month)
      } yield (month, c.length, c.map(_.fineAmount).sum)

      val orderByMonth = fineByMonth.sortBy(_._1.asc).list()
      val fines = orderByMonth.map {
        case ((month, count, amount)) => MonthlyFine(month, count, amount.get)
      }
      FineSummary("all", fines)
    }
  }
}
