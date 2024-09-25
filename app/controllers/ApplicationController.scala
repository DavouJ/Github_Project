package controllers

import com.google.inject.Singleton
import models.{APIError, ApiUserModel, DatabaseError, MongoUserModel}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request, Result}
import repositories.MongoRepository
import services.{GitHubService, MongoService}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(
                                       mongoService: MongoService,
                                       gitHubService: GitHubService,
                                     )(implicit ec: ExecutionContext, val controllerComponents: ControllerComponents) extends BaseController {

  def create2(user: ApiUserModel)(implicit request: Request[_]) = {
    mongoService.create(user.transform).map {
      case Right(item)
      => Created(Json.toJson("created"))
      case Left(error)
      => Status(error.ResponseStatus)(Json.toJson(error.reason))
    }
  }
  def create(userName: String): Action[AnyContent]= Action.async { implicit request =>

    gitHubService.getGitHubUser(user = userName).value.flatMap {
      case Right(user) =>
        mongoService.create(user.transform).map {
          case Right(item)
          => Created
          case Left(error)
          => Status(error.ResponseStatus)(Json.toJson(error.reason))
        }
      case Left(_) =>
        BadRequest
    }
    //Future(Created(Json.toJson("created")))

  }


  def getUser(search: String) = Action.async { implicit request =>

    gitHubService.getGitHubUser(user = search).value.map {
      case Right(user) =>
        Ok(views.html.userProfile(search, user))
      case Left(error: APIError) =>
        Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }
  }

  //  def displayUser(search: String) = Action.async { implicit request =>
  //
  //  }
}



