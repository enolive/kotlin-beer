package de.welcz.samples.kotlinbeer

import de.welcz.samples.kotlinbeer.beers.Beer
import de.welcz.samples.kotlinbeer.beers.BeerHandler
import de.welcz.samples.kotlinbeer.beers.PartialBeer
import org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder
import org.springdoc.core.fn.builders.operation.Builder
import org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder
import org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder
import org.springdoc.core.utils.Constants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

@Configuration
class Router(private val beerHandler: BeerHandler) {
  @Bean
  fun routesConfig(): RouterFunction<ServerResponse> {
    return coRouter {
      "/beers".nest {
        GET("") { beerHandler.getAllBeers(it) }
        withDocs {
          operationId("getBeers")
          summary("gets all existing beers")
          response(
            responseBuilder().responseCode("200").description("List of beers").implementationArray(
              Beer::class.java
            )
          )
        }

        GET("{id}") { beerHandler.getBeer(it) }
        // uglier variant using methods decorated with @Operation that are really verbosy
  //      withDocsAt(BeerHandler::class, BeerHandler::getBeer)
        withDocs {
          operationId("getBeerById")
          summary("get beer by id")
          parameter(beerIdParam())
          response(responseBuilder().responseCode("200").description("existing beer").implementation(Beer::class.java))
          response(beerDoesNotExist())
        }

        POST("") { beerHandler.createBeer(it, "/beers") }
        withDocs {
          operationId("createBeer")
          summary("create a beer")
          requestBody(requestBodyBuilder().implementation(PartialBeer::class.java))
          response(responseBuilder().responseCode("201").description("created beer").implementation(Beer::class.java))
        }

        PUT("{id}") { beerHandler.updateBeer(it) }
        withDocs {
          operationId("updateBeer")
          summary("update an existing beer")
          parameter(beerIdParam())
          requestBody(requestBodyBuilder().implementation(PartialBeer::class.java))
          response(responseBuilder().responseCode("200").description("updated beer").implementation(Beer::class.java))
          response(beerDoesNotExist())
        }

        DELETE("{id}") { beerHandler.deleteBeer(it) }
        withDocs {
          operationId("deleteBeer")
          parameter(beerIdParam())
          summary("delete an existing beer")
          response(responseBuilder().responseCode("204").description("beer was deleted if present"))
        }
      }
    }
  }
}

private fun beerIdParam() = parameterBuilder().name("id").description("id of the beer").required(true)
private fun beerDoesNotExist() = responseBuilder().responseCode("204").description("beer does not exist")

private fun CoRouterFunctionDsl.withDocs(buildOperation: Builder.() -> Unit) {
  val opsBuilder = Builder.operationBuilder()
  buildOperation(opsBuilder)
  withAttribute(Constants.OPERATION_ATTRIBUTE, opsBuilder)
}

private fun CoRouterFunctionDsl.withDocsAt(beanClass: KClass<*>, beanMethod: KCallable<*>) {
  val opsBuilder = Builder.operationBuilder().beanClass(beanClass.java).beanMethod(beanMethod.name)
  withAttribute(Constants.OPERATION_ATTRIBUTE, opsBuilder)
}
