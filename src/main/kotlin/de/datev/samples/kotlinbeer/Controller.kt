package de.datev.samples.kotlinbeer

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RequestMapping("/beers")
@RestController
class Controller {
  @GetMapping
  fun getAllBeers(): Flow<Beer> {
    return flowOf(Beer("Nestle", "Wasser", 0.toBigDecimal()))
  }
}

data class Beer(
  val brand: String,
  val name: String,
  val strength: BigDecimal,
)
