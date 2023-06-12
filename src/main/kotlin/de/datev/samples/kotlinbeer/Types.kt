package de.datev.samples.kotlinbeer

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document
data class Beer(
  @Id
  val id: ObjectId? = null,
  val brand: String,
  val name: String,
  val strength: BigDecimal,
)
