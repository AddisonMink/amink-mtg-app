package mock

import dao.UserDao
import models.User

import scala.concurrent.Future

object MockUserDao extends UserDao {

  val user1 = User(1,"Addison")
  val user2 = User(2,"Carter")
  private val users = Map(1 -> user1, 2 -> user2)

  override def getUsers: Future[Seq[User]] = {
    Future.successful(users.values.toSeq)
  }

  override def getUser(id: Long): Future[Option[User]] = {
    Future.successful(users.get(id.toInt))
  }
}
