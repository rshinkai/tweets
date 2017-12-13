package services

import javax.inject.Singleton

import models.User
import scalikejdbc.DBSession

import scala.util.Try

@Singleton
class UserServiceImpl extends UserService {

  // ユーザーの作成に成功した場合は、Success(AUTO_INCREMENTによるID値を返します)
  override def create(user: User)(implicit dBSession: DBSession): Try[Long] = Try {
    User.create(user)
  }

  // 先頭要素をOptionに入れて返す。要素が無い場合はNoneが返る。
  override def findByEmail(email: String)(implicit dBSession: DBSession): Try[Option[User]] = Try {
    User.where('email -> email).apply().headOption
  }
}
