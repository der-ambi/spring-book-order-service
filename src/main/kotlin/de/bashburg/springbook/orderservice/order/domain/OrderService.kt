package de.bashburg.springbook.orderservice.order.domain

import de.bashburg.springbook.orderservice.book.Book
import de.bashburg.springbook.orderservice.book.BookClient
import de.bashburg.springbook.orderservice.order.domain.OrderStatus.ACCEPTED
import de.bashburg.springbook.orderservice.order.domain.OrderStatus.REJECTED
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val bookClient: BookClient
) {

    fun getAllOrders(): Flux<Order> = orderRepository.findAll()

    fun submitOrder(bookIsbn: String, quantity: Int): Mono<Order> =
        bookClient.getBookbyIsbn(bookIsbn)
            .map { buildAcceptedOrder(it, quantity) }
            .defaultIfEmpty(buildRejectedOrder(bookIsbn, quantity))
            .flatMap(orderRepository::save)

    companion object {
        fun buildRejectedOrder(bookIsbn: String, quantity: Int): Order =
            Order.of(bookIsbn, null, null, quantity, REJECTED)

        fun buildAcceptedOrder(book: Book, quantity: Int) =
            Order.of(book.isbn, "${book.title} - ${book.author}", book.price, quantity, ACCEPTED)
    }
}