package services

import cats.data.EitherT
import com.google.inject.ImplementedBy
import dao.{CardDao, UserDao}
import javax.inject.Inject
import models.User
import requests.post.PostUserRequest

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserServiceImpl])
trait UserService {

  def getUsers: Future[Seq[User]]

  def getUser(id: Long): Future[Either[String,User]]

  def getUserLatest(id: Long): Future[Either[String,User]]

  def addUser(req: PostUserRequest): Future[User]

  def deleteUser(id: Long): Future[Boolean]
}

class UserServiceImpl @Inject()(userDao: UserDao, dbCardService: DbCardService)(implicit ec: ExecutionContext) extends UserService {

  override def getUsers: Future[Seq[User]] = userDao.getUsers

  override def getUser(id: Long): Future[Either[String,User]] = {
    val futureEither = for {
      user <- EitherT.fromOptionF(userDao.getUser(id), s"No user with id $id exists.")
      cards <- EitherT.fromOptionF(dbCardService.getUserCards(id), s"Could not retrieve cards for user with id $id.")
      sortedCards = cards
        .groupBy(_.colors)
        .values
        .flatMap(_.sortBy(_.name))
        .toSeq
    } yield user.copy(cards = sortedCards)
    futureEither.value
  }

  override def getUserLatest(id: Long): Future[Either[String, User]] = {
    val futureEither = for {
      user <- EitherT.fromOptionF(userDao.getUser(id), s"No user with id $id exists.")
      cards <- EitherT.fromOptionF(dbCardService.getUserCards(id,user.batch), s"Could not retrieve cards for user with id $id.")
      sortedCards = cards
        .groupBy(_.colors)
        .values
        .flatMap(_.sortBy(_.name))
        .toSeq
    } yield user.copy(cards = sortedCards)
    futureEither.value
  }

  override def addUser(req: PostUserRequest): Future[User] = {
    userDao.addUser(req.name)
  }

  override def deleteUser(id: Long): Future[Boolean] = {
    for {
      s1 <- userDao.removeUser(id)
      s2 <- dbCardService.deleteUserCards(id)
    } yield s1 && s2
  }
}
