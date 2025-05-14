package com.dogs.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DogSubBreedResponse(
    val subBreed: String
)