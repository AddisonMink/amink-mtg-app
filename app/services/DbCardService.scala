package services

import cats.data.EitherT
import com.google.inject.ImplementedBy
import dao.{CardDao, UserDao}
import javax.inject.Inject
import models.{ApiCard, DbCard}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DbCardServiceImpl])
trait DbCardService {

  def getUserCards(id: Long): Future[Option[Seq[DbCard]]]

  def giveUserBoosters(userId: Long, setId: String, num: Int = 1): Future[Either[String,Seq[ApiCard]]]
}

class DbCardServiceImpl @Inject()(
  apiCardService: ApiCardService,
  cardDao: CardDao,
  userDao: UserDao)
  (implicit ec: ExecutionContext) extends DbCardService {

  override def getUserCards(id: Long): Future[Option[Seq[DbCard]]] = cardDao.getPlayerCards(id)

  override def giveUserBoosters(userId: Long, setId: String, num: Int): Future[Either[String,Seq[ApiCard]]] = {
    val eitherT: EitherT[Future, String, Seq[ApiCard]] = for {
      set <- EitherT.fromOptionF(apiCardService.fetchSet(setId), s"Could not fetch set with id ${setId}.")
      cards = BoosterService.assembleBoosters(set,num)
      user <- EitherT.fromOptionF(userDao.incrementBatch(userId), s"No user with id ${userId} exists.")
      batch = user.batch
      _ <- EitherT.liftF(cardDao.insertCards(userId,batch,cards))
    } yield cards

    eitherT.value
  }
}
