package mock

import dao.CardDao
import models.{ApiCard, DbCard}

import scala.concurrent.Future

class MockCardDao(userDao: MockUserDao) extends CardDao {

  var mockCards: Map[Long,Seq[DbCard]] = Map(
    userDao.user1.id -> Seq(DbCard(1,"mock1",Seq(),"",1), DbCard(1,"mock2",Seq(),"",2))
  )

  override def getUserCards(userId: Long): Future[Option[Seq[DbCard]]] = {
    Future.successful(mockCards.get(userId))
  }

  override def getUserCards(userId: Long, batch: Int): Future[Option[Seq[DbCard]]] = {
    Future.successful(mockCards.get(userId).map(_.filter(_.batch == batch)))
  }

  override def insertCards(userId: Long, batch: Int, cards: Seq[ApiCard]): Future[Boolean] = {
    val oldCards = mockCards.getOrElse(userId, Seq())
    val dbCards = cards.map(c => DbCard(userId,c.name,c.colors,c.imageUrl,batch))
    mockCards = mockCards + (userId -> (oldCards ++ dbCards))
    Future.successful(true)
  }

  override def deleteUserCards(userId: Long): Future[Boolean] = {
    mockCards = mockCards.filter {
      case (id, _) if id == userId => false
      case _ => true
    }
    Future.successful(true)
  }
}
