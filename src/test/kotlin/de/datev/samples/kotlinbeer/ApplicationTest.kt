package de.datev.samples.kotlinbeer

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment

@SpringBootTest
class ApplicationTest(private val environment: Environment?) : DescribeSpec({
  describe("Application") {
    it("starts with environment") {
      environment.shouldNotBeNull()
    }
  }
})
