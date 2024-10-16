package services

import cats.data.EitherT
import connectors.GitHubConnector
import models.{APIError, ApiUserModel, RepoModel}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GitHubService @Inject()(connector: GitHubConnector) {

  def getGitHubUser(urlOverride: Option[String] = None, user: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, ApiUserModel] =
    connector.getUser[ApiUserModel](urlOverride.getOrElse(s"https://api.github.com/users/$user"))

  def getRepos(urlOverride: Option[String] = None, user: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, List[RepoModel]] =
    connector.getRepos[RepoModel](urlOverride.getOrElse(s"https://api.github.com/users/$user/repos"))
}
