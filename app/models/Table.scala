package models


import slick.driver.H2Driver.api._
import com.github.tototoshi.slick.H2JodaSupport._
import org.joda.time.DateTime


case class Task(id: Int, name: String, dueDate: DateTime, completed: Int)
class Tasks(tag: Tag) extends Table[Task](tag, "tasks") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def dueDate = column[DateTime]("dueDate")

  def completed = column[Int]("completed")

  def * = (id, name, dueDate, completed) <>(Task.tupled, Task.unapply)
}