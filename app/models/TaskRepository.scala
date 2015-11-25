package models

import javax.inject.Inject

import com.github.tototoshi.slick.H2JodaSupport._
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scalaz._
import Scalaz._

class TaskRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  // create schema and insert sample data
  val taskQuery = TableQuery[Tasks]
  db.run(DBIO.seq(
    taskQuery.schema.create,
    taskQuery += Task(0, "play with cat", DateTime.now.plusDays(-1), 0),
    taskQuery += Task(0, "play with dog", DateTime.now.plusDays(-2), 0),
    taskQuery += Task(0, "wash dishes", DateTime.now.plusDays(-2), 0),
    taskQuery += Task(0, "pay bill", DateTime.now.plusDays(-3), 0),
    taskQuery += Task(0, "play with daughter", DateTime.now.plusDays(-4), 1)
  ))

  def all(): Future[Seq[Task]] = db.run(taskQuery.result)

  def incompleteTasks: Future[Seq[Task]] = db.run(taskQuery.filter(_.completed =!= 1).result)

  def insert(task: Task): Future[Unit] = db.run(taskQuery += task).map { _ => () }

  def insert(tasks: Seq[Task]): Future[Unit] = db.run(taskQuery ++= tasks).map { _ => () }

  def delete(id: Int): Future[Int] = db.run(taskQuery.filter(_.id === id).delete)

  def updateCompleted(id: Int, isCompleted: Boolean): Future[Int] =
    db.run(taskQuery.filter(_.id === id).map(_.completed).update(isCompleted ? 1 | 0))

  def tasksAfter(dateTime: DateTime): Future[Seq[Task]] =
    db.run(taskQuery.filter(_.dueDate >= dateTime).result)

  def tasksBefore(dateTime: DateTime): Future[Seq[Task]] =
    db.run(taskQuery.filter(_.dueDate <= dateTime).result)
}
