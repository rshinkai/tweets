package services

import javax.inject.Singleton

import jp.t2v.lab.play2.pager.scalikejdbc._
import jp.t2v.lab.play2.pager.{ Pager, SearchResult }
import models.Tweet
import scalikejdbc._

import scala.util.Try

@Singleton
class TweetServiceImpl extends TweetService {

  override def create(tweet: Tweet)(implicit dBSession: DBSession): Try[Long] = Try {
    Tweet.create(tweet)
  }

  override def deleteById(tweetId: Long)(implicit dBSession: DBSession): Try[Int] = Try {
    Tweet.deleteById(tweetId)
  }

  override def findByUserId(pager: Pager[Tweet], userId: Long)(
      implicit dbSession: DBSession
  ): Try[SearchResult[Tweet]] =
    countBy(userId).map { size =>
      SearchResult(pager, size)(findAllByWithLimitOffset(userId))
    }

  override def countBy(userId: Long)(implicit dbSession: DBSession): Try[Long] = Try {
    Tweet.countBy(sqls.eq(Tweet.defaultAlias.userId, userId))
  }

  override def findAllByWithLimitOffset(pager: Pager[Tweet], userId: Long)(
      implicit dbSession: DBSession
  ): Try[SearchResult[Tweet]] = Try {
    val size = Tweet.countBy(sqls.eq(Tweet.defaultAlias.userId, userId))
    SearchResult(pager, size)(findAllByWithLimitOffset(userId))
  }

  private def findAllByWithLimitOffset(userId: Long)(pager: Pager[Tweet])(
      implicit dbSession: DBSession
  ): Seq[Tweet] = Tweet.findAllByWithLimitOffset(
    sqls.eq(Tweet.defaultAlias.userId, userId),
    pager.limit,
    pager.offset,
    pager.allSorters.map(_.toSQLSyntax(Tweet.defaultAlias))
  )
}
