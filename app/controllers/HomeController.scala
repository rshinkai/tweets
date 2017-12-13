package controllers

import javax.inject._

import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc._
import services.UserService

@Singleton
class HomeController @Inject()(val userService: UserService, val messagesApi: MessagesApi)
    extends Controller
    with I18nSupport
    with AuthConfigSupport
    with OptionalAuthElement {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index: Action[AnyContent] = StackAction { implicit request =>
    Ok(views.html.index(loggedIn))
  }

}
