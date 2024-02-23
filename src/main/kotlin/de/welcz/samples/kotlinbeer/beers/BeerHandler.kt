package de.welcz.samples.kotlinbeer.beers

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.rightIfNotNull
import de.welcz.samples.kotlinbeer.*
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
  }.foldServerResponse { it.responseOk() }

  suspend fun createBeer(request: ServerRequest, rootUrl: String): ServerResponse = either {
    val toCreate = request.bodyJson<PartialBeer>().bind()
    val created = toCreate.complete().let { beerRepository.save(it) }
    created
  }.foldServerResponse { it.responseCreated(rootUrl) }

  suspend fun deleteBeer(request: ServerRequest): ServerResponse = either {
    val id = request.objectId().bind()
    beerRepository.deleteById(id)
  }.foldServerResponse { responseNoContent() }

  suspend fun updateBeer(request: ServerRequest): ServerResponse = either {
    val id = request.objectId().bind()
    beerRepository.tryFindById(id).bind()
    val toUpdate = request.bodyJson<PartialBeer>().bind()
    val updated = toUpdate.complete(id).let { beerRepository.save(it) }
    updated
  }.foldServerResponse { it.responseOk() }

  private suspend fun BeerRepository.tryFindById(id: ObjectId): Either<ResourceNotFound, Beer> =
    findById(id).rightIfNotNull { ResourceNotFound }

  private fun PartialBeer.complete(id: ObjectId? = null) =
    Beer(id = id, brand = brand, name = name, strength = strength)
}
