package de.welcz.samples.kotlinbeer.helpers

import arrow.core.raise.either
import io.kotest.assertions.fail
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.coRouter

@WebFluxTest
@Import(ErrorHandlingRouter::class)
class HandlerExtensionsKtTest(private val webTestClient: WebTestClient) : DescribeSpec({
  describe("Error Handling") {
    it("handles resource not found") {
      val response = webTestClient.get().uri("/errors/not-found").exchange()

      response.expectStatus().isNoContent
      response.expectBody().isEmpty
    }

    it("handles invalid body") {
      val response = webTestClient.get().uri("/errors/invalid-body").exchange()

      response.expectStatus().isBadRequest
      response.shouldHaveJsonBody(
        """
          {"message":"the given body is invalid"}
        """.trimIndent()
      )
    }

    it("handles invalid object id") {
      val response = webTestClient.get().uri("/errors/invalid-objectid").exchange()

      response.expectStatus().isBadRequest
      response.shouldHaveJsonBody(
        """
          {"message":"the given id is invalid"}
        """.trimIndent()
      )
    }
  }
})

class ErrorHandlingRouter {
  @Bean
  fun routes() = coRouter {
    "errors".nest {
      GET("not-found") {
        either {
          raise(ResourceNotFound)
        }.toServerResponse(::unreachableCode)
      }
      GET("invalid-body") {
        either {
          raise(InvalidBody)
        }.toServerResponse(::unreachableCode)
      }
      GET("invalid-objectid") {
        either {
          raise(InvalidObjectId)
        }.toServerResponse(::unreachableCode)
      }
    }
  }
}

fun <T> unreachableCode(@Suppress("UNUSED_PARAMETER") a: Nothing): T = fail("should not be callable")
