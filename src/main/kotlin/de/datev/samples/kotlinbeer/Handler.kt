package de.datev.samples.kotlinbeer

import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ResponseStatusException
import java.net.URI

@Component
class Handler(
  private val beerRepository: BeerRepository,
) {

  suspend fun getAllBeers(request: ServerRequest): ServerResponse {
    val beers = beerRepository.findAll()
    return beers.responseOk()
  }

  suspend fun getBeer(request: ServerRequest): ServerResponse {
    val id = request.objectId()
    val beer = beerRepository.findById(id) ?: throw ResponseStatusException(HttpStatus.NO_CONTENT)
    return beer.responseOk()
  }

  suspend fun createBeer(request: ServerRequest, rootUrl: String): ServerResponse {
    val partialBeer = request.awaitBody<PartialBeer>()
    val createdBeer = partialBeer.complete().let { beerRepository.save(it) }
    return createdBeer.responseCreated(rootUrl)
  }

  suspend fun deleteBeer(request: ServerRequest): ServerResponse {
    val id = request.objectId()
    beerRepository.deleteById(id)
    return noContent()
  }

  suspend fun updateBeer(request: ServerRequest): ServerResponse {
    val id = request.objectId()
    val partialBeer = request.awaitBody<PartialBeer>()
    beerRepository.findById(id) ?: throw ResponseStatusException(HttpStatus.NO_CONTENT)
    val updatedBeer = partialBeer.complete().copy(id = id).let { beerRepository.save(it) }
    return updatedBeer.responseOk()
  }

  private suspend fun Any.responseOk() =
    ServerResponse.ok().bodyValueAndAwait(this)

  private suspend inline fun <reified T : Any> Flow<T>.responseOk() =
    ServerResponse.ok().bodyAndAwait(this)

  private suspend fun HasId.responseCreated(rootUrl: String) =
    ServerResponse.created(URI("$rootUrl/$id")).bodyValueAndAwait(this)

  private suspend fun noContent() = ServerResponse.noContent().buildAndAwait()

  private fun ServerRequest.objectId() = ObjectId(pathVariable("id"))

  private fun PartialBeer.complete() =
    Beer(brand = brand, name = name, strength = strength)
}
