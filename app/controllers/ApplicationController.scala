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

  def create(userName: String): Action[AnyContent]= Action.async {

    gitHubService.getGitHubUser(user = userName).value.flatMap {
      case Right(user) =>
        mongoService.create(user.transform).flatMap {
          case Right(item)=>
            mongoService.index().map {
              case Right(item: Seq[MongoUserModel]) => Ok {views.html.saved(item)}
              case Left(error: DatabaseError) => Status(error.ResponseStatus)(Json.toJson("index fail", error.reason))
            }
          case Left(error)
          => Future.successful(Status(error.ResponseStatus)(Json.toJson("creation fail", error.reason)))
        }
      case Left(error: APIError) =>
        Future.successful(Status(error.httpResponseStatus)(Json.toJson( error.reason)))
    }
  }

  def getSaved(): Action[AnyContent]= Action.async {

    mongoService.index().map {
      case Right(item: Seq[MongoUserModel]) => Ok {views.html.savedProfiles(item)}
      case Left(error: DatabaseError) => Status(error.ResponseStatus)(Json.toJson("index fail", error.reason))
    }
  }

  def getUser(search: String) = Action.async { implicit request =>

    gitHubService.getGitHubUser(user = search).value.map {
      case Right(user) =>
        Ok(views.html.searchResult(search, user))
      case Left(error: APIError) =>
        Status(error.httpResponseStatus)(Json.toJson(error.reason))
    }
  }

  def delete(id: Int) = Action.async { implicit request => {

    mongoService.delete(id).map {
      case Right(true) => Redirect("/github/saved")
      case Left(error: DatabaseError) => Status(error.ResponseStatus)(Json.toJson(error.reason))
    }
  }
  }

  def update(name: String) = Action.async { implicit request => {

    Future.successful()
  }
  }

  }



