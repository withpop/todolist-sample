package controllers

import javax.inject.Inject

import models.{Task, TaskRepository}
import org.joda.time.DateTime
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.Future

class TaskController @Inject() (taskRep: TaskRepository) extends Controller {

  private val newTaskForm = Form(mapping(
    "name" -> text,
    "dueDate" -> jodaDate("yyyyMMddHHmmss")
  )(Task.apply(0, _, _, 0))( t => Some((t.name, t.dueDate)) ))

  def put = Action.async { implicit request =>
    newTaskForm.bindFromRequest.fold (
      hasErrors => Future(BadRequest),
      task => taskRep.insert(task).map(_ => Ok)
    )
  }

  def delete(id: Int) = Action.async {
    taskRep.delete(id).map(_ => Ok)
  }

  def update(id: Int) = Action.async { request =>
    val completed = for(
      map <- request.body.asFormUrlEncoded;
      args <- map.get("completed");
      completed <- args.headOption
    ) yield completed == "true"

    completed match {
      case Some(x) => taskRep.updateCompleted(id, x).map(_ => Ok)
      case None => Future{ BadRequest }
    }
  }

  def getTasks = Action.async { request =>
    val incompleteOnly = request.getQueryString("onlyNotYet").getOrElse("false")
    val result = if (incompleteOnly == "true")
      taskRep.incompleteTasks
    else
      taskRep.all

    result.map{ tasks =>
      Ok(Json.obj("result" ->
        tasks.map( t =>
          Json.obj(
            "id" -> t.id,
            "name" -> t.name,
            "dueDate" -> t.dueDate.toLocalDateTime.toString("yyyyMMddHHmmss"),
            "completed" -> (t.completed == 1)
          )
        )))
    }
  }

}