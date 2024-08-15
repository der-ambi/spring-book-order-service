package de.bashburg.springbook.orderservice.order.domain

import de.bashburg.springbook.orderservice.order.domain.OrderStatus.REJECTED
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(private val orderRepository: OrderRepository) {

    fun getAllOrders(): Flux<Order> = orderRepository.findAll()

    fun submitOrder(bookIsbn: String, quantity: Int): Mono<Order> =
        Mono.just(buildRejectedOrder(bookIsbn, quantity)).flatMap(orderRepository::save)

    companion object {
        fun buildRejectedOrder(bookIsbn: String, quantity: Int): Order =
            Order.of(bookIsbn, null, null, quantity, REJECTED)
    }
}