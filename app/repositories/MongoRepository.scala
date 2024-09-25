package repositories

import models.{DatabaseError, MongoUserModel}
import org.mongodb.scala.model.{IndexModel, Indexes}
import play.api.http.Status.NOT_FOUND
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MongoRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[MongoUserModel](
  collectionName = "GitHub_User_Models",
  mongoComponent = mongoComponent,
  domainFormat = MongoUserModel.formats,
  indexes = Seq(IndexModel(Indexes.ascending("_id"))),
  replaceIndexes = false
) {
  def create(user: MongoUserModel): Future[Either[DatabaseError.BadDBResponse, MongoUserModel]] =
    collection.insertOne(user).toFuture().map {
      _ => Right(user)
    }.recover { case error: Error =>
      Left(DatabaseError.BadDBResponse(500, s"Duplicate User: $error"))
    }

  def index(): Future[Either[DatabaseError.BadDBResponse, Seq[MongoUserModel]]] = //list of all the dataModels in the database
    collection.find().toFuture().map {
      users: Seq[MongoUserModel] => Right(users)
    }.recover { case _ =>
      Left(DatabaseError.BadDBResponse(NOT_FOUND, "No users have been found"))
    }
}
