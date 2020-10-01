package dao

import javax.inject.Inject
import model.User
import play.api.db.{DBApi, Database}

import scala.concurrent.Future

trait UserDao {

  def getUsers: Future[Seq[User]]

  def getUser(id: Long): Future[Option[User]]

}