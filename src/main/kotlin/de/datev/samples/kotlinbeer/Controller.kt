package de.datev.samples.kotlinbeer

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RequestMapping("/beers")
@RestController
class Controller(
  private val beerRepository: BeerRepository,
) {
  @GetMapping
  fun getAllBeers() = beerRepository.findAll()

  @PostMapping
  suspend fun createBeer(@RequestBody toCreate: PartialBeer) =
    toCreate.complete().let { beerRepository.save(it) }.wrapInCreatedResponse()

  private fun HasId.wrapInCreatedResponse() =
    ResponseEntity.created(URI("/beers/$id")).body(this)

  private fun PartialBeer.complete() =
    Beer(brand = brand, name = name, strength = strength)
}

