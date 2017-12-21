package models

import java.time.ZonedDateTime

import scalikejdbc._, jsr310._
import skinny.orm._
import skinny.orm.feature._

case class UserFavorite(id: Option[Long],
                        userId: Long,
                        tweetId: Long,
                        createAt: ZonedDateTime = ZonedDateTime.now(),
                        updateAt: ZonedDateTime = ZonedDateTime.now(),
                        user: Option[User] = None,
                        tweet: Option[Tweet] = None)

object UserFavorite extends SkinnyCRUDMapper[UserFavorite] {

  lazy val u = User.createAlias("u")

  lazy val userRef = belongsToWithAliasAndFkAndJoinCondition[User](
    right = User -> u,
    fk = "userId",
    on = sqls.eq(defaultAlias.userId, u.id),
    merge = (uf, f) => uf.copy(user = f)
  )

  lazy val t = Tweet.createAlias("t")

  lazy val tweetRef = belongsToWithAliasAndFkAndJoinCondition[Tweet](
    right = Tweet -> t,
    fk = "tweetId",
    on = sqls.eq(defaultAlias.tweetId, t.id),
    merge = (uf, f) => uf.copy(tweet = f)
  )

  lazy val allAssociations: CRUDFeatureWithId[Long, UserFavorite] = joins(userRef, tweetRef)

  override def tableName = "user_favorites"

  override def defaultAlias: Alias[UserFavorite] = createAlias("uf")

  override def extract(rs: WrappedResultSet, n: ResultName[UserFavorite]): UserFavorite =
    autoConstruct(rs, n, "user", "tweet")

  def create(userFavorite: UserFavorite)(implicit session: DBSession): Long =
    createWithAttributes(toNamedValues(userFavorite): _*)

  private def toNamedValues(record: UserFavorite): Seq[(Symbol, Any)] = Seq(
    'userId   -> record.userId,
    'tweetId  -> record.tweetId,
    'createAt -> record.createAt,
    'updateAt -> record.updateAt
  )

  def update(userFavorite: UserFavorite)(implicit session: DBSession): Int =
    updateById(userFavorite.id.get).withAttributes(toNamedValues(userFavorite): _*)
}
