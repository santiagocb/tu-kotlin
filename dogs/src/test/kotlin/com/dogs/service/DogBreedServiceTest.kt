package com.dogs.service

import com.dogs.api.DogBreedApiClient
import com.dogs.model.DogBreed
import com.dogs.model.DogBreedResponse
import com.dogs.model.DogSubBreedResponse
import com.dogs.repository.DogBreedRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
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
    fun `save should correctly transform breeds map and call saveAll`() = runBlocking {
        // Given
        val mockInput = mapOf(
            "bulldog" to listOf("english", "french"),
            "beagle" to emptyList()
        )
        val expectedOutput = listOf(
            DogBreed(breed = "bulldog", subBreed = "english,french"),
            DogBreed(breed = "beagle", subBreed = "")
        )

        val mockFlow = mockk<kotlinx.coroutines.flow.Flow<DogBreed>>()
        coEvery { mockFlow.collect(any()) } just Runs
        coEvery { dogBreedRepository.saveAll(expectedOutput) } returns mockFlow

        // When
        dogBreedService.save(mockInput)

        // Then
        coVerify { dogBreedRepository.saveAll(expectedOutput) }
    }

    @Test
    fun `getBreeds should fetch from repository and map correctly`() = runBlocking {
        // Given
        val mockData = flowOf(
            DogBreed(id = 1, breed = "bulldog", subBreed = "english,french"),
            DogBreed(id = 2, breed = "beagle", subBreed = null)
        )
        val expectedResponse = listOf(
            DogBreedResponse(breed = "bulldog", subBreeds = listOf("english", "french")),
            DogBreedResponse(breed = "beagle", subBreeds = null)
        )
        coEvery { dogBreedRepository.findAll() } returns mockData

        // When
        val result = dogBreedService.getBreeds()

        // Then
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { dogBreedRepository.findAll() }
    }

    @Test
    fun `getBreedsWithoutSubBreeds should only include breeds without subBreeds`() = runBlocking {
        // Given
        val mockData = flowOf(
            DogBreed(id = 1, breed = "bulldog", subBreed = "english,french"),
            DogBreed(id = 2, breed = "beagle", subBreed = null),
            DogBreed(id = 3, breed = "chihuahua", subBreed = ""),
        )
        val expectedResponse = listOf(
            DogBreedResponse(breed = "beagle", subBreeds = null),
            DogBreedResponse(breed = "chihuahua", subBreeds = null)
        )
        coEvery { dogBreedRepository.findAll() } returns mockData

        // When
        val result = dogBreedService.getBreedsWithoutSubBreeds().toList()

        // Then
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { dogBreedRepository.findAll() }
    }

    @Test
    fun `getSubBreedsForBreed should return subBreeds when subBreed is present`() = runBlocking {
        // Given
        val breed = "bulldog"
        val mockDogBreed = DogBreed(id = 1, breed = breed, subBreed = "english,french")
        val expectedResponse = listOf(
            DogSubBreedResponse(subBreed = "english"),
            DogSubBreedResponse(subBreed = "french")
        )

        coEvery { dogBreedRepository.findByBreed(breed) } returns mockDogBreed

        // When
        val result = dogBreedService.getSubBreedsForBreed(breed)

        // Then
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { dogBreedRepository.findByBreed(breed) }
    }


    @Test
    fun `getSubBreedsForBreed should return empty list when subBreed is null`() = runBlocking {
        // Given
        val breed = "beagle"
        val mockDogBreed = DogBreed(id = 2, breed = breed, subBreed = null)
        val expectedResponse = emptyList<DogSubBreedResponse>()
        coEvery { dogBreedRepository.findByBreed(breed) } returns mockDogBreed

        // When
        val result = dogBreedService.getSubBreedsForBreed(breed)

        // Then
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { dogBreedRepository.findByBreed(breed) } // Ensure repository is called
    }

    @Test
    fun `getSubBreedsForBreed should return null when breed is not found`() = runBlocking {
        // Given
        val breed = "unknown"
        val expectedResponse: List<DogSubBreedResponse>? = null
        coEvery { dogBreedRepository.findByBreed(breed) } returns null

        // When
        val result = dogBreedService.getSubBreedsForBreed(breed)

        // Then
        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { dogBreedRepository.findByBreed(breed) }
    }

    @Test
    fun `getBreedImage should returns image from database if present`() = runBlocking {
        // Given
        val mockedBreed = DogBreed(id = 1, breed = "bulldog", subBreed = null, image = byteArrayOf(1, 2, 3))
        coEvery { dogBreedRepository.findByBreed("bulldog") } returns mockedBreed

        // When
        val result = dogBreedService.getBreedImage("bulldog")

        // Then
        assertNotNull(result)
        assertArrayEquals(byteArrayOf(1, 2, 3), result)
        coVerify(exactly = 1) { dogBreedRepository.findByBreed("bulldog") }
        coVerify(exactly = 0) { dogBreedApiClient.fetchImageFromAPI(any()) }
    }

    @Test
    fun `getBreedImage should fetches and stores image if not in database`() = runBlocking {
        // Given
        val mockedBreed = DogBreed(id = 1, breed = "bulldog", subBreed = null, image = null)
        val imageUrl = "http://example.com/bulldog.jpg"
        val imageBytes = byteArrayOf(1, 2, 3)

        coEvery { dogBreedRepository.findByBreed("bulldog") } returns mockedBreed
        coEvery { dogBreedApiClient.fetchImageFromAPI("bulldog") } returns imageUrl
        coEvery { dogBreedApiClient.downloadImage(imageUrl) } returns imageBytes
        coEvery { dogBreedRepository.save(any()) } returns mockedBreed.copy(image = imageBytes)

        // When
        val result = dogBreedService.getBreedImage("bulldog")

        // Then
        assertNotNull(result)
        assertArrayEquals(imageBytes, result)
        coVerify { dogBreedRepository.findByBreed("bulldog") }
        coVerify { dogBreedApiClient.fetchImageFromAPI("bulldog") }
        coVerify { dogBreedApiClient.downloadImage(imageUrl) }
        coVerify { dogBreedRepository.save(mockedBreed.copy(image = imageBytes)) }
    }

    @Test
    fun `getBreedImage should throws exception for breed not found in API`() = runBlocking {
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