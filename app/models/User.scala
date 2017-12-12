package models

import java.time.ZonedDateTime

import scalikejdbc._, jsr310._
import skinny.orm.{ Alias, SkinnyCRUDMapper }

case class User(id: Option[Long] = None,
                name: String,
                email: String,
                password: String,
                createAt: ZonedDateTime = ZonedDateTime.now(),
                updateAt: ZonedDateTime = ZonedDateTime.now())

object User extends SkinnyCRUDMapper[User] {

  override def tableName = "users"

  override def defaultAlias: Alias[User] = createAlias("u")

  private def toNamedValue(record: User): Seq[(Symbol, Any)] = Seq(
    'name     -> record.name,
    'email    -> record.email,
    'password -> record.password,
    'createAt -> record.createAt,
    'updateAt -> record.updateAt
  )

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[User]): User =
    autoConstruct(rs, n)

  def create(user: User)(implicit session: DBSession): Long =
    createWithAttributes(toNamedValue(user): _*)

  def update(user: User)(implicit session: DBSession): Int =
    updateById(user.id.get).withAttributes(toNamedValue(user): _*)

}
