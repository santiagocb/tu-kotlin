package com.dogs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@EnableCaching
@SpringBootApplication
class Application {
	@Bean
	fun webClient(builder: WebClient.Builder): WebClient = builder.baseUrl("https://dog.ceo/api").build()
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}