package dao

import java.sql.ResultSet

import com.google.inject.ImplementedBy
import javax.inject.Inject
import models.User
import play.api.db.Database
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserDaoImpl])
trait UserDao {

  def getUsers: Future[Seq[User]]

  def getUser(id: Long): Future[Option[User]]

  def addUser(name: String): Future[User]

  def removeUser(id: Long): Future[Boolean]

  def incrementBatch(id: Long): Future[Option[User]]
}

class UserDaoImpl @Inject()(protected val db: Database)(implicit ec: ExecutionContext) extends UserDao with PostgresDao[User] {

  protected val createTableSql =
    """ CREATE TABLE IF NOT EXISTS users (
      |   id SERIAL PRIMARY KEY,
      |   name varchar(100) NOT NULL,
      |   batch integer NOT NULL
      | );
      |""".stripMargin

  override protected def readRow(set: ResultSet): User = {
    val id = set.getLong("id")
    val name = set.getString("name")
    val batch = set.getInt("batch")
    User(id,name, batch = batch)
  }

  override def getUsers: Future[Seq[User]] = Future {
    val sql = "SELECT * FROM users"
    readAll(query(sql))
  }

  override def getUser(id: Long): Future[Option[User]] = Future {
    val sql = s"SELECT * FROM users WHERE id = $id"
    readAll(query(sql)).headOption
  }

  override def addUser(name: String): Future[User] = Future {
    val sql = s"INSERT INTO users (name, batch) VALUES ('$name', 0) RETURNING *;"
    readAll(query(sql)).head
  }

  override def removeUser(id: Long): Future[Boolean] = Future {
    val sql = s"DELETE FROM users WHERE id = $id;"
    execute(sql)
  }

  override def incrementBatch(id: Long): Future[Option[User]] = Future {
    val sql = s"UPDATE users SET batch = (batch + 1) WHERE id = $id RETURNING *;"
    readAll(query(sql)).headOption
  }
}