package controllers

import javax.inject._

import jp.t2v.lab.play2.auth.AuthenticationElement
import play.api.Logger
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc._
import services.UserService

@Singleton
class UsersController @Inject()(val userService: UserService, val messagesApi: MessagesApi)
    extends Controller
    with I18nSupport
    with AuthConfigSupport
    with AuthenticationElement {

  def index: Action[AnyContent] = StackAction { implicit request =>
    userService.findAll
      .map { users =>
        Ok(views.html.users.index(loggedIn, users))
      }
      .recover {
        case e: Exception =>
          Logger.error(s"occurred error", e)
          Redirect(routes.UsersController.index())
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  def show(userId: Long) = StackAction { implicit request =>
    userService
      .findById(userId)
      .map { userOpt =>
        userOpt.map { user =>
          Ok(views.html.users.show(loggedIn, user))
        }.get
      }
      .recover {
        case e: Exception =>
          Logger.error(s"occurred error", e)
          Redirect(routes.UsersController.index())
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

}
