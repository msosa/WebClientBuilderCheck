package com.example.webclientbuildercheck.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.http.MediaType
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@RestController
class RestApi(
	private val builder: WebClient.Builder,
	private val serverProperties: ReactiveWebServerApplicationContext,
	@Qualifier("WorkingMapper") workingMapper: ObjectMapper
) {
	private val logger = LoggerFactory.getLogger(RestApi::class.java)

	private val client by lazy {
		val exchangeStrategies = ExchangeStrategies.builder()
			.codecs { configure(it, workingMapper) }

		builder
			.clone()
			.baseUrl("http://localhost:${serverProperties.webServer.port}")
			.exchangeStrategies(exchangeStrategies.build())
//			Note: It seems using codecs directly on the builder like below things works properly
//			but when using exchangeStrategies it does not work as intended
//			.codecs { configure(it, workingMapper) }
			.build()
	}

	private fun configure(clientCodecConfigurer: ClientCodecConfigurer, objectMapper: ObjectMapper) {
		clientCodecConfigurer.defaultCodecs().let { codec ->
			codec.jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON))
			codec.jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
		}
	}

	@GetMapping("date")
	fun getDate() = DateAsString("2020-01-01T10:10:10.000Z")

	@GetMapping("check")
	fun getFailingDate(): Mono<String> {
		return client.get()
			.uri("date").exchangeToMono { it.bodyToMono<DataAsOffset>() }
			.map { it.date.toString() }
			.doOnError { logger.error("Failing on client call") }
	}

	data class DateAsString(
		val date: String
	)

	data class DataAsOffset(
		val date: OffsetDateTime
	)
}