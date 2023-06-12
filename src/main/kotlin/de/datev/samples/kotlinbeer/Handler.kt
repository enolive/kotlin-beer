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
      GET("") {
        val beers = handler.getAllBeers()
        ServerResponse.ok().bodyAndAwait(beers)
      }
      GET("{id}") {
        val id = ObjectId(it.pathVariable("id"))
        val beer = handler.getBeer(id)
        ServerResponse.ok().bodyValueAndAwait(beer)
      }
      POST("") {
        val partialBeer = it.awaitBody<PartialBeer>()
        val createdBeer = handler.createBeer(partialBeer)
        ServerResponse.created(URI("/beers/${createdBeer.id}")).bodyValueAndAwait(createdBeer)
      }
      PUT("{id}") {
        val id = ObjectId(it.pathVariable("id"))
        val partialBeer = it.awaitBody<PartialBeer>()
        val updatedBeer = handler.updateBeer(id, partialBeer)
        ServerResponse.ok().bodyValueAndAwait(updatedBeer)
      }
      DELETE("{id}") {
        val id = ObjectId(it.pathVariable("id"))
        handler.deleteBeer(id)
        ServerResponse.noContent().buildAndAwait()
      }
    }
  }
}

