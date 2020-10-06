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

  def addUser(name: String): Future[Long]

  def removeUser(id: Long): Future[Boolean]
}

class UserDaoImpl @Inject()(protected val db: Database)(implicit ec: ExecutionContext) extends UserDao with PostgresDao[User] {

  protected val createTableSql =
    """ CREATE TABLE IF NOT EXISTS users (
      |   id SERIAL PRIMARY KEY,
      |   name varchar(100) NOT NULL
      | );
      |""".stripMargin

  override protected def readRow(set: ResultSet): User = {
    val id = set.getLong("id")
    val name = set.getString("name")
    User(id,name)
  }

  override def getUsers: Future[Seq[User]] = Future {
    val sql = "SELECT * FROM users"
    readAll(query(sql))
  }

  override def getUser(id: Long): Future[Option[User]] = Future {
    val sql = s"SELECT * FROM users WHERE id = $id"
    readAll(query(sql)).headOption
  }

  override def addUser(name: String): Future[Long] = Future {
    val sql = s"INSERT INTO users (name) VALUES ('$name') RETURNING id;"
    val result = query(sql)
    result.next()
    result.getLong("id")
  }

  override def removeUser(id: Long): Future[Boolean] = Future {
    val sql = s"DELETE FROM users WHERE id = $id;"
    execute(sql)
  }
}