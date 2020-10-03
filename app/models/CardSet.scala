package models

case class CardSet(
  id: String,
  commons: Seq[ApiCard],
  uncommons: Seq[ApiCard],
  rares: Seq[ApiCard]
)
