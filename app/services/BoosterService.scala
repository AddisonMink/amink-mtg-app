package services

import models.{ApiCard, CardSet}

import scala.util.Random

object BoosterService {

  def assembleBooster(set: CardSet): Seq[ApiCard] = {
    val commons = Random.shuffle(set.commons).take(10)
    val uncommons = Random.shuffle(set.uncommons).take(3)
    val rares = Random.shuffle(set.rares).take(1)
    val cards = rares ++ uncommons ++ commons
    cards.groupBy(_.colors).values.flatten.toSeq
  }
}
