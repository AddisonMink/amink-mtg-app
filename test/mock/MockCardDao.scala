package mock

import dao.CardDao
import models.{ApiCard, DbCard}

import scala.concurrent.Future

object MockCardDao extends CardDao {

  private var mockCards: Map[Long,Seq[DbCard]] = Map(
    MockUserDao.user1.id -> Seq(DbCard(1,"mock1",Seq(),""), DbCard(1,"mock2",Seq(),""))
  )

  override def getPlayerCards(playerId: Long): Future[Option[Seq[DbCard]]] = {
    Future.successful(mockCards.get(playerId))
  }

  override def insertCards(playerId: Long, cards: Seq[ApiCard]): Future[Unit] = {
    val oldCards = mockCards.get(playerId).getOrElse(Seq())
    val dbCards = cards.map(c => DbCard(playerId,c.name,c.colors,c.imageUrl))
    mockCards = mockCards + (playerId -> (oldCards ++ dbCards))
    Future.successful(true)
  }
}
