package mock

import dao.UserDao
import models.User

import scala.concurrent.Future

object MockUserDao extends UserDao {

  val user1 = User(1,"Addison")
  val user2 = User(2,"Carter")
  private var users = Map(1 -> user1, 2 -> user2)

  override def getUsers: Future[Seq[User]] = {
    Future.successful(users.values.toSeq)
  }

  override def getUser(id: Long): Future[Option[User]] = {
    Future.successful(users.get(id.toInt))
  }

  override def addUser(name: String): Future[Long] = {
    val id = users.keys.max + 1
    val user = User(id,name)
    users = users + (id -> user)
    Future.successful(id)
  }

  override def removeUser(id: Long): Future[Boolean] = {
    if(users.contains(id.toInt)) {
      users = users - id.toInt
      Future.successful(true)
    } else {
      Future.successful(false)
    }
  }
}
