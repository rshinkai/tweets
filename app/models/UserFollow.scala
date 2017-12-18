package models

import java.time.ZonedDateTime

import scalikejdbc._, jsr310._
import skinny.orm._
import skinny.orm.feature._

case class UserFollow(id: Option[Long],
                      userId: Long,
                      followId: Long,
                      createAt: ZonedDateTime = ZonedDateTime.now(),
                      updateAt: ZonedDateTime = ZonedDateTime.now(),
                      user: Option[User] = None,
                      followUser: Option[User] = None)

object UserFollow extends SkinnyCRUDMapper[UserFollow] {

  lazy val u1 = User.createAlias("u1")

  lazy val userRef = belongsToWithAliasAndFkAndJoinCondition[User](
    right = User -> u1,
    fk = "userId",
    on = sqls.eq(defaultAlias.userId, u1.id),
    merge = (uf, f) => uf.copy(user = f)
  )

  lazy val u2 = User.createAlias("u2")

  lazy val followRef = belongsToWithAliasAndFkAndJoinCondition[User](
    right = User -> u2,
    fk = "followId",
    on = sqls.eq(defaultAlias.followId, u2.id),
    merge = (uf, f) => uf.copy(followUser = f)
  )

  lazy val allAssociations: CRUDFeatureWithId[Long, UserFollow] = joins(userRef, followRef)

  override def tableName = "user_follows"

  override def defaultAlias: Alias[UserFollow] = createAlias("uf")

  override def extract(rs: WrappedResultSet, n: ResultName[UserFollow]): UserFollow =
    autoConstruct(rs, n, "user", "followUser")

  def create(userFollow: UserFollow)(implicit session: DBSession): Long =
    createWithAttributes(toNamedValues(userFollow): _*)

  private def toNamedValues(record: UserFollow): Seq[(Symbol, Any)] = Seq(
    'userId   -> record.userId,
    'followId -> record.followId,
    'createAt -> record.createAt,
    'updateAt -> record.updateAt
  )

  def update(userFollow: UserFollow)(implicit session: DBSession): Int =
    updateById(userFollow.id.get).withAttributes(toNamedValues(userFollow): _*)

}