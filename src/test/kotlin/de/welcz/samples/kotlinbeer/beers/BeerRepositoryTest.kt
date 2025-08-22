package de.welcz.samples.kotlinbeer.beers

import de.welcz.samples.kotlinbeer.helpers.beerWithoutId
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.single
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.MongoDBContainer

@DataMongoTest
class BeerRepositoryTest(
  private val underTest: BeerRepository,
) : DescribeSpec({
  describe("repository of beers") {
    it("can be queried") {
      val beer = Arb.beerWithoutId().single()
      val toSave = beer.copy()

      val saved = underTest.save(toSave)

      val savedId = saved.id.shouldNotBeNull()
      val found = underTest.findById(savedId)
      saved.shouldBeEqualToIgnoringFields(beer, Beer::id)
      found shouldBe saved
    }
  }
}) {
  companion object {
    // see also https://blog.code-n-roll.dev/kotest-spring-testcontainers
    @ServiceConnection
    @Suppress("unused")
    private val mongo = MongoDBContainer("mongo:7.0")
  }
}

