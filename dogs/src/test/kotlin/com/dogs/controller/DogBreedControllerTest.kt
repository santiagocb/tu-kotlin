package com.dogs.controller

import com.dogs.model.DogBreedResponse
import com.dogs.model.DogSubBreedResponse
import com.dogs.service.DogBreedService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = ["spring.profiles.active=test"])
class DogBreedControllerIntegrationTest {

    /*
        Learning: MockK and @MockBean (a Spring Boot feature designed for Mockito) can not live together.
        By default, Spring's @MockBean uses Mockito as the mock framework, meaning it does not integrate directly with MockK.
        In this case @MockBean is used. MockK is used for testing the service.
     */

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var dogBreedService: DogBreedService

    @Test
    fun `GET v1 breeds should return all breeds`() = runBlocking {
        // Given
        val mockDogBreedResponse = listOf(
            DogBreedResponse(breed = "bulldog", subBreeds = listOf("english", "french")),
            DogBreedResponse(breed = "beagle", subBreeds = null)
        )
        Mockito.`when`(runBlocking { dogBreedService.getBreeds() })
            .thenReturn(mockDogBreedResponse)

        // When
        val result = webTestClient.get()
            .uri("/v1/breeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(DogBreedResponse::class.java)
            .returnResult()
            .responseBody ?: emptyList()

        // Then
        assertEquals(
            mockDogBreedResponse,
            result
        )
    }

    @Test
    fun `GET v1 subBreeds should return all sub-breeds`() = runBlocking {
        // Given
        val mockDogSubreedResponse = listOf(
            DogSubBreedResponse(subBreed = "english"),
            DogSubBreedResponse(subBreed = "french")
        )
        Mockito.`when`(runBlocking { dogBreedService.getAllSubBreeds() })
            .thenReturn(mockDogSubreedResponse)

        // When
        val result = webTestClient.get().uri("/v1/subBreeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk // Assert HTTP 200
            .expectBodyList(DogSubBreedResponse::class.java)
            .returnResult()
            .responseBody ?: emptyList()

        // Then
        assertEquals(
            mockDogSubreedResponse,
            result
        )
    }

    @Test
    fun `GET v1 withoutSubBreeds should return breeds without sub-breeds`() = runBlocking {
        // Given
        val mockResponse = flowOf(
            DogBreedResponse(breed = "beagle", subBreeds = null),
            DogBreedResponse(breed = "chihuahua", subBreeds = null)
        )
        Mockito.`when`(runBlocking { dogBreedService.getBreedsWithoutSubBreeds() })
            .thenReturn(mockResponse)

        // When
        val result = webTestClient.get().uri("/v1/withoutSubBreeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(DogBreedResponse::class.java)
            .returnResult()
            .responseBody ?: emptyList()

        // Then
        assertEquals(
            mockResponse.toList(),
            result
        )
    }

    @Test
    fun `GET v1 breeds {breed} subBreeds should return sub-breeds for a specific breed`() = runBlocking {
        // Given
        val breed = "bulldog"
        val mockResponse = listOf(
            DogSubBreedResponse(subBreed = "english"),
            DogSubBreedResponse(subBreed = "french")
        )
        Mockito.`when`(runBlocking { dogBreedService.getSubBreedsForBreed(breed)  })
            .thenReturn(mockResponse)

        // When
        val result = webTestClient.get().uri("/v1/breeds/$breed/subBreeds")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(DogSubBreedResponse::class.java)
            .returnResult()
            .responseBody ?: emptyList()

        // Then
        assertEquals(
            mockResponse.toList(),
            result
        )
    }

    @Test
    fun `GET v1 breeds {breed} image should return the image`(): Unit = runBlocking {
        // Given
        val breed = "beagle"
        val mockImage = byteArrayOf(1, 2, 3, 4)
        Mockito.`when`(runBlocking { dogBreedService.getBreedImage(breed)  })
            .thenReturn(mockImage)

        // When
        webTestClient.get().uri("/v1/breeds/$breed/image")
            .accept(MediaType.IMAGE_JPEG)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.IMAGE_JPEG)
            .expectBody()
            .consumeWith {
                // Then
                val responseBody = it.responseBody
                assertEquals(mockImage.toList(), responseBody?.toList())
            }
    }
}