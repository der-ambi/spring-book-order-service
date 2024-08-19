package de.bashburg.springbook.orderservice.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import jakarta.annotation.PostConstruct
import org.springframework.cloud.function.json.JacksonMapper
import org.springframework.cloud.function.json.JsonMapper
import org.springframework.context.annotation.Configuration

@Configuration
class JsonMapperConfigurer(val mapper: JsonMapper) {

    @PostConstruct
    fun extendJsonMapper() {
        (mapper as? JacksonMapper)?.configureObjectMapper {
            it.registerModule(KotlinModule.Builder().build())
        }
    }
}