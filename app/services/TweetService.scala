package services

import jp.t2v.lab.play2.pager.{ Pager, SearchResult }
import models.Tweet
import scalikejdbc.{ AutoSession, DBSession }

import scala.util.Try

trait TweetService {

  def create(tweet: Tweet)(implicit dBSession: DBSession = AutoSession): Try[Long]

  def deleteById(tweetId: Long)(implicit dBSession: DBSession = AutoSession): Try[Int]

  def findByUserId(pager: Pager[Tweet], userId: Long)(
      implicit dBSession: DBSession = AutoSession
  ): Try[SearchResult[Tweet]]

  def countBy(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def findAllByWithLimitOffset(pager: Pager[Tweet], userId: Long)(
      implicit dbSession: DBSession = AutoSession
  ): Try[SearchResult[Tweet]]
}
