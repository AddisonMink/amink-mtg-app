package services

import cats.data.EitherT
import com.google.inject.ImplementedBy
import dao.{CardDao, UserDao}
import javax.inject.Inject
import models.User

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def getUsers: Future[Seq[User]]

  def getUser(id: Long): Future[Either[String,User]]
}

class UserServiceImpl @Inject()(userDao: UserDao, cardDao: CardDao)(implicit ec: ExecutionContext) extends UserService {

  override def getUsers: Future[Seq[User]] = userDao.getUsers

  override def getUser(id: Long): Future[Either[String,User]] = {
    val eitherOption = for {
      user <- EitherT.fromOptionF(userDao.getUser(id), s"No user with id $id exists.")
      cards <- EitherT.fromOptionF(cardDao.getPlayerCards(id), s"Could not retrieve cards for user with id $id.")
    } yield user.copy(cards = cards)
    eitherOption.value
  }
}
