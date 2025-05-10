package com.dogs.service

import com.dogs.model.DogBreed
import com.dogs.model.DogBreedResponse
import com.dogs.repository.DogBreedRepository
import kotlinx.coroutines.flow.*
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class DogBreedService(private val dogBreedRepository: DogBreedRepository) {

    suspend fun save(breeds: Map<String, List<String>>) {
        val dogBreeds = breeds.map { (breed, subBreeds) ->
            DogBreed(breed = breed, subBreed = subBreeds.joinToString(","))
        }
        println("Saving ${dogBreeds.size} dog breeds in database")
        dogBreedRepository.saveAll(dogBreeds).collect()
    }

    suspend fun getBreeds(): Flow<DogBreedResponse> {
        return dogBreedRepository.findAll().map { dogBreed ->
            DogBreedResponse(
                breed = dogBreed.breed,
                subBreeds = dogBreed.subBreed?.split(",")
            )
        }
    }
}