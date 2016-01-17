package api

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import model.DatabaseConfig
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot extends App {
  implicit val system = ActorSystem("api-root")
  val service = system.actorOf(Props[ParkingTicketServiceActor], "parking-service")
  implicit val timeout = Timeout(5.seconds)
  DatabaseConfig.init()
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8888)
}
