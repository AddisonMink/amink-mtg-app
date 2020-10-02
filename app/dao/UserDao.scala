package dao

import java.sql.ResultSet

import com.google.inject.ImplementedBy
import javax.inject.Inject
import model.User
import play.api.db.Database
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  def getUsers: Future[Seq[User]]

  def getUser(id: Long): Future[Option[User]]
}

class UserDaoImpl @Inject()(db: Database)(implicit ec: ExecutionContext) extends UserDao {

  private val createTableSql =
    """ CREATE TABLE IF NOT EXISTS users (
      |   id SERIAL PRIMARY KEY,
      |   name varchar(100) NOT NULL
      | );
      |""".stripMargin

  private def readRow(set: ResultSet): User = {
    val id = set.getLong("id")
    val name = set.getString("name")
    User(id,name)
  }

  private def readAll(set: ResultSet): Seq[User] = {
    val buffer = ListBuffer.empty[User]
    while(set.next()) buffer.addOne(readRow(set))
    buffer.toSeq
  }

  private def query(sql: String): ResultSet = {
    db.withConnection { conn =>
      val stmt = conn.createStatement()
      stmt.execute(createTableSql)
      stmt.executeQuery(sql)
    }
  }

  override def getUsers: Future[Seq[User]] = Future {
    val sql = "SELECT * FROM users"
    readAll(query(sql))
  }

  override def getUser(id: Long): Future[Option[User]] = Future {
    val sql = s"SELECT * FROM users WHERE id = $id"
    readAll(query(sql)).headOption
  }
}