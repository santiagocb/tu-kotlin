package com.dogs.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DogBreedResponse(
    val breed: String,
    val subBreeds: List<String>?
)