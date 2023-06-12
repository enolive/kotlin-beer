package de.datev.samples.kotlinbeer

import arrow.core.Either
import de.datev.samples.kotlinbeer.beers.HasId
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.web.reactive.function.server.*
import java.net.URI

val handlerExtLogger = KotlinLogging.logger { }

suspend fun <T> Either<RequestError, T>.foldServerResponse(func: suspend (T) -> ServerResponse): ServerResponse =
  fold({ it.responseError() }, { func(it) })

private suspend fun RequestError.responseError(): ServerResponse {
  handlerExtLogger.warn { this }
  return when (this) {
    ResourceNotFound -> responseNoContent()
    InvalidObjectId, InvalidBody -> ServerResponse.badRequest().bodyValueAndAwait(message)
  }
}

suspend fun Any.responseOk() = ServerResponse.ok().bodyValueAndAwait(this)
suspend inline fun <reified T : Any> Flow<T>.responseOk() = ServerResponse.ok().bodyAndAwait(this)
suspend fun HasId.responseCreated(rootUrl: String) =
  ServerResponse.created(URI("$rootUrl/$id")).bodyValueAndAwait(this)

suspend fun responseNoContent() = ServerResponse.noContent().buildAndAwait()
fun ServerRequest.objectId() = Either.catch { ObjectId(pathVariable("id")) }.mapLeft { InvalidObjectId }
suspend inline fun <reified T : Any> ServerRequest.bodyJson(): Either<InvalidBody, T> =
  Either
    .catch { awaitBody<T>() }
    .tapLeft { handlerExtLogger.error(it) { "failed to deserialize body" } }
    .mapLeft { InvalidBody }

sealed class RequestError(val message: String) {
  override fun toString() = message
}
object InvalidBody : RequestError("the given body is invalid")
object InvalidObjectId : RequestError("the given id is invalid")
object ResourceNotFound : RequestError("the requested resource was not found")
