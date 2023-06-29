package de.datev.samples.kotlinbeer

import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/beers")
class BeerController(private val repository: BeerRepository) {

  @GetMapping
  fun getBeers(): Flow<Beer> = repository.findAll()

  @GetMapping("{id}")
  suspend fun getBeer(@PathVariable id: ObjectId): ResponseEntity<Beer> = repository.findById(id)
    ?.let { found -> ResponseEntity.ok(found) }
    ?: ResponseEntity.noContent().build()

  @PostMapping
  suspend fun addBeer(@RequestBody beer: PartialBeer): ResponseEntity<Beer> {
    val created = repository.save(beer.complete())
    return ResponseEntity.created(URI("/beers/${created.id}")).body(created)
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  suspend fun deleteBeer(@PathVariable id: ObjectId) {
    repository.deleteById(id)
  }

  @PutMapping("{id}")
  @Transactional
  suspend fun putBeer(@PathVariable id: ObjectId, @RequestBody beerToUpdate: PartialBeer): ResponseEntity<Beer> {
    repository.findById(id) ?: return ResponseEntity.noContent().build()
    val newBeer = beerToUpdate.complete().copy(id = id)
    return repository.save(newBeer).let { ResponseEntity.ok(it) }
  }

  private suspend fun PartialBeer.complete() =
    Beer(brand = brand, name = name, strength = strength)
}
