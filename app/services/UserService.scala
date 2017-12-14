package services

import models.User
import scalikejdbc.{ AutoSession, DBSession }

import scala.util.Try

trait UserService {

  def create(user: User)(implicit dBSession: DBSession = AutoSession): Try[Long]

  def findByEmail(email: String)(implicit dBSession: DBSession = AutoSession): Try[Option[User]]

  def findAll(implicit dBSession: DBSession = AutoSession): Try[List[User]]

  def findById(id: Long)(implicit dBSession: DBSession = AutoSession): Try[Option[User]]
}
