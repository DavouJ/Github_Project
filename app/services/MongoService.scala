package services

import models.{DatabaseError, MongoUserModel}
import repositories.MongoRepository

import javax.inject.Inject
import scala.concurrent.Future

class MongoService @Inject()(mongoRepository: MongoRepository){

  def create(user: MongoUserModel): Future[Either[DatabaseError.BadDBResponse, MongoUserModel]] = {
    mongoRepository.create(user)
  }

}
