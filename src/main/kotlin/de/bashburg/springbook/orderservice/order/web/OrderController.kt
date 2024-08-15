package de.bashburg.springbook.orderservice.order.web

import de.bashburg.springbook.orderservice.order.domain.Order
import de.bashburg.springbook.orderservice.order.domain.OrderService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping
    fun getAllOrders(): Flux<Order> = orderService.getAllOrders()

    @PostMapping
    fun submitOrder(@RequestBody @Valid orderRequest: OrderRequest): Mono<Order> =
        orderService.submitOrder(orderRequest.isbn, orderRequest.quantity)
}