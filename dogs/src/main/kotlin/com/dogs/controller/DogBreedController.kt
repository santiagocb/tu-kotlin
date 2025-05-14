package com.dogs.controller

import com.dogs.service.DogBreedService
import com.dogs.model.DogBreedResponse
import com.dogs.model.DogSubBreedResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Dog Breed APIs", description = "Breed APIs for demo purpose")
@RestController
@RequestMapping("v1")
class DogBreedController(private val dogBreedService: DogBreedService) {

    @GetMapping("/breeds")
    suspend fun getAllDogBreeds(): List<DogBreedResponse> =
        dogBreedService.getBreeds()

    @GetMapping("/subBreeds")
    suspend fun getAllSubBreeds(): List<DogSubBreedResponse> =
        dogBreedService.getAllSubBreeds()

    @GetMapping("/withoutSubBreeds")
    suspend fun getBreedsWithoutSubBreeds(): Flow<DogBreedResponse> =
        dogBreedService.getBreedsWithoutSubBreeds()

    @GetMapping("/breeds/{breed}/subBreeds")
    suspend fun getSubBreedsForBreed(@PathVariable breed: String): List<DogSubBreedResponse>? =
        dogBreedService.getSubBreedsForBreed(breed)

    @GetMapping("/breeds/{breed}/image", produces = [MediaType.IMAGE_JPEG_VALUE])
    suspend fun getBreedImage(@PathVariable breed: String): ByteArray {
        return dogBreedService.getBreedImage(breed)
    }
}