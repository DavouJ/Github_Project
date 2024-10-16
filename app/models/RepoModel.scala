package models

import play.api.libs.json.{Json, OFormat}

case class RepoModel (id: Int,
                      name: String,
                      description: Option[String],
                      html_url: String,
                      language: Option[String],
                      forks_count: Int)

object RepoModel {
  implicit val formats: OFormat[RepoModel] = Json.format[RepoModel]
}


