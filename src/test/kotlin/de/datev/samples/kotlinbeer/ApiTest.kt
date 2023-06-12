package de.datev.samples.kotlinbeer

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.flow.asFlow
import org.bson.types.ObjectId
import org.intellij.lang.annotations.Language
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest
class ApiTest(
  private val webTestClient: WebTestClient,
  @MockkBean
  private val beerRepository: BeerRepository,
) : DescribeSpec({
  describe("API for /beers") {
    it("has GET /") {
      val existingBeers = listOf(
        Beer(id = ObjectId.get(), brand = "Nestle", name = "Wasser", strength = 0.toBigDecimal()),
        Beer(id = ObjectId.get(), brand = "Nestle", name = "Nesquik", strength = 0.toBigDecimal()),
      )
      every { beerRepository.findAll() } returns existingBeers.asFlow()
      val expected = existingBeers.joinToString(prefix = "[", postfix = "]", separator = ",") { it.toJson() }

      val response = webTestClient.get().uri("/beers").exchange()

      response.expectStatus().isOk
      response.shouldHaveJsonBody(expected)
    }

    describe("has GET /{id}") {
      it("returns existing beer") {
        val id = ObjectId.get()
        val existingBeer =
          Beer(id = id, brand = "Nestle", name = "Wasser", strength = 0.toBigDecimal())
        coEvery { beerRepository.findById(id) } returns existingBeer
        val expected = existingBeer.toJson()

        val response = webTestClient.get().uri("/beers/$id").exchange()

        response.expectStatus().isOk
        response.shouldHaveJsonBody(expected)
      }

      it("returns NO CONTENT when beer does not exist") {
        val id = ObjectId.get()
        val existingBeer = null
        coEvery { beerRepository.findById(id) } returns existingBeer

        val response = webTestClient.get().uri("/beers/$id").exchange()

        response.expectStatus().isNoContent
      }
    }

    it("has POST /") {
      val toCreate = Beer(brand = "Nestle", name = "Wasser", strength = 0.toBigDecimal())
      val created = toCreate.copy(id = ObjectId.get())
      val expected = created.toJson()
      @Language("JSON") val toJson = """
        {
          "brand": "Nestle",
          "name": "Wasser",
          "strength": 0
        }
      """.trimIndent()
      coEvery { beerRepository.save(toCreate) } returns created

      val response = webTestClient
        .post()
        .uri("/beers")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(toJson)
        .exchange()

      response.expectStatus().isCreated
      response.shouldHaveJsonBody(expected)
      response.expectHeader().location("/beers/${created.id}")
    }
  }

  it("has DELETE /{id}") {
    val id = ObjectId.get()
    coJustRun { beerRepository.deleteById(any()) }

    val response = webTestClient.delete().uri("/beers/$id").exchange()

    response.expectStatus().isNoContent
    coVerify { beerRepository.deleteById(id) }
  }
})

@Language("JSON")
private fun Beer.toJson() = """
 {
   "id": "$id",
   "brand": "$brand",
   "name": "$name",
   "strength": $strength
 }
""".trimIndent()

private fun WebTestClient.ResponseSpec.shouldHaveJsonBody(
  @Language("JSON")
  expected: String
) {
  expectBody<String>().consumeWith {
    it.responseBody
      .shouldNotBeNull()
      .shouldEqualJson(expected)
  }
}
