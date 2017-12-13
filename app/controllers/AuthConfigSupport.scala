package controllers

import jp.t2v.lab.play2.auth.AuthConfig
import play.api.mvc.Results._
import play.api.mvc.{ RequestHeader, Result }

import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect._
import services.UserService

trait AuthConfigSupport extends AuthConfig {

  // ユーザーを識別するためのIDの型
  override type Id = String

  // ユーザーを表す型(= Userと指定するとAuthConfigSupport#Userと型がかぶるので、必ずmodels.Userと指定してください)
  override type User = models.User

  // 認可のために使われる型(今回は利用しないのでNothingを指定する)
  override type Authority = Nothing

  // キャッシュAPIからIDを取得する際に利用される
  override implicit val idTag: ClassTag[Id] = classTag[Id]

  // UserService
  val userService: UserService

  // ログインセッションを保存するためのトークンアクセッサ
  override lazy val tokenAccessor = new RememberMeTokenAccessor(sessionTimeoutInSeconds)

  // ログインセッションの有効期限
  override def sessionTimeoutInSeconds: Int = 3600

  // IDからユーザーを解決する
  override def resolveUser(id: String)(implicit context: ExecutionContext): Future[Option[User]] =
    Future {
      userService.findByEmail(id).get
    }

  // ログインに成功した後にリダイレクトする先を返す
  override def loginSucceeded(request: RequestHeader)(implicit context: ExecutionContext): Future[Result] =
    Future.successful(
      Redirect(routes.HomeController.index())
    )

  // ログアウトに成功した後にリダイレクトする先を返す
  override def logoutSucceeded(request: RequestHeader)(implicit context: ExecutionContext): Future[Result] =
    Future.successful {
      Redirect(routes.HomeController.index())
    }

  // 認証に失敗した場合にリダイレクトする先を返す
  override def authenticationFailed(request: RequestHeader)(implicit context: ExecutionContext): Future[Result] =
    Future.successful(
      Redirect(routes.AuthController.index())
    )

  // 認可に失敗した場合のレスポンスを返す(今回は利用しないのでてきとうなレスポンスを返す)
  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Nothing])(
      implicit context: ExecutionContext
  ): Future[Result] = Future.successful(
    Forbidden("no permission")
  )

  // ユーザーの持つ認可を決定するメソッド(今回はロールを区別しないので常にtrueを返す)
  override def authorize(user: User, authority: Nothing)(implicit context: ExecutionContext): Future[Boolean] =
    Future.successful {
      true
    }
}
