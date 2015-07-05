package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{ LoggerFactory, Logger }
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._
import org.joda.time.DateTime

@Singleton
class TemperatureController extends Controller with MongoController {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[TemperatureController])

  def collection: JSONCollection = db.collection[JSONCollection]("temp")

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //

  import models._
  import models.JsonFormats._

  def logTemperatureForRoom(room: String, temperature: Double) = Action.async {
    val dt: DateTime = new DateTime();
    val temp: Temperature = Temperature(temperature, room,
      dt.getHourOfDay, dt.getMinuteOfHour, dt.getDayOfMonth, dt.getMonthOfYear, dt.getYear);

    collection.insert(temp).map {
      lastError =>
        logger.debug(s"Successfully inserted with LastError: $lastError")
        Created(s"Temperature logged")
    }
  }

  def clearTemp() = Action.async {
    db.drop().map { lastError =>
      logger.debug(s"Successfully dropped all temp data with LastError: $lastError")
      Ok(s"Temperature dropped")
    }
  }

  def getTemperatureForRoomToday(room: String) = Action.async {
    val dt = new DateTime()
    executeQuery(room, dt.getDayOfMonth, dt.getMonthOfYear, dt.getYear)
  }

  def getTemperatureForRoom(room: String, day: Int, month: Int, year: Int) = Action.async {
    executeQuery(room, day, month, year);
  }

  def executeQuery(room: String, day: Int, month: Int, year: Int): Future[Result] = {
    // let's do our query
    val cursor: Cursor[Temperature] = collection.
      // find all
      find(Json.obj("room" -> room, "day" -> day, "month" -> month, "year" -> year)).
      // sort them by creation date
      sort(Json.obj("created" -> -1)).
      // perform the query and get a cursor of JsObject
      cursor[Temperature]

    // gather all the JsObjects in a list
    val futureTemperatureList: Future[List[Temperature]] = cursor.collect[List]()

    // transform the list into a JsArray
    val futureTemperatureJsonArray: Future[JsArray] = futureTemperatureList.map { temp =>
      Json.arr(temp)
    }
    // everything's ok! Let's reply with the array
    futureTemperatureJsonArray.map {
      users =>
        Ok(users(0))
    }
  }
}
