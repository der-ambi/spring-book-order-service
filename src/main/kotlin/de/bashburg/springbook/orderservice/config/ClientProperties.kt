package de.bashburg.springbook.orderservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "polar")
data class ClientProperties(
    val catalogServiceUri: URI
)
