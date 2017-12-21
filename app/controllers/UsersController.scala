package controllers

import javax.inject._

import jp.t2v.lab.play2.auth.AuthenticationElement
import jp.t2v.lab.play2.pager.{ Pager, Sortable }
import models.{ Tweet, User }
import play.api.Logger
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc._
import services._

@Singleton
class UsersController @Inject()(val userService: UserService,
                                val tweetService: TweetService,
                                val userFollowService: UserFollowService,
                                val userFavoriteService: UserFavoriteService,
                                val messagesApi: MessagesApi)
    extends Controller
    with I18nSupport
    with AuthConfigSupport
    with AuthenticationElement {

  // 変更: 引数を変更
  def index(pager: Pager[models.User]): Action[AnyContent] = StackAction { implicit request =>
    userService
      .findAll(pager) // pagerを渡す
      .map { users =>
        Ok(views.html.users.index(loggedIn, users)) // SearchResultをビューに渡す
      }
      .recover {
        case e: Exception =>
          Logger.error(s"occurred error", e)
          Redirect(routes.UsersController.index(Pager.default))
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  def show(userId: Long, page: Int) = StackAction { implicit request =>
    val triedUserOpt        = userService.findById(userId)
    val triedUserFollows    = userFollowService.findById(loggedIn.id.get)
    val pager               = createPager[Tweet](page)
    val triedMicroPosts     = tweetService.findByUserId(pager, userId)
    val triedFollowingsSize = userFollowService.countByUserId(userId)
    val triedFollowersSize  = userFollowService.countByFollowId(userId)
    val triedFavorites      = userFavoriteService.findByUserId(userId)
    (for {
      userOpt        <- triedUserOpt
      userFollows    <- triedUserFollows
      microPosts     <- triedMicroPosts
      followingsSize <- triedFollowingsSize
      followersSize  <- triedFollowersSize
      userFavorites  <- triedFavorites
    } yield {
      userOpt.map { user =>
        Ok(
          views.html.users.show(loggedIn, user, userFollows, microPosts, followingsSize, followersSize, userFavorites)
        )
      }.get
    }).recover {
        case e: Exception =>
          Logger.error(s"occurred error", e)
          Redirect(routes.UsersController.index(Pager.default))
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  def getFollowers(userId: Long, page: Int) = StackAction { implicit request =>
    val targetUser           = User.findById(userId).get
    val triedMaybeUserFollow = userFollowService.findById(loggedIn.id.get)
    val pager                = createPager[models.User](page)
    val triedFollowers       = userFollowService.findFollowersByUserId(pager, userId)
    val triedMicroPostsSize  = tweetService.countBy(userId)
    val triedFollowingsSize  = userFollowService.countByUserId(userId)
    (for {
      userFollows    <- triedMaybeUserFollow
      followers      <- triedFollowers
      microPostSize  <- triedMicroPostsSize
      followingsSize <- triedFollowingsSize
    } yield {
      Ok(
        views.html.users.followers(
          loggedIn,
          targetUser,
          userFollows,
          followers,
          microPostSize,
          followingsSize
        )
      )
    }).recover {
        case e: Exception =>
          Logger.error("occurred error", e)
          Redirect(routes.UsersController.index(Pager.default))
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  def getFollowings(userId: Long, page: Int) = StackAction { implicit request =>
    val targetUser          = User.findById(userId).get
    val triedUserFollows    = userFollowService.findById(loggedIn.id.get)
    val pager               = createPager[models.User](page)
    val triedFollowings     = userFollowService.findFollowingsByUserId(pager, userId)
    val triedMicroPostsSize = tweetService.countBy(userId)
    val triedFollowersSize  = userFollowService.countByFollowId(userId)
    (for {
      userFollows    <- triedUserFollows
      followings     <- triedFollowings
      microPostsSize <- triedMicroPostsSize
      followersSize  <- triedFollowersSize
    } yield {
      Ok(
        views.html.users.followings(
          loggedIn,
          targetUser,
          userFollows,
          followings,
          microPostsSize,
          followersSize
        )
      )
    }).recover {
        case e: Exception =>
          Logger.error("occurred error", e)
          Redirect(routes.UsersController.index(Pager.default))
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  private def createPager[A](page: Int)(implicit sortable: Sortable[A]): Pager[A] =
    Pager(page, sortable.defaultPageSize, sortable.defaultSorter, sortable.optionalDefaultSorters: _*)
}
