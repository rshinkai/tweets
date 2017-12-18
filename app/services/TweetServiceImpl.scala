package services

import javax.inject.Singleton

import jp.t2v.lab.play2.pager.scalikejdbc._
import jp.t2v.lab.play2.pager.{ Pager, SearchResult }
import models.{ Tweet, UserFollow }
import scalikejdbc._

import scala.util.Try

@Singleton
class TweetServiceImpl extends TweetService {

  override def create(tweet: Tweet)(implicit dbSession: DBSession): Try[Long] = Try {
    Tweet.create(tweet)
  }

  override def findById(id: Long)(implicit dbSession: DBSession): Try[Tweet] = Try {
    Tweet.findById(id).get
  }

  override def deleteById(tweetId: Long)(implicit dbSession: DBSession): Try[Int] = Try {
    Tweet.deleteById(tweetId)
  }

  override def findByUserId(pager: Pager[Tweet], userId: Long)(
      implicit dbSession: DBSession
  ): Try[SearchResult[Tweet]] =
    countBy(userId).map { size =>
      SearchResult(pager, size)(findAllByWithLimitOffset(Seq(userId)))
    }

  override def countBy(userId: Long)(implicit dbSession: DBSession): Try[Long] = Try {
    Tweet.countBy(sqls.eq(Tweet.defaultAlias.userId, userId))
  }

  override def findAllByWithLimitOffset(pager: Pager[Tweet], userId: Long)(
      implicit dbSession: DBSession
  ): Try[SearchResult[Tweet]] = Try {
    val followingIds =
      UserFollow.findAllBy(sqls.eq(UserFollow.defaultAlias.userId, userId)).map(_.followId)
    val size = Tweet.countBy(sqls.in(Tweet.defaultAlias.userId, userId +: followingIds))
    SearchResult(pager, size)(findAllByWithLimitOffset(userId +: followingIds))
  }

  private def findAllByWithLimitOffset(userIds: Seq[Long])(pager: Pager[Tweet])(
      implicit dbSession: DBSession
  ): Seq[Tweet] = Tweet.findAllByWithLimitOffset(
    sqls.in(Tweet.defaultAlias.userId, userIds),
    pager.limit,
    pager.offset,
    pager.allSorters.map(_.toSQLSyntax(Tweet.defaultAlias))
  )
}
