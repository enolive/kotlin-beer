package de.welcz.samples.kotlinbeer.config

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import de.welcz.samples.kotlinbeer.beers.Beer
import de.welcz.samples.kotlinbeer.beers.PartialBeer
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration

@Configuration
// XXX: necessary for native image support
@RegisterReflectionForBinding(PartialBeer::class, Beer::class, ToStringSerializer::class)
class SerializationConfiguration
