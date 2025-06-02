package de.welcz.samples.kotlinbeer.beers

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import io.swagger.v3.oas.annotations.media.Schema
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
  @Schema(description = "ID of the beer", implementation = String::class)
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
