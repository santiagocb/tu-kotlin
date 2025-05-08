package com.dogs.controller

import com.dogs.service.DogBreedService
import com.dogs.model.DogBreedResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Dog Breed APIs", description = "Breed APIs for demo purpose")
@RestController
@RequestMapping("v1/breeds")
class DogBreedController(private val dogBreedService: DogBreedService) {

    @GetMapping
    suspend fun getAllDogBreeds(): List<DogBreedResponse> =
        dogBreedService.getBreeds().map { dogBreed ->
            DogBreedResponse(dogBreed.breed, dogBreed.subBreed?.split(","))
        }
}