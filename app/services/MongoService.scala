package services

import models.{ApiUserModel, DatabaseError, MongoUserModel}
import repositories.MongoRepository

import javax.inject.Inject
import scala.concurrent.Future

class MongoService @Inject()(mongoRepository: MongoRepository){

  def create(user: MongoUserModel): Future[Either[DatabaseError.BadDBResponse, MongoUserModel]] = {
    mongoRepository.create(user)
  }
  def index(): Future[Either[DatabaseError.BadDBResponse, Seq[MongoUserModel]]] = {
    mongoRepository.index()
  }
  def delete(id: Int): Future[Either[DatabaseError.BadDBResponse, Boolean]] = {
    mongoRepository.delete(id)
  }
//  def update(id: String, entry: MongoUserModel): Future[Either[DatabaseError.BadDBResponse, Boolean]] = {
//    mongoRepository.update(id, List("login" -> entry.login,
//      "avatar_url" -> entry.avatar_url,
//      "created_at" -> entry.created_at,
//      "location" -> entry.location,
//      "followers" -> entry.followers,
//      "following" -> entry.following))
//  }
}
