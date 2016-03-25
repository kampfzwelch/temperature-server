package controllers

import play.modules.reactivemongo.MongoController
import scala.concurrent.Future
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{ LoggerFactory, Logger }
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json.Json
import org.joda.time.DateTime
import com.google.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.ReactiveMongoComponents

import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import play.api.libs.json.JsArray
@Singleton
class TemperatureController @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends Controller with MongoController with ReactiveMongoComponents {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[TemperatureController])

  def collection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("temp")

  // ------------------------------------------ //
  // Using case classes + Json Writes and Reads //
  // ------------------------------------------ //
  import models.Temperature
  import models.Temperature._

  def dayStatistic(room: String, day: Int, month: Int, year: Int, futureTemperatureList: Future[List[Temperature]]) = {
    val temps = Vector.tabulate(24)(n => (0, 0.0))
    val ttt = futureTemperatureList map (t => t.foldLeft(temps)((li, temp) => li.updated(temp.hour, (li(temp.hour)._1 + 1, li(temp.hour)._2 + temp.temp))))
    ttt.map(x => List.tabulate(24)(n => Temperature(if (x(n)._1 == 0) 0.0 else x(n)._2 / x(n)._1, room, n, 0, day, month, year)))
  }

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

  def getTemperatureForRoomToday(room: String) = {
    val dt = new DateTime()
    getTemperatureForRoom(room, dt.getDayOfMonth, dt.getMonthOfYear, dt.getYear)
  }

  def getTemperatureForRoom(room: String, day: Int, month: Int, year: Int) = Action.async {
    val futureTemperatureList = getTemperateDataForDay(room, day, month, year);
    // transform the list into a JsArray
    val futureTemperatureJsonArray: Future[JsArray] =
      dayStatistic(room = room, day = day, month = month, year = year, futureTemperatureList)
        .map { temp =>
          Json.arr(temp)
        }
    futureTemperatureJsonArray.map(t => Ok(t(0).get))
  }

  def getTemperateDataForDay(room: String, day: Int, month: Int, year: Int): Future[List[Temperature]] = {
    val cursor: Cursor[Temperature] = collection.
      find(Json.obj("room" -> room, "day" -> day, "month" -> month, "year" -> year)).
      sort(Json.obj("created" -> -1)).cursor[Temperature]

    cursor.collect[List]()
  }

}
