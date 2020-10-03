package dao

import models.{ApiCard, DbCard}

import scala.concurrent.Future

trait CardDao {

  def getPlayerCards(playerId: Long): Future[Option[Seq[DbCard]]]

  def insertCards(playerId: Long, cards: Seq[ApiCard]): Future[Boolean]
}
