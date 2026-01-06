package de.welcz.samples.kotlinbeer.helpers

import de.welcz.samples.kotlinbeer.beers.Beer
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import org.bson.types.ObjectId
import java.math.RoundingMode
import java.util.*

fun Arb.Companion.beer() = arbitrary {
  val id = Arb.objectId().bind()
  val brand = Arb.string().bind()
  val name = Arb.string().bind()
  val strength = Arb.bigDecimal(scale = 5, roundingMode = RoundingMode.HALF_EVEN).bind()
  Beer(id = id, name = name, brand = brand, strength = strength)
}

fun Arb.Companion.beerWithoutId() =
  Arb.beer().map { it.copy(id = null) }

fun Arb.Companion.objectId() = arbitrary {
  val localDate = Arb.localDate().bind()
  val date = Date.from(localDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC))
  // object id only supports 3 bytes in the counter
  val counter = Arb.int(0..16777215).bind()
  ObjectId(date, counter)
}
