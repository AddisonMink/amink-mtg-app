package mock

import dao.UserDao
import models.User

import scala.concurrent.Future

class MockUserDao extends UserDao {

  val user1 = User(1,"Addison", batch = 1)
  val user2 = User(2,"Carter", batch = 1)
  private var users = Map(1 -> user1, 2 -> user2)

  override def getUsers: Future[Seq[User]] = {
    Future.successful(users.values.toSeq)
  }

  override def getUser(id: Long): Future[Option[User]] = {
    Future.successful(users.get(id.toInt))
  }

  override def addUser(name: String): Future[User] = {
    val id = users.keys.max + 1
    val user = User(id,name, batch = 1)
    users = users + (id -> user)
    Future.successful(user)
  }

  override def removeUser(id: Long): Future[Boolean] = {
    if(users.contains(id.toInt)) {
      users = users - id.toInt
      Future.successful(true)
    } else {
      Future.successful(false)
    }
  }

  override def incrementBatch(id: Long): Future[Option[User]] = {
    if(users.contains(id.toInt)) {
      val user = users(id.toInt)
      val newUser = user.copy(batch = user.batch + 1)
      users = users + (id.toInt -> newUser)
      Future.successful(Some(newUser))
    } else {
      Future.successful(None)
    }
  }
}
