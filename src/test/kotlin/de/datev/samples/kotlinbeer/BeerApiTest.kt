package de.datev.samples.kotlinbeer

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.*
import kotlinx.coroutines.flow.asFlow
import org.bson.types.ObjectId
import org.intellij.lang.annotations.Language
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest
class BeerApiTest(
  private val webTestClient: WebTestClient,
  @MockkBean private val repository: BeerRepository
) : DescribeSpec({
  describe("Beer API") {
    it("has GET /beers") {
      val existingBeers = listOf(
        Beer(id = ObjectId.get(), brand = "Plörrbräu", name = "Plörr Lau", strength = 0.000010.toBigDecimal()),
        Beer(id = ObjectId.get(), brand = "Meister", name = "Vollbier", strength = 6.8.toBigDecimal()),
        Beer(id = ObjectId.get(), brand = "Schanzenbräu", name = "Rotbier", strength = 5.toBigDecimal()),
      )
      every { repository.findAll() } returns existingBeers.asFlow()
      val expectedJson = existingBeers.joinToString(prefix = "[", postfix = "]") { it.toJson() }

      val response = webTestClient.get().uri("/beers").exchange()

      response.expectStatus().isOk
      response.shouldHaveJsonBody(expectedJson)
    }

    describe("has GET /beers/{id}") {
      it("returns existing beer") {
        val id = ObjectId.get()
        val existingBeer = Beer(id = id, brand = "Plörrbräu", name = "Plörr Lau", strength = 0.000010.toBigDecimal())

        coEvery { repository.findById(id) } returns existingBeer
        val expectedJson = existingBeer.toJson()

        val response = webTestClient.get().uri("/beers/$id").exchange()

        response.expectStatus().isOk
        response.shouldHaveJsonBody(expectedJson)
      }

      it("returns no content, when beer doesn't exist") {
        val id = ObjectId.get()

        coEvery { repository.findById(id) } returns null

        val response = webTestClient.get().uri("/beers/$id").exchange()

        response.expectStatus().isNoContent
        response.expectBody().isEmpty
      }
    }

    it("has POST /beers") {
      @Language("json") val beerToCreateAsJson = """
        {
          "brand": "PlörrBräu",
          "name": "Plörr Lau",
          "strength": 1
        }
      """.trimIndent()
      val beerToCreate = Beer(brand = "PlörrBräu", name = "Plörr Lau", strength = 1.toBigDecimal())
      val createdBeer = beerToCreate.copy(id = ObjectId.get())
      coEvery { repository.save(beerToCreate) } returns createdBeer
      val expectedJson = createdBeer.toJson()

      val response =
        webTestClient.post()
          .uri("/beers")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(beerToCreateAsJson)
          .exchange()

      response.expectStatus().isCreated
      response.expectHeader().location("/beers/${createdBeer.id}")
      response.shouldHaveJsonBody(expectedJson)
    }

    it("has DELETE /beers/{id}") {
      val id = ObjectId.get()
      coJustRun { repository.deleteById(any()) }

      val response = webTestClient.delete().uri("/beers/$id").exchange()

      response.expectStatus().isNoContent
      coVerify { repository.deleteById(id) }
    }

    describe("has PUT /beers/{id}") {
      it("updates beer if it exists") {
        val id = ObjectId.get()
        @Language("json") val beerToUpdateAsJson = """
        {
          "brand": "PlörrBräu",
          "name": "Plörr Lau",
          "strength": 1
        }
      """.trimIndent()
        val beerToUpdate = Beer(brand = "PlörrBräu", name = "Plörr Lau", strength = 1.toBigDecimal())
        val updatedBeer = beerToUpdate.copy(id = id)
        val existingBeer = beerToUpdate.copy(id = id, strength = 9.toBigDecimal())
        coEvery { repository.save(updatedBeer) } returns updatedBeer
        coEvery { repository.findById(id) } returns existingBeer

        val response = webTestClient
          .put()
          .uri("/beers/$id")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(beerToUpdateAsJson)
          .exchange()

        response.expectStatus().isOk
        response.shouldHaveJsonBody(updatedBeer.toJson())
      }
      it("does nothing if beer doesnt exists") {
        val id = ObjectId.get()
        @Language("json") val beerToUpdateAsJson = """
        {
          "brand": "PlörrBräu",
          "name": "Plörr Lau",
          "strength": 1
        }
      """.trimIndent()
        coEvery { repository.findById(id) } returns null

        val response = webTestClient
          .put()
          .uri("/beers/$id")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(beerToUpdateAsJson)
          .exchange()

        response.expectStatus().isNoContent
        response.expectBody().isEmpty
      }
    }
  }

})

@Language("json")
private fun Beer.toJson() = """
        {
          "id": "$id",
          "brand": "$brand",
          "name": "$name",
          "strength": $strength
        }
      """.trimIndent()

private fun WebTestClient.ResponseSpec.shouldHaveJsonBody(
  @Language("json") expectedJson: String
) {
  expectBody<String>().consumeWith {
    it.responseBody.shouldNotBeNull().shouldEqualJson(expectedJson)
  }
}
