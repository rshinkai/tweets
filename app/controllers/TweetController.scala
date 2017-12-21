package controllers

import java.time.ZonedDateTime
import javax.inject.{ Inject, Singleton }

import jp.t2v.lab.play2.auth.AuthenticationElement
import jp.t2v.lab.play2.pager.Pager
import models.Tweet
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc._
import services._

@Singleton
class TweetController @Inject()(
    val userService: UserService,
    val tweetService: TweetService,
    val userFavoriteService: UserFavoriteService,
    val messagesApi: MessagesApi
) extends Controller
    with I18nSupport
    with AuthConfigSupport
    with AuthenticationElement {

  private val postForm = Form {
    "content" -> nonEmptyText
  }

  def post(pager: Pager[Tweet]): Action[AnyContent] = StackAction { implicit request =>
    val user = loggedIn
    postForm
      .bindFromRequest()
      .fold(
        { formWithErrors =>
          handleError(pager, user, formWithErrors)
        }, { content =>
          createMicroPost(pager, user, content)
        }
      )
  }

  private def createMicroPost(pager: Pager[Tweet], user: User, content: String) = {
    val now       = ZonedDateTime.now
    val microPost = Tweet(None, user.id.get, content, now, now)
    tweetService
      .create(microPost)
      .map { _ =>
        Redirect(routes.HomeController.index(pager))
      }
      .recover {
        case e: Exception =>
          Logger.error("occurred error", e)
          Redirect(routes.HomeController.index(Pager.default))
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  private def handleError(
      pager: Pager[Tweet],
      user: User,
      formWithErrors: Form[String]
  )(implicit request: RequestHeader) = {
    tweetService
      .findAllByWithLimitOffset(pager, user.id.get)
      .map { searchResult =>
        val userFavorite = userFavoriteService.findByUserId(user.id.get).get
        BadRequest(views.html.index(Some(user), formWithErrors, searchResult, userFavorite))
      }
      .recover {
        case e: Exception =>
          Logger.error("occurred error", e)
          Redirect(routes.HomeController.index(Pager.default))
            .flashing("failure" -> Messages("InternalError"))
      }
      .getOrElse(InternalServerError(Messages("InternalError")))
  }

  def delete(microPostId: Long, pager: Pager[Tweet]): Action[AnyContent] = StackAction { implicit request =>
    tweetService
      .deleteById(microPostId)
      .map { _ =>
        Redirect(routes.HomeController.index(pager))
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
