package connectors


import cats.data.EitherT
import models.APIError
import play.api.libs.json.{JsError, JsResult, JsSuccess, Json, OFormat}
import play.api.libs.ws.{WSClient, WSResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GitHubConnector @Inject()(ws: WSClient) {
  def getUser[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Response] = {
    val request = ws.url(url)
//    val request = ws.url(url)
//      .withHttpHeaders(
//        "Authorization" -> s"Bearer ${EnvironmentVariables.authToken}",  // Add the Authorization header with the token
//        "Accept" -> "application/vnd.github+json",
//        "X-GitHub-Api-Version" -> "2022-11-28"
//      )
    val response = request.get()
    EitherT {
      response.map {
        result =>
          result.json.validate[Response] match {
            case JsSuccess(user, _) => Right(user)
            case JsError(errors) => Left(APIError.BadAPIResponse(500, s"$errors"))
          }
      }
    }
  }

  def getRepos[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, List[Response]] = {
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response.map {
        result =>
          result.json.validate[List[Response]] match {
            case JsSuccess(repoList, _) => Right(repoList)
            case JsError(errors) => Left(APIError.BadAPIResponse(500, s"$errors"))
          }
      }
    }
  }
}
