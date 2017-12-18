package controllers

import java.time.ZonedDateTime
import javax.inject.{ Inject, Singleton }

import com.github.t3hnar.bcrypt._
import forms.SignUp
import jp.t2v.lab.play2.pager.Pager
import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc._
import play.api.{ Configuration, Logger }
import services.UserService

@Singleton
class SignUpController @Inject()(userService: UserService, val messagesApi: MessagesApi, config: Configuration)
    extends Controller
    with I18nSupport {

  private val salt = config.getString("password.salt").get

  private val signUpForm: Form[SignUp] = Form {
    mapping(
      "name"     -> nonEmptyText,
      "email"    -> email,
      "password" -> nonEmptyText,
      "confirm"  -> nonEmptyText
    )(SignUp.apply)(SignUp.unapply)
      .verifying(Messages("PasswordInvalid"), form => form.password == form.confirm)
  }

  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.signup(signUpForm))
  }

  def register: Action[AnyContent] = Action { implicit request =>
    signUpForm
    // Requestオブジェクトからフォームに割り当てるためのメソッド
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest(views.html.signup(formWithErrors)), { signUp =>
          val now            = ZonedDateTime.now()
          val hashedPassword = signUp.password.bcrypt(salt)
          val user           = User(None, signUp.name, signUp.email, hashedPassword, now, now)
          userService
          // UserService#createはTry[Long]を返します。成功した場合はSuccessとなり、失敗した場合はFailure
            .create(user)
            // mapによって成功メッセージを含むFlashスコープを持つリダイレクトレスポンスに変換
            .map { _ =>
              Redirect(routes.HomeController.index(Pager.default))
                .flashing("success" -> Messages("SignUpScceeded"))
            }
            .recover {
              case e: Exception =>
                Logger.error(s"occurred error", e)
                Redirect(routes.HomeController.index(Pager.default))
                  .flashing("failure" -> Messages("InternalError"))
            }
            .getOrElse(InternalServerError(Messages("InternalError")))
        }
      )
  }

}
