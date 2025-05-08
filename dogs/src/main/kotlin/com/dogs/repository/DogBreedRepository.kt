package com.dogs.repository

import com.dogs.model.DogBreed
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DogBreedRepository : CoroutineCrudRepository<DogBreed, Long>