package controllers

import java.time.ZonedDateTime
import javax.inject.{ Inject, Singleton }

import jp.t2v.lab.play2.auth.AuthenticationElement
import jp.t2v.lab.play2.pager.{ Pager, Sortable }
import models.{ Tweet, UserFavorite }
import play.api.Logger
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc.{ Action, AnyContent, Controller }
import services.{ TweetService, UserFavoriteService, UserService }

@Singleton
class UserFavoriteController @Inject()(val userFavoriteService: UserFavoriteService,
                                       val userService: UserService,
                                       val tweetService: TweetService,
                                       val messagesApi: MessagesApi)
    extends Controller
    with I18nSupport
    with AuthConfigSupport
    with AuthenticationElement {

  def addFavorite(tweetId: Long): Action[AnyContent] = StackAction { implicit request =>
    val currentUser  = loggedIn
    val now          = ZonedDateTime.now()
    val userFavorite = new UserFavorite(None, currentUser.id.get, tweetId, now, now)
    userFavoriteService
      .create(userFavorite)
      .map { _ =>
        Redirect(routes.HomeController.index(Pager.default))
      }
      .recover {
        case e: Exception =>
          Logger.error("occurred error", e)
          Redirect(routes.HomeController.index(Pager.default))
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  def removeFavorite(tweetId: Long): Action[AnyContent] = StackAction { implicit request =>
    val currentUser = loggedIn
    userFavoriteService
      .deleteBy(currentUser.id.get, tweetId)
      .map { _ =>
        Redirect(routes.HomeController.index(Pager.default))
      }
      .recover {
        case e: Exception =>
          Logger.error("occurred error", e)
          Redirect(routes.HomeController.index(Pager.default))
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

}
