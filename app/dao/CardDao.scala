package dao

import java.sql.ResultSet

import javax.inject.Inject
import play.api.db.Database
import scala.concurrent.ExecutionContext
import com.google.inject.ImplementedBy
import models.{ApiCard, DbCard}

import scala.concurrent.Future

@ImplementedBy(classOf[CardDaoImpl])
trait CardDao {

  def getPlayerCards(playerId: Long): Future[Option[Seq[DbCard]]]

  def insertCards(playerId: Long, cards: Seq[ApiCard]): Future[Boolean]

  def deletePlayerCards(playerId: Long): Future[Boolean]
}

class CardDaoImpl @Inject()(protected val db: Database)(implicit ec: ExecutionContext) extends CardDao with PostgresDao[DbCard] {

  override protected val createTableSql: String =
    """ CREATE TABLE IF NOT EXISTS cards (
      |   playerId INT NOT NULL,
      |   name varchar(100) NOT NULL,
      |   colors TEXT NOT NULL,
      |   imageUrl TEXT NOT NULL
      | );
      |
      | CREATE INDEX IF NOT EXISTS idx_playerId ON cards(playerId);
      |""".stripMargin

  override protected def readRow(set: ResultSet): DbCard = {
    val id = set.getLong("playerId")
    val name = set.getString("name")
    val colors = set.getString("colors").split(",").toSeq
    val url = set.getString("imageUrl")
    DbCard(id,name,colors,url)
  }

  override def getPlayerCards(playerId: Long): Future[Option[Seq[DbCard]]] = Future {
    val sql = s"SELECT * FROM cards WHERE playerId = $playerId;"
    val cards = readAll(query(sql))
    if(cards.nonEmpty) Some(cards) else None
  }

  override def insertCards(playerId: Long, cards: Seq[ApiCard]): Future[Boolean] = Future {
    val prefixSQL = "INSERT INTO cards (playerId, name, colors, imageUrl) VALUES "
    val bodySql = cards
      .map(c => c.copy(name = c.name.replace("'", "''"))) // Escape single quotes
      .map(c => s"($playerId, '${c.name}', '${c.colors.mkString(",")}', '${c.imageUrl}')")
      .mkString(", ")
    val sql = prefixSQL ++ bodySql ++ ";"
    execute(sql)
  }

  override def deletePlayerCards(playerId: Long): Future[Boolean] = Future {
    val sql = s"DELETE FROM cards WHERE playerId = $playerId;"
    execute(sql)
  }
}