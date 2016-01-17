package model

import scala.io.Source
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta.MTable
import scala.util.{Success, Try}

object DatabaseConfig {
  val db = Database.forURL("jdbc:h2:mem:todo-list;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1")
  val table: TableQuery[ParkingTickets] = TableQuery[ParkingTickets]
  def init() = {
    def parseInt(value: String): Int = {
      Try(value.toInt) match {
        case Success(v) => v
        case _ => 0
      }
    }

    def parseDouble(value: String): Double = {
      Try(value.toDouble) match {
        case Success(v) => v
        case _ => 1.0
      }
    }
    db.withTransaction { implicit session =>
      table.ddl.create
      val files = (1 to 4).map(i => s"/parking_tickets_2014/Parking_Tags_Data_2014_$i.csv")
      for {
        fileName <- files
        file = this.getClass.getResource(fileName)
        row <- Source.fromURL(file).getLines()
      } {
        //TODO improve parsing logic
        val value = row.split(",")
        if( value.size == 11 && value(0) != "tag_number_masked") {
          val month = parseInt(value(1).substring(4, 6))
          val code = parseInt(value(2))
          val desc = value(3)
          val amount = parseDouble(value(4))
          val province = value(10)
          table +=(province, code, desc, amount, month)
        }
      }
    }
  }
}
