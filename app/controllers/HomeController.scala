package controllers

import javax.inject._

import jp.t2v.lab.play2.auth.OptionalAuthElement
import jp.t2v.lab.play2.pager.{ Pager, SearchResult }
import models.Tweet
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc._
import services.{ TweetService, UserService }

@Singleton
class HomeController @Inject()(val userService: UserService,
                               val tweetService: TweetService,
                               val messagesApi: MessagesApi)
    extends Controller
    with I18nSupport
    with AuthConfigSupport
    with OptionalAuthElement {

  private val tweetForm = Form {
    "content" -> nonEmptyText
  }

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(pager: Pager[Tweet]): Action[AnyContent] = StackAction { implicit request =>
    val userOpt = loggedIn
    userOpt
      .map { user =>
        tweetService
          .findAllByWithLimitOffset(pager, user.id.get)
          .map { searchResult =>
            Ok(views.html.index(userOpt, tweetForm, searchResult))
          }
          .recover {
            case e: Exception =>
              Logger.error(s"occurred error", e)
              Redirect(routes.HomeController.index(Pager.default))
                .flashing("failure" -> Messages("InternalError"))
          }
          .getOrElse(InternalServerError(Messages("InternalError")))
      }
      .getOrElse(Ok(views.html.index(userOpt, tweetForm, SearchResult(pager, 0)(_ => Seq.empty[Tweet]))))
  }

}
