package services
import jp.t2v.lab.play2.pager.{Pager, SearchResult}
import models.User
import scalikejdbc.DBSession

import scala.util.{Success, Try}

class MockUserService extends UserService {
  override def create(user: User)(implicit dBSession: DBSession): Try[Long] = Success(1L)

  override def findByEmail(email: String)(implicit dBSession: DBSession): Try[Option[User]] = Success(
    Some(User(Some(1L), email, email, "xxx"))
  )

  override def findAll(pager: Pager[User])(implicit dbSession: DBSession): Try[SearchResult[User]] =
    Success(SearchResult(pager, 1)(_ => List(User(Some(1L), "test", "test@test.coml", "xxx"))))

  override def findById(id: Long)(implicit dbSession: DBSession): Try[Option[User]] =
    Success(Some(User(Some(1L), "test", "test@test.coml", "xxx")))
}
