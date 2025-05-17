package com.dogs.service

import com.dogs.api.DogBreedApiClient
import com.dogs.model.DogBreed
import com.dogs.model.DogBreedResponse
import com.dogs.model.DogSubBreedResponse
import com.dogs.repository.DogBreedRepository
import kotlinx.coroutines.flow.*
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException

@Service
class DogBreedService(
    private val dogBreedRepository: DogBreedRepository,
    private val dogBreedApiClientDog: DogBreedApiClient
) {

    suspend fun save(breeds: Map<String, List<String>>) {
        val dogBreeds = breeds.map { (breed, subBreeds) ->
            DogBreed(breed = breed, subBreed = subBreeds.joinToString(","))
        }
        dogBreedRepository.saveAll(dogBreeds).collect()
    }

    @Cacheable("breeds")
    suspend fun getBreeds(): List<DogBreedResponse> {
        return dogBreedRepository.findAll().map { dogBreed ->
            DogBreedResponse(
                breed = dogBreed.breed,
                subBreeds = dogBreed.subBreed?.split(",")
            )
        }.toList()
    }

    suspend fun getAllSubBreeds(): List<DogSubBreedResponse> {
        return dogBreedRepository.findAll().toList()
            .filter { dogBreed -> !dogBreed.subBreed.isNullOrBlank() }
            .flatMap { dogBreed ->
                dogBreed.subBreed!!.split(",")
                    .map {
                        DogSubBreedResponse(subBreed = it)
                    }
            }
            .distinct()
    }

    suspend fun getBreedsWithoutSubBreeds(): Flow<DogBreedResponse> {
        return dogBreedRepository.findAll()
            .filter { dogBreed -> dogBreed.subBreed.isNullOrBlank() } // Filter breeds with no sub-breeds
            .map { dogBreed ->
                DogBreedResponse(
                    breed = dogBreed.breed,
                    subBreeds = null
                )
            }
    }

    suspend fun getSubBreedsForBreed(breed: String): List<DogSubBreedResponse>? {
        val dogBreed = dogBreedRepository.findByBreed(breed)

        return if (dogBreed != null) {
            if (dogBreed.subBreed == null) listOf()
            else dogBreed.subBreed.split(",").map { DogSubBreedResponse(subBreed = it) }
        } else {
            null
        }
    }

    suspend fun getBreedImage(breed: String): ByteArray {

        val dogBreed = dogBreedRepository.findByBreed(breed)

        if (dogBreed?.image != null) {
            return dogBreed.image
        }

        try {
            val imageUrl = dogBreedApiClientDog.fetchImageFromAPI(breed)
            val imageBytes = dogBreedApiClientDog.downloadImage(imageUrl)

            dogBreed?.let {
                val updatedBreed = it.copy(image = imageBytes)
                dogBreedRepository.save(updatedBreed)
            }

            return imageBytes

        } catch (e: WebClientResponseException.NotFound) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Breed not found: $breed")
        } catch (e: WebClientResponseException) {
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error while calling external API: ${e.responseBodyAsString}",
                e
            )
        } catch (e: WebClientRequestException) {
            throw ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Unable to connect to external API for breed: $breed",
                e
            )
        }
    }
}
