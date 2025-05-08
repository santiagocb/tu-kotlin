package com.dogs.service

import com.dogs.model.DogBreed
import com.dogs.repository.DogBreedRepository
import kotlinx.coroutines.flow.toList
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class DogBreedService(private val dogBreedRepository: DogBreedRepository) {

    suspend fun save(breeds: Map<String, List<String>>) {
        val dogBreeds = breeds.map { (breed, subBreeds) ->
            DogBreed(breed = breed, subBreed = subBreeds.joinToString(","), image = null)
        }
        dogBreedRepository.saveAll(dogBreeds)
    }

    @Cacheable("breeds")
    suspend fun getBreeds(): List<DogBreed> {
        return dogBreedRepository.findAll().toList()
    }
}