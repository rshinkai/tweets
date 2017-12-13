package services
import models.User
import scalikejdbc.DBSession

import scala.util.{ Success, Try }

class MockUserService extends UserService {
  override def create(user: User)(implicit dBSession: DBSession): Try[Long] = Success(1L)

  override def findByEmail(email: String)(implicit dBSession: DBSession): Try[Option[User]] = Success(
    Some(User(Some(1L), email, email, "xxx"))
  )
}
