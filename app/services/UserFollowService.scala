package services

import jp.t2v.lab.play2.pager.{ Pager, SearchResult }
import models.{ User, UserFollow }
import scalikejdbc.{ AutoSession, DBSession }

import scala.util.Try

trait UserFollowService {

  def create(userFollow: UserFollow)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def findById(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[List[UserFollow]]

  def findByFollowId(followId: Long)(implicit dbSession: DBSession = AutoSession): Try[Option[UserFollow]]

  def findFollowersByUserId(pager: Pager[User], userId: Long)(
      implicit dbSession: DBSession = AutoSession
  ): Try[SearchResult[User]]

  def findFollowingsByUserId(pager: Pager[User], userId: Long)(
      implicit dbSession: DBSession = AutoSession
  ): Try[SearchResult[User]]

  def countByUserId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def countByFollowId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long]

  def deleteBy(userId: Long, followId: Long)(implicit dbSession: DBSession = AutoSession): Try[Int]

}
