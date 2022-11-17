package com.example.webclientbuildercheck.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@RestController
class RestApi(
	private val builder: WebClient.Builder,
	private val serverProperties: ReactiveWebServerApplicationContext,
	@Qualifier("WorkingMapper") objectMapper: ObjectMapper
) {
	private val client by lazy {
//		WebClient.builder()
		builder
			.clone()
			.baseUrl("http://localhost:${serverProperties.webServer.port}")
			.codecs { codecs ->
				codecs.defaultCodecs().let { codec ->
					codec.jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON))
					codec.jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
				}
			}
			.build()
	}

	@GetMapping("date")
	fun getDate() = DateAsString("2020-01-01T10:10:10.000Z")

	@GetMapping("check")
	fun getFailingDate(): Mono<DataAsOffset> = client.get().uri("date").exchangeToMono { it.bodyToMono() }

	data class DateAsString(
		val date: String
	)

	data class DataAsOffset(
		val date: OffsetDateTime
	)
}