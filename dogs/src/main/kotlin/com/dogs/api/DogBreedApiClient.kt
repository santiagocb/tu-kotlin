package com.dogs.api

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class DogBreedApiClient(private val webClient: WebClient) {
    suspend fun getBreeds(): Map<String, List<String>> {
        val response = webClient.get()
            .uri("/breeds/list/all")
            .retrieve()
            .bodyToMono(DogBreedApiResponse::class.java)
            .awaitSingle()

        return response.message
    }
}

data class DogBreedApiResponse(
    val message: Map<String, List<String>>
)