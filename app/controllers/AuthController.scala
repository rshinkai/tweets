package controllers

import javax.inject.{ Inject, Singleton }

import com.github.t3hnar.bcrypt._
import forms.Login
import jp.t2v.lab.play2.auth.LoginLogout
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import services.UserService

import scala.concurrent.Future

@Singleton
class AuthController @Inject()(val userService: UserService, val messagesApi: MessagesApi)
    extends Controller
    with I18nSupport
    with AuthConfigSupport
    with LoginLogout {

  private val loginForm: Form[Login] = Form {
    mapping(
      "email"    -> email,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply)
      .verifying(Messages("AuthFailed"), form => authenticate(form.email, form.password).isDefined)
  }

  private val remembermeForm: Form[Boolean] = Form {
    "rememberme" -> boolean
  }

  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.auth.login(loginForm, remembermeForm.fill(request.session.get("rememberme").exists("true" ==))))
  }

  def login: Action[AnyContent] = {
    Action.async { implicit request =>
      val rememberMe = remembermeForm.bindFromRequest()
      loginForm.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest(views.html.auth.login(formWithErrors, rememberMe))), { login =>
          val req = request.copy(tags = request.tags + ("rememberme" -> rememberMe.get.toString))
          gotoLoginSucceeded(login.email)(req, defaultContext)
            .map(_.withSession("rememberme" -> rememberMe.get.toString))
            .map(_.flashing("sccess" -> Messages("LoggedIn")))
        }
      )
    }
  }

  def logout: Action[AnyContent] = Action.async { implicit request =>
    gotoLogoutSucceeded.map(
      _.flashing("success" -> Messages("LoggedOut"))
        .removingFromSession("rememberme")
    )
  }

  private def authenticate(email: String, password: String): Option[User] = {
    userService
      .findByEmail(email)
      .map { user =>
        user.flatMap { u =>
          if (password.isBcrypted(u.password))
            user
          else
            None
        }
      }
      .get
  }

}
