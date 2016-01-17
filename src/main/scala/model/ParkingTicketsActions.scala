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
    db.withSession { implicit session =>
      val infractionByCode = for {
        ((code, desc), c) <- table.filter(_.province === province).groupBy(col => (col.code, col.description))
      } yield (code, desc, c.length, c.map(_.fineAmount).sum)

      val topTenInfractions = infractionByCode.sortBy(_._3.desc).take(10).list()
      val infractions = topTenInfractions.map {
        case ((code, desc, count, amount)) => Infraction(code, desc, count, amount.get)
      }
      InfractionSummary(province, infractions)
    }
  }

  def getAllInfractionSummary(): InfractionSummary =  {
    db.withSession { implicit session =>
      val infractionByCode = for {
        ((code, desc), c) <- table.groupBy(col => (col.code, col.description))
      } yield (code, desc, c.length, c.map(_.fineAmount).sum)

      val topTenInfractions = infractionByCode.sortBy(_._3.desc).take(10).list()
      val infractions = topTenInfractions.map {
        case ((code, desc, count, amount)) => Infraction(code, desc, count, amount.get)
      }
      InfractionSummary("all", infractions)
    }
  }

  def getProvinceFineSummary(province: String): FineSummary = {
    db.withSession { implicit session =>
      val fineByMonth = for {
        (month, c) <- table.filter(_.province === province).groupBy(_.month)
      } yield (month, c.length, c.map(_.fineAmount).sum)

      val orderByMonth = fineByMonth.sortBy(_._1.asc).list()
      val fines = orderByMonth.map {
        case ((month, count, amount)) => MonthlyFine(month, count, amount.get)
      }
      FineSummary(province, fines)
    }
  }

  def getAllFineSummary(): FineSummary = {
    db.withSession { implicit session =>
      val fineByMonth = for {
        (month, c) <- table.groupBy(_.month)
      } yield (month, c.length, c.map(_.fineAmount).sum)

      val orderByMonth = fineByMonth.sortBy(_._1.asc).list()
      val fines = orderByMonth.map {
        case ((month, count, amount)) => MonthlyFine(month, count, amount.get)
      }
      FineSummary("all", fines)
    }
  }
}
