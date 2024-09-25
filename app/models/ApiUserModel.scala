package models

import play.api.libs.json.{Json, OFormat}

case class ApiUserModel(id: Int,
                        login: String,
                        avatar_url: String,
                        created_at: String,
                        location: Option[String],
                        followers: Int,
                        following: Int){
  def transform = MongoUserModel(_id = id,
    login = this.login,
    avatar_url = this.avatar_url,
    created_at = this.created_at,
    location = this.location,
    followers = this.followers,
    following = this.following)
}

object ApiUserModel {
  implicit val formats: OFormat[ApiUserModel] = Json.format[ApiUserModel]
}



