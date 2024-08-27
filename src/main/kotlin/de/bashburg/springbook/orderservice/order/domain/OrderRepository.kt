package de.bashburg.springbook.orderservice.order.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrderRepository : ReactiveCrudRepository<Order, Long> {
    fun findAllByCreatedBy(userId: String): Flux<Order>
}