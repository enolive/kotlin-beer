package de.datev.samples.kotlinbeer

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.net.URI

@Component
class BeerHandler(private val repository: BeerRepository) {
  suspend fun getAllBeers(serverRequest: ServerRequest): ServerResponse {
    val beers = repository.findAll()
    /* flowOf(
    PartialBeer(brand = "Schanzenbräu", name = "Rot", strength = 4.9.toBigDecimal()),
    PartialBeer(brand = "Orca Bräu", name = "Timeless IPA", strength = 5.5.toBigDecimal())
  ) */
    return ServerResponse.ok().bodyAndAwait(beers)
  }

  suspend fun createBeer(serverRequest: ServerRequest): ServerResponse {
    val beerBody = serverRequest.awaitBody<PartialBeer>()
    val beer = Beer(brand = beerBody.brand, name = beerBody.name, strength = beerBody.strength)
    val createdBeer = repository.save(beer)
    return ServerResponse.created(URI("/beers/${createdBeer.id}")).bodyValueAndAwait(createdBeer)
  }
}
