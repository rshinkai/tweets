import com.google.inject.AbstractModule
import services._

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
    bind(classOf[TweetService]).to(classOf[TweetServiceImpl])
    bind(classOf[UserFollowService]).to(classOf[UserFollowServiceImpl]) // 追加
  }
}
