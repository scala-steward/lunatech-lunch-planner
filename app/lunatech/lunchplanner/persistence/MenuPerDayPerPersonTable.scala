package lunatech.lunchplanner.persistence

import java.util.UUID

import lunatech.lunchplanner.common.DBConnection
import lunatech.lunchplanner.models.{ MenuPerDay, MenuPerDayPerPerson, User }
import slick.driver.PostgresDriver.api._
import slick.lifted.{ ForeignKeyQuery, ProvenShape, TableQuery }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MenuPerDayPerPersonTable(tag: Tag) extends Table[MenuPerDayPerPerson](tag, "MenuPerDayPerPerson") {
  private val menuPerDayTable = TableQuery[MenuPerDayTable]
  private val userTable = TableQuery[UserTable]

  def uuid: Rep[UUID] = column[UUID]("uuid", O.PrimaryKey)

  def menuPerDayUuid: Rep[UUID] = column[UUID]("menuPerDayUuid")

  def userUuid: Rep[UUID] = column[UUID]("userUuid")

  def menuPerDayPerPersonMenuPerDayForeignKey: ForeignKeyQuery[MenuPerDayTable, MenuPerDay] =
    foreignKey("menuPerDayPerPersonMenuPerDay_fkey_", menuPerDayUuid, menuPerDayTable)(_.uuid)

  def menuPerDayPerPersonUserForeignKey: ForeignKeyQuery[UserTable, User] =
    foreignKey("menuPerDayPerPersonUser_fkey_", userUuid, userTable)(_.uuid)

  def * : ProvenShape[MenuPerDayPerPerson] = (uuid, menuPerDayUuid, userUuid) <> ((MenuPerDayPerPerson.apply _).tupled, MenuPerDayPerPerson.unapply)
}

object MenuPerDayPerPersonTable {
  val menuPerDayPerPersonTable: TableQuery[MenuPerDayPerPersonTable] = TableQuery[MenuPerDayPerPersonTable]

  def add(menuPerDayPerPerson: MenuPerDayPerPerson)(implicit connection: DBConnection): Future[MenuPerDayPerPerson] = {
    val query = menuPerDayPerPersonTable returning menuPerDayPerPersonTable += menuPerDayPerPerson
    connection.db.run(query)
  }

  def exists(uuid: UUID)(implicit connection: DBConnection): Future[Boolean] = {
    connection.db.run(menuPerDayPerPersonTable.filter(_.uuid === uuid).exists.result)
  }

  def getByUUID(uuid: UUID)(implicit connection: DBConnection): Future[Option[MenuPerDayPerPerson]] = {
    exists(uuid).flatMap {
      case true =>
        val query = menuPerDayPerPersonTable.filter(x => x.uuid === uuid)
        connection.db.run(query.result.headOption)
      case false => Future(None)
    }
  }

  def getByMenuPerDayUuid(menuPerDayUuid: UUID)(implicit connection: DBConnection): Future[Seq[MenuPerDayPerPerson]] = {
    val query = menuPerDayPerPersonTable.filter(_.menuPerDayUuid === menuPerDayUuid)
    connection.db.run(query.result)
  }

  def getUsersByMenuPerDayUuid(menuPerDayUuid: UUID)(implicit connection: DBConnection): Future[Seq[User]] = {
    val query = for {
      mpdpp <- menuPerDayPerPersonTable.filter(_.menuPerDayUuid === menuPerDayUuid)
      user <- UserTable.userTable if mpdpp.userUuid === user.uuid
    } yield user

    connection.db.run(query.result)
  }

  def getByUserUuid(userUuid: UUID)(implicit connection: DBConnection): Future[Seq[MenuPerDayPerPerson]] = {
    val query = menuPerDayPerPersonTable.filter(_.userUuid === userUuid)
    connection.db.run(query.result)
  }

  def getByUserUuidAndMenuPerDayUuid(userUuid: UUID, menuPerDayUuid: UUID)(implicit connection: DBConnection): Future[Option[MenuPerDayPerPerson]] = {
    val query = menuPerDayPerPersonTable.filter(_.userUuid === userUuid).filter(_.menuPerDayUuid === menuPerDayUuid)
    connection.db.run(query.result.headOption)
  }

  def getAll(implicit connection: DBConnection): Future[Seq[MenuPerDayPerPerson]] = {
    connection.db.run(menuPerDayPerPersonTable.result)
  }

  def remove(uuid: UUID)(implicit connection: DBConnection): Future[Int]  = {
      val query = menuPerDayPerPersonTable.filter(x => x.uuid === uuid).delete
      connection.db.run(query)
  }

  def removeByMenuPerDayUuid(menuPerDayUuid: UUID)(implicit connection: DBConnection): Future[Int] = {
    val query = menuPerDayPerPersonTable.filter(x => x.menuPerDayUuid === menuPerDayUuid).delete
    connection.db.run(query)
  }

}

