import models.TaskRepository
import org.joda.time.DateTime
import org.scalacheck.Prop.forAll
import org.scalacheck._
import org.scalatest.FunSuite
import play.api._
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

class TaskRepositorySpec extends FunSuite{
  val app = new GuiceApplicationBuilder().configure(
    Configuration.from(
      Map(
        "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
        "slick.dbs.default.db.driver" -> "org.h2.Driver",
        "slick.dbs.default.db.url" -> "jdbc:h2:mem:testdb"
      )
    )
  ).in(Mode.Test)
    .build

  val taskRep = Application.instanceCache[TaskRepository].apply(app)

  test("Incomplete tasks should be filtered") {
    taskRep.incompleteTasks.onComplete{
      case Success(r) => assert(!r.exists(_.completed == 1))
      case Failure(_) => fail("Query failed.")
    }
  }

  test("Tasks should be filtered by DateTime"){
    def genDateTime: Gen[DateTime] = Gen.choose(-10000, 10000).map(m => DateTime.now().plusMinutes(m))

    val afterProp = forAll(genDateTime) { t =>
      val res = Await.result(taskRep.tasksAfter(t), Duration.Inf)
      !res.exists(_.dueDate.compareTo(t) > 0)
    }

    val beforeProp = forAll(genDateTime) { t =>
      val res = Await.result(taskRep.tasksBefore(t), Duration.Inf)
      !res.exists(_.dueDate.compareTo(t) < 0)
    }

    afterProp.check
    beforeProp.check
  }

}
