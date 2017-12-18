package services

import jp.t2v.lab.play2.pager.{ Pager, SearchResult }
import models.User
import scalikejdbc.{ AutoSession, DBSession }

import scala.util.Try

trait UserService {

  def create(user: User)(implicit dBSession: DBSession = AutoSession): Try[Long]

  def findByEmail(email: String)(implicit dBSession: DBSession = AutoSession): Try[Option[User]]

  def findAll(pager: Pager[User])(implicit dBSession: DBSession = AutoSession): Try[SearchResult[User]]

  def findById(id: Long)(implicit dBSession: DBSession = AutoSession): Try[Option[User]]
}
