package lunatech.lunchplanner.common

import lunatech.lunchplanner.persistence.{ DishTable, MenuDishTable, MenuPerDayPerPersonTable, MenuPerDayTable, MenuTable, UserProfileTable, UserTable }
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent._
import scala.concurrent.duration._

trait TestDatabaseProvider {
  self: DBConnectionProvider =>

  val defaultTimeout = 10.seconds

  val userProfileTable: TableQuery[UserProfileTable] = TableQuery[UserProfileTable]
  val userTable: TableQuery[UserTable] = TableQuery[UserTable]
  val dishTable: TableQuery[DishTable] = TableQuery[DishTable]
  val menuTable: TableQuery[MenuTable] = TableQuery[MenuTable]
  val menuDishTable: TableQuery[MenuDishTable] = TableQuery[MenuDishTable]
  val menuPerDayTable: TableQuery[MenuPerDayTable] = TableQuery[MenuPerDayTable]
  val menuPerDayPerPersonTable: TableQuery[MenuPerDayPerPersonTable] = TableQuery[MenuPerDayPerPersonTable]

  def cleanDatabase() = {
    Await.result(db.run(menuPerDayPerPersonTable.delete), defaultTimeout)
    Await.result(db.run(menuPerDayTable.delete), defaultTimeout)
    Await.result(db.run(menuDishTable.delete), defaultTimeout)
    Await.result(db.run(dishTable.delete), defaultTimeout)
    Await.result(db.run(menuTable.delete), defaultTimeout)
    Await.result(db.run(userProfileTable.delete), defaultTimeout)
    Await.result(db.run(userTable.delete), defaultTimeout)
  }

}
