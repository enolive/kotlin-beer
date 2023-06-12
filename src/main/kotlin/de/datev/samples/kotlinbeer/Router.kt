package de.datev.samples.kotlinbeer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class Router(private val handler: Handler) {
  @Bean
  fun routsConfig() = coRouter {
    "/beers".nest {
      GET("") { handler.getAllBeers(it) }
      GET("{id}") { handler.getBeer(it) }
      POST("") { handler.createBeer(it, "/beers") }
      PUT("{id}") { handler.updateBeer(it) }
      DELETE("{id}") { handler.deleteBeer(it) }
    }
  }
}
