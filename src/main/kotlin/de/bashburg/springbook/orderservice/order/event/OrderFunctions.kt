package de.bashburg.springbook.orderservice.order.event

import de.bashburg.springbook.orderservice.order.domain.OrderService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux

private val logger = KotlinLogging.logger {}

@Configuration
class OrderFunctions {

    @Bean
    fun dispatchOrder(orderService: OrderService): (Flux<OrderDispatchedMessage>) -> Unit = { flux ->
        orderService.consumeOrderDispatchedEvent(flux)
            .doOnNext { order -> logger.info { "The order with id ${order.id} has been dispatched" } }
            .subscribe()
    }
}