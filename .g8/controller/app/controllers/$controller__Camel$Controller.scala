package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.i18n._

/**
 * $controller;format="Camel"$Controller
 */
@Singleton
class $controller;format="Camel"$Controller @Inject()(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action {
    Ok(views.html.$controller;format="camel"$.index)
  }

}
