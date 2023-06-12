package de.datev.samples.kotlinbeer.config

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.datev.samples.kotlinbeer.beers.Beer
import de.datev.samples.kotlinbeer.beers.PartialBeer
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration

@Configuration
// XXX: necessary for native image support
@RegisterReflectionForBinding(PartialBeer::class, Beer::class, ToStringSerializer::class)
class SerializationConfiguration
