package de.welcz.samples.kotlinbeer

import de.welcz.samples.kotlinbeer.beers.BeerHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class Router(private val beerHandler: BeerHandler) {
  @Bean
  fun routesConfig() = coRouter {
    "/beers".nest {
      GET("") { beerHandler.getAllBeers(it) }
      GET("{id}") { beerHandler.getBeer(it) }
      POST("") { beerHandler.createBeer(it, "/beers") }
      PUT("{id}") { beerHandler.updateBeer(it) }
      DELETE("{id}") { beerHandler.deleteBeer(it) }
    }
  }
}
