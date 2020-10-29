package mock

import dao.CardDao
import models.{ApiCard, DbCard}

import scala.concurrent.Future

class MockCardDao(userDao: MockUserDao) extends CardDao {

  var mockCards: Map[Long,Seq[DbCard]] = Map(
    userDao.user1.id -> Seq(DbCard(1,"mock1",Seq(),"",1), DbCard(1,"mock2",Seq(),"",2))
  )

  override def getPlayerCards(playerId: Long): Future[Option[Seq[DbCard]]] = {
    Future.successful(mockCards.get(playerId))
  }

  override def insertCards(playerId: Long, batch: Int, cards: Seq[ApiCard]): Future[Boolean] = {
    val oldCards = mockCards.getOrElse(playerId, Seq())
    val dbCards = cards.map(c => DbCard(playerId,c.name,c.colors,c.imageUrl,batch))
    mockCards = mockCards + (playerId -> (oldCards ++ dbCards))
    Future.successful(true)
  }

  override def deletePlayerCards(playerId: Long): Future[Boolean] = {
    mockCards = mockCards.filter {
      case (id, _) if id == playerId => false
      case _ => true
    }
    Future.successful(true)
  }
}
