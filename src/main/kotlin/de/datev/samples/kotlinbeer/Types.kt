package de.datev.samples.kotlinbeer

import java.math.BigDecimal

data class Beer(
  val brand: String,
  val name: String,
  val strength: BigDecimal,
)
