package de.datev.samples.kotlinbeer

import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
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

  @GetMapping("{id}")
  suspend fun getBeer(@PathVariable id: ObjectId) =
    beerRepository.findById(id) ?: throw ResponseStatusException(HttpStatus.NO_CONTENT)

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("{id}")
  suspend fun deleteBeer(@PathVariable id: ObjectId) =
    beerRepository.deleteById(id)

  @PutMapping("{id}")
  suspend fun updateBeer(@PathVariable id: ObjectId, @RequestBody toUpdate: PartialBeer): Beer {
    val found = beerRepository.findById(id)
    return toUpdate.complete().copy(id = id).let { beerRepository.save(it) }
  }

  private fun HasId.wrapInCreatedResponse() =
    ResponseEntity.created(URI("/beers/$id")).body(this)

  private fun PartialBeer.complete() =
    Beer(brand = brand, name = name, strength = strength)
}

