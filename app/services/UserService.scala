package services

import models.User
import scalikejdbc.{ AutoSession, DBSession }

import scala.util.Try

trait UserService {

  def create(user: User)(implicit dBSession: DBSession = AutoSession): Try[Long]

  def findByEmail(email: String)(implicit dBSession: DBSession = AutoSession): Try[Option[User]]
}
