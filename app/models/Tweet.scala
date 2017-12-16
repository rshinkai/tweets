package models

import java.time.ZonedDateTime

import jp.t2v.lab.play2.pager.{ OrderType, Sortable }
import scalikejdbc._, jsr310._
import skinny.orm.{ Alias, SkinnyCRUDMapper }

case class Tweet(id: Option[Long],
                 userId: Long,
                 content: String,
                 createAt: ZonedDateTime,
                 updateAt: ZonedDateTime,
                 user: Option[User] = None)

object Tweet extends SkinnyCRUDMapper[Tweet] {

  override def tableName: String = "tweets"

  override def defaultAlias: Alias[Tweet] = createAlias("t")

  belongsTo[User](User, (uf, u) => uf.copy(user = u)).byDefault

  override def extract(rs: WrappedResultSet, n: scalikejdbc.ResultName[Tweet]): Tweet =
    autoConstruct(rs, n, "user")

  def create(tweet: Tweet)(implicit dbSession: DBSession): Long =
    createWithAttributes(toNamedValues(tweet): _*)

  private def toNamedValues(tweet: Tweet): Seq[(Symbol, Any)] = Seq(
    'userId   -> tweet.userId,
    'content  -> tweet.content,
    'createAt -> tweet.createAt,
    'updateAt -> tweet.updateAt
  )

  def update(tweet: Tweet)(implicit dBSession: DBSession): Int =
    updateById(tweet.id.get).withAttributes(toNamedValues(tweet): _*)

  implicit object sortable extends Sortable[Tweet] {
    override def default: (String, OrderType) = ("id", OrderType.Descending)

    override def defaultPageSize: Int = 10

    override def acceptableKeys: Set[String] = Set("id")
  }
}
