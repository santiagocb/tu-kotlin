package com.dogs.database

import com.dogs.api.DogBreedApiClient
import com.dogs.service.DogBreedService
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DogBreedDatabasePopulator(
    private val dogBreedApiClient: DogBreedApiClient,
    private val dogBreedService: DogBreedService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) = runBlocking {
        val breedsInDb = dogBreedService.getBreeds().toList()

        println(">>>>> breedsInDb are ${breedsInDb.count()}}")
        if (breedsInDb.isEmpty()) {
            val breeds = dogBreedApiClient.getBreeds()
            println(breeds)
            dogBreedService.save(breeds)
        }
    }
}