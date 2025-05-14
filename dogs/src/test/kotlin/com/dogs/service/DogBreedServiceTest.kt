package com.dogs.service

import com.dogs.api.DogBreedApiClient
import com.dogs.model.DogBreed
import com.dogs.repository.DogBreedRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException

class DogBreedServiceTest {

    private lateinit var dogBreedRepository: DogBreedRepository
    private lateinit var dogBreedApiClient: DogBreedApiClient
    private lateinit var dogBreedService: DogBreedService

    @BeforeEach
    fun setup() {
        dogBreedRepository = mockk()
        dogBreedApiClient = mockk()
        dogBreedService = DogBreedService(dogBreedRepository, dogBreedApiClient)
    }

    @Test
    fun `example test`() = runBlocking {
        val result = true
        assertEquals(true, result)
    }

    @Test
    fun `getBreedImage - returns image from database if present`() = runBlocking {
        // Arrange
        val mockedBreed = DogBreed(id = 1, breed = "bulldog", subBreed = null, image = byteArrayOf(1, 2, 3))
        coEvery { dogBreedRepository.findByBreed("bulldog") } returns mockedBreed

        // Act
        val result = dogBreedService.getBreedImage("bulldog")

        // Assert
        assertNotNull(result)
        assertArrayEquals(byteArrayOf(1, 2, 3), result) // Verify the correct image is returned
        coVerify(exactly = 1) { dogBreedRepository.findByBreed("bulldog") } // Ensures repo method is called
        coVerify(exactly = 0) { dogBreedApiClient.fetchImageFromAPI(any()) } // Ensures API is not called
    }

    @Test
    fun `getBreedImage - fetches and stores image if not in database`() = runBlocking {
        // Arrange
        val mockedBreed = DogBreed(id = 1, breed = "bulldog", subBreed = null, image = null)
        val imageUrl = "http://example.com/bulldog.jpg"
        val imageBytes = byteArrayOf(1, 2, 3)

        coEvery { dogBreedRepository.findByBreed("bulldog") } returns mockedBreed
        coEvery { dogBreedApiClient.fetchImageFromAPI("bulldog") } returns imageUrl
        coEvery { dogBreedApiClient.downloadImage(imageUrl) } returns imageBytes
        coEvery { dogBreedRepository.save(any()) } returns mockedBreed.copy(image = imageBytes)

        // Act
        val result = dogBreedService.getBreedImage("bulldog")

        // Assert
        assertNotNull(result)
        assertArrayEquals(imageBytes, result)
        coVerify { dogBreedRepository.findByBreed("bulldog") }
        coVerify { dogBreedApiClient.fetchImageFromAPI("bulldog") }
        coVerify { dogBreedApiClient.downloadImage(imageUrl) }
        coVerify { dogBreedRepository.save(mockedBreed.copy(image = imageBytes)) }
    }

    @Test
    fun `getBreedImage - throws exception for breed not found in API`() = runBlocking {
        // Arrange
        coEvery { dogBreedRepository.findByBreed("unknown") } returns null
        coEvery { dogBreedApiClient.fetchImageFromAPI("unknown") } throws WebClientResponseException.create(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            HttpHeaders.EMPTY,
            ByteArray(0),
            null
        )

        // Act & Assert
        val exception = assertThrows(ResponseStatusException::class.java) {
            runBlocking { dogBreedService.getBreedImage("unknown") }
        }

        // Verify exception
        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("Breed not found: unknown", exception.reason)
        coVerify(exactly = 1) { dogBreedApiClient.fetchImageFromAPI("unknown") }
    }
}