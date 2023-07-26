package de.datev.samples.kotlinbeer

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.asFlow
import org.bson.types.ObjectId
import org.intellij.lang.annotations.Language
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest
@Import(Router::class, BeerHandler::class)
class BeerAPITest(testClient: WebTestClient, @MockkBean private val repository: BeerRepository) : DescribeSpec({
  describe("GET All Beers") {
    it("returns all beers") {
      val existingBeers = listOf<Beer>(
        Beer(id = ObjectId.get(), brand = "Schanzenbräu", name = "Rot", strength = 4.9.toBigDecimal()),
        Beer(id = ObjectId.get(), brand = "Orca Bräu", name = "Timeless IPA", strength = 5.5.toBigDecimal())
      )
      val expectedJson = existingBeers.joinToString(prefix = "[", postfix = "]") { it.expectedJson() }
      every { repository.findAll() }.returns(existingBeers.asFlow())
      val response = testClient.get().uri("/beers").exchange()
      response.expectStatus().isOk
      response.shouldHaveJsonBody(expectedJson)
    }
  }
  describe("POST a Beer") {
    it("creates a beer") {
      @Language("json") val jsonBody = """
        {
          "brand": "Schanzenbräu2",
          "name": "Schanze Rot",
          "strength": 4.9
        }
      """.trimIndent()
      val beerToCreate = Beer(brand = "Schanzenbräu2", name = "Schanze Rot", strength = 4.9.toBigDecimal())
      val createdBeer = beerToCreate.copy(id = ObjectId.get())
      coEvery { repository.save(beerToCreate) } returns createdBeer
      val expectedBeerJson = createdBeer.expectedJson()
      val response =
        testClient.post().uri("/beers").contentType(MediaType.APPLICATION_JSON).bodyValue(jsonBody).exchange()
      response.expectStatus().isCreated
      response.shouldHaveJsonBody(expectedBeerJson)
      response.expectHeader().location("/beers/${createdBeer.id}")
    }
  }
})

private fun WebTestClient.ResponseSpec.shouldHaveJsonBody(
  @Language("json") expectedJson: String
) {
  expectBody<String>().consumeWith({
    it.responseBody.shouldNotBeNull().shouldEqualJson(expectedJson.trimIndent())
  })
}

@Language("json")
private fun Beer.expectedJson(): String {
  return """{
  "id": "$id",
  "brand": "$brand",
  "name": "$name",
  "strength": $strength
}"""
}

