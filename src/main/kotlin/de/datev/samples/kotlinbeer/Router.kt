package de.datev.samples.kotlinbeer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class Router(private val beerHandler: BeerHandler) {
  @Bean
  fun routerConfig() = coRouter {
    "/beers".nest {
      GET("", beerHandler::getAllBeers)
      POST("", beerHandler::createBeer)
    }
  }
}
