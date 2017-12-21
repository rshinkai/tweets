package services

import javax.inject.Singleton

import models.{ UserFavorite }
import scalikejdbc._

import scala.util.Try

@Singleton
class UserFavoriteServiceImpl extends UserFavoriteService {

  override def create(userFavorite: UserFavorite)(implicit dbSession: DBSession): Try[Long] = Try {
    UserFavorite.create(userFavorite)
  }

  override def findByUserId(userId: Long)(implicit dbSession: DBSession): Try[List[UserFavorite]] = Try {
    UserFavorite.where('userId -> userId).apply()
  }

  override def findByTweetId(tweetId: Long)(implicit dbSession: DBSession): Try[Option[UserFavorite]] = Try {
    UserFavorite.where('tweetId -> tweetId).apply().headOption
  }

  override def countByUserId(userId: Long)(implicit dbSession: DBSession): Try[Long] = Try {
    UserFavorite.allAssociations.countBy(sqls.eq(UserFavorite.defaultAlias.userId, userId))
  }

  override def deleteBy(userId: Long, tweetId: Long)(implicit dbSession: DBSession): Try[Int] = Try {
    val c     = UserFavorite.column
    val count = UserFavorite.countBy(sqls.eq(c.userId, userId).and.eq(c.tweetId, tweetId))
    if (count == 1) {
      UserFavorite.deleteBy(
        sqls
          .eq(UserFavorite.column.userId, userId)
          .and(sqls.eq(UserFavorite.column.tweetId, tweetId))
      )
    } else 0
  }

}
