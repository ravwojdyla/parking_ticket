package model

import scala.slick.driver.H2Driver.simple._

class ParkingTickets(tag: Tag)
  extends Table[(String, Int, String, Double, Int)] (tag, "PARKING_TICKET") {
  def province: Column[String] = column[String]("PROVINCE")
  def code: Column[Int] = column[Int]("CODE")
  def description: Column[String] = column[String]("DESCRIPTION")
  def fineAmount: Column[Double] = column[Double]("FINE_AMOUNT")
  def month: Column[Int] = column[Int]("MONTH")
  def * = (province, code, description, fineAmount, month)
}
