package com.dogs.repository

import com.dogs.model.DogBreed
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DogBreedRepository : CoroutineCrudRepository<DogBreed, Long> {

    @Query("SELECT * FROM dogbreed WHERE breed = :breed")
    suspend fun findByBreed(breed: String): DogBreed?
}