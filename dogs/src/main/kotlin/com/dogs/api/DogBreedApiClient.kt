package com.dogs.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import java.net.URL

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

    suspend fun fetchImageFromAPI(breed: String): String {
        val response = webClient
            .get()
            .uri("https://dog.ceo/api/breed/$breed/images")
            .retrieve()
            .bodyToMono(DogImageApiResponse::class.java)
            .awaitSingle()
        if (response.message.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No images available for breed: $breed")
        }
        return response.message.first()
    }

    suspend fun downloadImage(imageUrl: String): ByteArray {
        return withContext(Dispatchers.IO) {
            URL(imageUrl).readBytes()
        }
    }
}

data class DogBreedApiResponse(
    val message: Map<String, List<String>>
)
