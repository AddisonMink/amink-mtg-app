package dao

import java.sql.ResultSet
import play.api.db.Database

import scala.collection.mutable.ListBuffer

trait PostgresDao[A] {

  protected val db: Database

  protected val createTableSql: String

  protected def readRow(set: ResultSet): A

  protected final def readAll(set: ResultSet): Seq[A] = {
    val buffer = ListBuffer.empty[A]
    while(set.next()) buffer.addOne(readRow(set))
    buffer.toSeq
  }

  protected final def query(sql: String): ResultSet = {
    db.withConnection { conn =>
      val stmt = conn.createStatement()
      stmt.execute(createTableSql)
      stmt.executeQuery(sql)
    }
  }

  protected final def execute(sql: String): Boolean = {
    db.withConnection { conn =>
      val stmt = conn.createStatement()
      stmt.execute(createTableSql)
      stmt.execute(sql)
    }
  }
}
