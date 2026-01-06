package de.welcz.samples.kotlinbeer.beers

import de.welcz.samples.kotlinbeer.helpers.beerWithoutId
import de.welcz.samples.kotlinbeer.helpers.objectId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.MongoDBContainer

@DataMongoTest
class BeerRepositoryTest(
  private val underTest: BeerRepository,
) : DescribeSpec({
  describe("repository of beers") {
    it("can be queried with property-based testing") {
      checkAll(MAX_ITERATIONS, Arb.beerWithoutId()) { beer ->
        val saved = underTest.save(beer)

        val savedId = saved.id.shouldNotBeNull()
        val found = underTest.findById(savedId)
        saved.shouldBeEqualToIgnoringFields(beer, Beer::id)
        found shouldBe saved
      }
    }

    it("returns null when beer is not found") {
      checkAll(MAX_ITERATIONS, Arb.objectId()) { nonExistentId ->
        val result = underTest.findById(nonExistentId)

        result shouldBe null
      }
    }
  }
}) {
  companion object {
    private const val MAX_ITERATIONS = 10

    // see also https://blog.code-n-roll.dev/kotest-spring-testcontainers
    @ServiceConnection
    @Suppress("unused")
    private val mongo = MongoDBContainer("mongo:7.0")
  }
}

