package services

import javax.inject.Singleton

import jp.t2v.lab.play2.pager.{ OrderType, Pager, SearchResult, Sorter }
import models.{ User, UserFollow }
import scalikejdbc._

import scala.util.Try

@Singleton
class UserFollowServiceImpl extends UserFollowService {

  // 追加
  implicit def sortersToSQLSyntaxs(sorters: Seq[Sorter[User]]): Seq[SQLSyntax] = {
    sorters.map { sorter =>
      if (sorter.dir == OrderType.Descending)
        UserFollow.defaultAlias.id.desc
      else
        UserFollow.defaultAlias.id.asc
    }
  }

  override def create(userFollow: UserFollow)(implicit dbSession: DBSession): Try[Long] = Try {
    UserFollow.create(userFollow)
  }

  override def findById(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[List[UserFollow]] = Try {
    UserFollow.where('userId -> userId).apply()
  }

  override def findByFollowId(followId: Long)(implicit dbSession: DBSession = AutoSession): Try[Option[UserFollow]] =
    Try {
      UserFollow.where('followId -> followId).apply().headOption
    }

  // userIdのユーザーをフォローするユーザーの集合を取得する
  override def findFollowersByUserId(pager: Pager[User], userId: Long)(
      implicit dbSession: DBSession = AutoSession
  ): Try[SearchResult[User]] = {
    countByFollowId(userId).map { size =>
      SearchResult(pager, size) { pager =>
        UserFollow.allAssociations
          .findAllByWithLimitOffset(
            sqls.eq(UserFollow.defaultAlias.followId, userId),
            pager.limit,
            pager.offset,
            pager.allSorters
          )
          .map(_.user.get)
      }
    }
  }

  override def countByFollowId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long] = Try {
    UserFollow.allAssociations.countBy(sqls.eq(UserFollow.defaultAlias.followId, userId))
  }

  // userIdのユーザーがフォローしているユーザーの集合を取得する
  override def findFollowingsByUserId(pager: Pager[User], userId: Long)(
      implicit dbSession: DBSession = AutoSession
  ): Try[SearchResult[User]] = {
    // 全体の母数を取得する
    countByUserId(userId).map { size =>
      // SearchResultの生成
      SearchResult(pager, size) { pager =>
        UserFollow.allAssociations
          .findAllByWithLimitOffset(
            sqls.eq(UserFollow.defaultAlias.userId, userId),
            pager.limit,
            pager.offset,
            pager.allSorters
          )
          .map(_.followUser.get)
      }
    }
  }

  override def countByUserId(userId: Long)(implicit dbSession: DBSession = AutoSession): Try[Long] = Try {
    UserFollow.allAssociations.countBy(sqls.eq(UserFollow.defaultAlias.userId, userId))
  }

  override def deleteBy(userId: Long, followId: Long)(implicit dbSession: DBSession = AutoSession): Try[Int] = Try {
    val c     = UserFollow.column
    val count = UserFollow.countBy(sqls.eq(c.userId, userId).and.eq(c.followId, followId))
    if (count == 1) {
      UserFollow.deleteBy(
        sqls
          .eq(UserFollow.column.userId, userId)
          .and(sqls.eq(UserFollow.column.followId, followId))
      )
    } else 0
  }

}
