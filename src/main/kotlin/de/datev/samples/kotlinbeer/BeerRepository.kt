package de.datev.samples.kotlinbeer

import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface BeerRepository : CoroutineCrudRepository<Beer, ObjectId>
