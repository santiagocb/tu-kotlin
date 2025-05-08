package com.dogs.database

import com.dogs.api.DogBreedApiClient
import com.dogs.service.DogBreedService
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class DogBreedDatabasePopulator(
    private val dogBreedApiClient: DogBreedApiClient,
    private val dogBreedService: DogBreedService
) {
    @PostConstruct
    fun initializeDatabase() = runBlocking {
        val breedsInDb = dogBreedService.getBreeds()
        if (breedsInDb.isEmpty()) {
            val breeds = dogBreedApiClient.getBreeds()
            println(breeds)
            dogBreedService.save(breeds)
        }
    }
}