package de.datev.samples.kotlinbeer

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/beers")
@RestController
class Controller(
  private val beerRepository: BeerRepository,
) {
  @GetMapping
  fun getAllBeers() = beerRepository.findAll()
}

