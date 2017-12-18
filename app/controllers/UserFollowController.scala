package controllers

import java.time.ZonedDateTime
import javax.inject._

import jp.t2v.lab.play2.auth.AuthenticationElement
import jp.t2v.lab.play2.pager.Pager
import models.UserFollow
import play.api.Logger
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc.{ Action, AnyContent, Controller }
import services.{ UserFollowService, UserService }

@Singleton
class UserFollowController @Inject()(val userFollowService: UserFollowService,
                                     val userService: UserService,
                                     val messagesApi: MessagesApi)
    extends Controller
    with I18nSupport
    with AuthConfigSupport
    with AuthenticationElement {

  def follow(userId: Long): Action[AnyContent] = StackAction { implicit request =>
    val currentUser = loggedIn
    val now         = ZonedDateTime.now()
    val userFollow  = UserFollow(None, currentUser.id.get, userId, now, now)
    userFollowService
      .create(userFollow)
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

  def unFollow(userId: Long): Action[AnyContent] = StackAction { implicit request =>
    val currentUser = loggedIn
    userFollowService
      .deleteBy(currentUser.id.get, userId)
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
