package services

import models.UserFavorite
import scalikejdbc.{ AutoSession, DBSession }

import scala.util.Try

trait UserFavoriteService {
  def create(userFavorite: UserFavorite)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def findByUserId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[List[UserFavorite]]

  def findByTweetId(tweetId: Long)(implicit dbSession: DBSession = AutoSession): Try[Option[UserFavorite]]

  def countByUserId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def deleteBy(userId: Long, tweetId: Long)(implicit dbSession: DBSession = AutoSession): Try[Int]
}
