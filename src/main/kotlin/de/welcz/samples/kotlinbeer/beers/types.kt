package de.welcz.samples.kotlinbeer.beers

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

interface HasId {
  val id: ObjectId?
}

@Document(collection = "Beer")
data class Beer(
  @Id
  @JsonSerialize(using = ToStringSerializer::class)
  override val id: ObjectId? = null,
  val brand: String,
  val name: String,
  val strength: BigDecimal,
) : HasId

data class PartialBeer(
  val brand: String,
  val name: String,
  val strength: BigDecimal,
)
