package de.datev.samples.kotlinbeer

import org.bson.types.ObjectId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ResponseStatusException
import java.net.URI

@Component
class Handler(
  private val beerRepository: BeerRepository,
) {
  fun getAllBeers() = beerRepository.findAll()

  suspend fun createBeer(@RequestBody toCreate: PartialBeer) =
    toCreate.complete().let { beerRepository.save(it) }

  suspend fun getBeer(@PathVariable id: ObjectId) =
    beerRepository.findById(id) ?: throw ResponseStatusException(HttpStatus.NO_CONTENT)

  suspend fun deleteBeer(@PathVariable id: ObjectId) =
    beerRepository.deleteById(id)

  suspend fun updateBeer(@PathVariable id: ObjectId, @RequestBody toUpdate: PartialBeer): Beer {
    beerRepository.findById(id) ?: throw ResponseStatusException(HttpStatus.NO_CONTENT)
    return toUpdate.complete().copy(id = id).let { beerRepository.save(it) }
  }

  suspend fun deleteBeer(request: ServerRequest): ServerResponse {
    val id = ObjectId(request.pathVariable("id"))
    deleteBeer(id)
    return ServerResponse.noContent().buildAndAwait()
  }

  suspend fun updateBeer(request: ServerRequest): ServerResponse {
    val id = ObjectId(request.pathVariable("id"))
    val partialBeer = request.awaitBody<PartialBeer>()
    val updatedBeer = updateBeer(id, partialBeer)
    return ServerResponse.ok().bodyValueAndAwait(updatedBeer)
  }

  suspend fun createBeer(request: ServerRequest): ServerResponse {
    val partialBeer = request.awaitBody<PartialBeer>()
    val createdBeer = createBeer(partialBeer)
    return ServerResponse.created(URI("/beers/${createdBeer.id}")).bodyValueAndAwait(createdBeer)
  }

  suspend fun getBeer(request: ServerRequest): ServerResponse {
    val id = ObjectId(request.pathVariable("id"))
    val beer = getBeer(id)
    return ServerResponse.ok().bodyValueAndAwait(beer)
  }

  suspend fun getAllBeers(request: ServerRequest): ServerResponse {
    val beers = getAllBeers()
    return ServerResponse.ok().bodyAndAwait(beers)
  }

  private fun HasId.wrapInCreatedResponse() =
    ResponseEntity.created(URI("/beers/$id")).body(this)

  private fun PartialBeer.complete() =
    Beer(brand = brand, name = name, strength = strength)
}

@Configuration
class Router(private val handler: Handler) {
  @Bean
  fun routsConfig() = coRouter {
    "/beers".nest {
      GET("") { handler.getAllBeers(it) }
      GET("{id}") { handler.getBeer(it) }
      POST("") { handler.createBeer(it) }
      PUT("{id}") { handler.updateBeer(it) }
      DELETE("{id}") { handler.deleteBeer(it) }
    }
  }
}
