package de.datev.samples.kotlinbeer

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.web.reactive.function.server.*
import java.net.URI

suspend fun <T> Either<RequestError, T>.foldServerResponse(func: suspend (T) -> ServerResponse): ServerResponse =
  fold({ it.responseError() }, { func(it) })

private suspend fun RequestError.responseError(): ServerResponse = when (this) {
  ResourceNotFound -> responseNoContent()
  InvalidObjectId -> ServerResponse.badRequest().bodyValueAndAwait(message)
}

suspend fun Any.responseOk() = ServerResponse.ok().bodyValueAndAwait(this)
private suspend inline fun <reified T : Any> Flow<T>.responseOk() = ServerResponse.ok().bodyAndAwait(this)
suspend fun HasId.responseCreated(rootUrl: String) =
  ServerResponse.created(URI("$rootUrl/$id")).bodyValueAndAwait(this)

suspend fun responseNoContent() = ServerResponse.noContent().buildAndAwait()
fun ServerRequest.objectId() = Either.catch { ObjectId(pathVariable("id")) }.mapLeft { InvalidObjectId }

object InvalidObjectId : RequestError("the given id is invalid")
sealed class RequestError(val message: String)
object ResourceNotFound : RequestError("the requested resource was not found")
