package de.welcz.samples.kotlinbeer.beers

import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import de.welcz.samples.kotlinbeer.helpers.*
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class BeerHandler(
  private val beerRepository: BeerRepository,
) {

  suspend fun getAllBeers(request: ServerRequest): ServerResponse {
    val beers = beerRepository.findAll()
    return beers.responseOk()
  }

  suspend fun getBeer(request: ServerRequest): ServerResponse = either {
    val id = request.objectId().bind()
    val beer = beerRepository.tryFindById(id).bind()
    beer
  }.toServerResponse { it.responseOk() }

  suspend fun createBeer(request: ServerRequest, rootUrl: String): ServerResponse = either {
    val toCreate = request.bodyJson<PartialBeer>().bind()
    val created = toCreate.withId().let { beerRepository.save(it) }
    created
  }.toServerResponse { it.responseCreated(rootUrl) }

  suspend fun deleteBeer(request: ServerRequest): ServerResponse = either {
    val id = request.objectId().bind()
    beerRepository.deleteById(id)
  }.toServerResponse { responseNoContent() }

  suspend fun updateBeer(request: ServerRequest): ServerResponse = either {
    val id = request.objectId().bind()
    beerRepository.tryFindById(id).bind()
    val toUpdate = request.bodyJson<PartialBeer>().bind()
    val updated = toUpdate.withId(id).let { beerRepository.save(it) }
    updated
  }.toServerResponse { it.responseOk() }

  private suspend fun BeerRepository.tryFindById(id: ObjectId) = either {
    ensureNotNull(findById(id)) { ResourceNotFound }
  }

  private fun PartialBeer.withId(id: ObjectId? = null) =
    Beer(id = id, brand = brand, name = name, strength = strength)
}
