package com.dogs.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("dogbreed")
data class DogBreed(
    @Id val id: Long? = null,
    val breed: String,
    val subBreed: String?
)
