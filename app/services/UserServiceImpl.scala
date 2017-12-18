package services

import javax.inject.Singleton

import jp.t2v.lab.play2.pager.{ Pager, SearchResult }
import jp.t2v.lab.play2.pager.scalikejdbc._
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

  override def findAll(pager: Pager[User])(implicit dbSession: DBSession): Try[SearchResult[User]] = Try {
    // 総件数を取得する
    val size = User.countAllModels()
    // SearchResultを生成する
    SearchResult(pager, size) { pager =>
      // Pagerに基づいて結果を返す
      User.findAllWithLimitOffset(
        pager.limit,
        pager.offset,
        pager.allSorters.map(_.toSQLSyntax(User.defaultAlias))
      )
    }
  }

  override def findById(id: Long)(implicit dBSession: DBSession): Try[Option[User]] = Try {
    User.findById(id)
  }
}
