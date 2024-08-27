package de.bashburg.springbook.orderservice.order.web

import de.bashburg.springbook.orderservice.order.domain.Order
import de.bashburg.springbook.orderservice.order.domain.OrderService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping
    fun getAllOrders(@AuthenticationPrincipal jwt: Jwt): Flux<Order> = orderService.getAllOrders(jwt.subject)

    @PostMapping
    fun submitOrder(@RequestBody @Valid orderRequest: OrderRequest): Mono<Order> =
        orderService.submitOrder(orderRequest.isbn, orderRequest.quantity)
}