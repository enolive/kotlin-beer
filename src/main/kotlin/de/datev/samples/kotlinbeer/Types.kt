package de.datev.samples.kotlinbeer

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "Beer")
data class Beer(
  @Id
  @JsonSerialize(using = ToStringSerializer::class)
  val id: ObjectId? = null,
  val brand: String,
  val name: String,
  val strength: BigDecimal,
)
