package repositories

import models.{DatabaseError, MongoUserModel}
import org.mongodb.scala.bson.Document
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.{Filters, IndexModel, Indexes}
import org.mongodb.scala.result.DeleteResult
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
    }.recover { case _ =>
      Left(DatabaseError.BadDBResponse(500, s"Duplicate User"))
    }

  def index(): Future[Either[DatabaseError.BadDBResponse, Seq[MongoUserModel]]] = //list of all the dataModels in the database
    collection.find().toFuture().map {
      users: Seq[MongoUserModel] => Right(users)
    }.recover { case _ =>
      Left(DatabaseError.BadDBResponse(NOT_FOUND, "No users have been found"))
    }

  private def byID(id: Int): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )

  def delete(id: Int): Future[Either[DatabaseError.BadDBResponse, Boolean]] =
    collection.deleteOne(
      filter = byID(id)
    ).toFuture().map { x : DeleteResult =>
      x.wasAcknowledged() match{
        case true =>
          Right(true)
        case _ =>
          Left(DatabaseError.BadDBResponse(500, "Could not delete"))
      }
    }

  def update(id: Int, entry: MongoUserModel): Future[Either[DatabaseError.BadDBResponse, Boolean]] = {

    val updateDocument = Document("$set" -> Document("login" -> entry.login,
      "avatar_url" -> entry.avatar_url,
      "created_at" -> entry.created_at,
      "location" -> entry.location,
      "followers" -> entry.followers,
      "following" -> entry.following))

    collection.updateOne(Filters.equal("_id", id), updateDocument)
      .toFuture().map {
        user => Right(user.wasAcknowledged())
      }.recover { case _ =>
        Left(DatabaseError.BadDBResponse(500, "Could not update"))
      }
  }
}
