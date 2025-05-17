package com.dogs.controller

import com.dogs.model.DogBreedResponse
import com.dogs.service.DogBreedService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Boot app with random port for testing
class DogBreedControllerIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient // Injected WebTestClient

    @Autowired
    private lateinit var dogBreedService: DogBreedService // Injected mock service

    @TestConfiguration
    class TestConfig {

        @Bean
        fun dogBreedService(): DogBreedService {
            return mockk() // Relaxed mock allows unmocked default behavior
        }
    }

    @Test
    fun `GET v1 breeds should return all breeds`() = runBlocking {
        // Arrange
        val mockResponse = listOf(
            DogBreedResponse(breed = "bulldog", subBreeds = listOf("english", "french")),
            DogBreedResponse(breed = "beagle", subBreeds = null)
        )
        coEvery { dogBreedService.getBreeds() } returns mockResponse

        // Act & Assert
       val result = webTestClient.get().uri("/v1/breeds") // Call the endpoint
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk // Assert HTTP 200
            .expectBodyList(DogBreedResponse::class.java)
            .returnResult()
            .responseBody ?: emptyList()

        Assertions.assertEquals(mockResponse, result)

        coVerify(exactly = 1) { dogBreedService.getBreeds() } // Verify the service is called once
    }


//    @Test
//    fun `GET v1/subBreeds should return all sub-breeds`() = runBlocking {
//        // Arrange
//        val mockResponse = listOf(
//            DogSubBreedResponse(subBreed = "english"),
//            DogSubBreedResponse(subBreed = "french")
//        )
//        coEvery { dogBreedService.getAllSubBreeds() } returns mockResponse
//
//        // Act & Assert
//        webTestClient.get().uri("/v1/subBreeds")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk // Assert HTTP 200
//            .expectBodyList(DogSubBreedResponse::class.java)
//            .isEqualTo(mockResponse)
//
//        coVerify(exactly = 1) { dogBreedService.getAllSubBreeds() }
//    }
//
//    @Test
//    fun `GET v1/withoutSubBreeds should return breeds without sub-breeds`() = runBlocking {
//        // Arrange
//        val mockResponse = flowOf(
//            DogBreedResponse(breed = "beagle", subBreeds = null),
//            DogBreedResponse(breed = "chihuahua", subBreeds = null)
//        )
//        coEvery { dogBreedService.getBreedsWithoutSubBreeds() } returns mockResponse
//
//        // Act & Assert
//        webTestClient.get().uri("/v1/withoutSubBreeds")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk
//            .expectBodyList(DogBreedResponse::class.java)
//            .consumeWith { body ->
//                val responseBody = body.responseBody!!
//                assertEquals(2, responseBody.size)
//                assertEquals("beagle", responseBody[0].breed)
//                assertEquals(null, responseBody[0].subBreeds)
//            }
//
//        coVerify(exactly = 1) { dogBreedService.getBreedsWithoutSubBreeds() }
//    }
//
//    @Test
//    fun `GET v1/breeds/{breed}/subBreeds should return sub-breeds for a specific breed`() = runBlocking {
//        // Arrange
//        val breed = "bulldog"
//        val mockResponse = listOf(
//            DogSubBreedResponse(subBreed = "english"),
//            DogSubBreedResponse(subBreed = "french")
//        )
//        coEvery { dogBreedService.getSubBreedsForBreed(breed) } returns mockResponse
//
//        // Act & Assert
//        webTestClient.get().uri("/v1/breeds/$breed/subBreeds")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk
//            .expectBodyList(DogSubBreedResponse::class.java)
//            .isEqualTo(mockResponse)
//
//        coVerify(exactly = 1) { dogBreedService.getSubBreedsForBreed(breed) }
//    }
//
//    @Test
//    fun `GET v1/breeds/{breed}/image should return the image`() = runBlocking {
//        // Arrange
//        val breed = "beagle"
//        val mockImage = byteArrayOf(1, 2, 3, 4) // Mocked image data
//        coEvery { dogBreedService.getBreedImage(breed) } returns mockImage
//
//        // Act & Assert
//        webTestClient.get().uri("/v1/breeds/$breed/image")
//            .accept(MediaType.IMAGE_JPEG)
//            .exchange()
//            .expectStatus().isOk
//            .expectHeader().contentType(MediaType.IMAGE_JPEG)
//            .expectBody()
//            .consumeWith {
//                val responseBody = it.responseBody
//                assertEquals(mockImage.toList(), responseBody?.toList()) // Verify binary data matches mock
//            }
//
//        coVerify(exactly = 1) { dogBreedService.getBreedImage(breed) }
//    }
}