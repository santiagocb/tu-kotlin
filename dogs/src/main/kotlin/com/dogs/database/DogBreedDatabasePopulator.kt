package com.dogs.database

import com.dogs.api.DogBreedApiClient
import com.dogs.service.DogBreedService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("default") // Only activate this bean in non-test environments
class DogBreedDatabasePopulator(
    private val dogBreedApiClient: DogBreedApiClient,
    private val dogBreedService: DogBreedService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) = runBlocking {
        val breedsInDb = dogBreedService.getBreeds()

        if (breedsInDb.isEmpty()) {
            val breeds = dogBreedApiClient.getBreeds()
            dogBreedService.save(breeds)
        }
    }
}