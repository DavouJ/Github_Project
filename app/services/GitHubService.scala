package services

import cats.data.EitherT
import connectors.GitHubConnector
import models.{APIError, ApiUserModel}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GitHubService @Inject()(connector: GitHubConnector) {

  def getGitHubUser(urlOverride: Option[String] = None, user: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, ApiUserModel] =
    connector.get[ApiUserModel](urlOverride.getOrElse(s"https://api.github.com/users/$user"))


}
