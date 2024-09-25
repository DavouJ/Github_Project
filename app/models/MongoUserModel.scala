package models

import play.api.libs.json.{Json, OFormat, OWrites}

case class MongoUserModel(_id: Int,
                          login: String,
                          avatar_url: String,
                          created_at: String,
                          location: Option[String],
                          followers: Int,
                          following: Int)

object MongoUserModel {
  implicit val formats: OFormat[MongoUserModel] = Json.format[MongoUserModel]
}