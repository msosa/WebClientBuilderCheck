package com.example.webclientbuildercheck

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@SpringBootApplication
class WebClientBuilderCheckApplication {
	@Bean
	@Primary
	fun defaultObjectMapper(): ObjectMapper = JsonMapper.builder()
		.addModules(KotlinModule.Builder().build())
		.build()

	@Bean("WorkingMapper")
	fun otherMapper(): ObjectMapper = JsonMapper.builder()
		.addModules(KotlinModule.Builder().build(), JavaTimeModule())
		.build()
}

fun main(args: Array<String>) {
	runApplication<WebClientBuilderCheckApplication>(*args)
}
