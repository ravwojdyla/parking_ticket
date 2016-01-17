package api

import akka.actor.Actor
import model.ParkingTicketsActions
import spray.routing._
import spray.http._
import MediaTypes._
import spray.json._
import model.JsonProtocols._
class ParkingTicketServiceActor extends Actor with ParkingTicketService {
  def actorRefFactory = context
  def receive = runRoute(myRoute)
}

//TODO add unit test
trait ParkingTicketService extends HttpService with ParkingTicketsActions {
  val myRoute =
    path("infractions"){
      get {
        respondWithMediaType(`application/json`) {
          complete {
            getAllInfractionSummary().toJson.toString()
          }
        }
      }
    } ~
  path("infractions"/ Rest){ province =>
    get {
      respondWithMediaType(`application/json`) {
        complete {
          getProvinceInfractionSummary(province).toJson.toString()
        }
      }
    }
    } ~
  path("fines"){
    get {
      respondWithMediaType(`application/json`) {
        complete {
          getAllFineSummary().toJson.toString()
        }
      }
    }
  } ~
  path("fines" / Rest){ province =>
    get {
      respondWithMediaType(`application/json`) {
        complete {
          getProvinceFineSummary(province).toJson.toString()
        }
      }
    }
  }
}