package de.datev.samples.kotlinbeer

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import org.intellij.lang.annotations.Language
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest
class ApiTest(private val webTestClient: WebTestClient) : DescribeSpec({
  describe("API for /beers") {
    describe("has GET /") {
      val expected = """
                      [
                        {
                          "brand": "Nestle",
                          "name": "Wasser",
                          "strength": 0
                        }
                      ]
                    """.trimIndent()

      val response = webTestClient.get().uri("/beers").exchange()

      response.expectStatus().isOk
      response.shouldHaveJsonBody(expected)
    }
  }
})

private fun WebTestClient.ResponseSpec.shouldHaveJsonBody(@Language("JSON") expected: String) {
  expectBody<String>().consumeWith {
    it.responseBody
      .shouldNotBeNull()
      .shouldEqualJson(expected)
  }
}
