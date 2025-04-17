package de.welcz.samples.kotlinbeer.helpers

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.nulls.shouldNotBeNull
import org.intellij.lang.annotations.Language
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

internal fun WebTestClient.ResponseSpec.shouldHaveJsonBody(
  @Language("JSON")
  expected: String
) {
  expectBody<String>().consumeWith {
    it.responseBody
      .shouldNotBeNull()
      .shouldEqualJson(expected)
  }
}