package de.bashburg.springbook.orderservice.order.domain

import de.bashburg.springbook.orderservice.book.Book
import de.bashburg.springbook.orderservice.book.BookClient
import de.bashburg.springbook.orderservice.order.domain.OrderStatus.*
import de.bashburg.springbook.orderservice.order.event.OrderAcceptedMessage
import de.bashburg.springbook.orderservice.order.event.OrderDispatchedMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val bookClient: BookClient,
    private val streamBridge: StreamBridge
) {

    fun getAllOrders(userId: String): Flux<Order> = orderRepository.findAllByCreatedBy(userId)

    @Transactional
    fun submitOrder(bookIsbn: String, quantity: Int): Mono<Order> =
        bookClient.getBookbyIsbn(bookIsbn)
            .map { buildAcceptedOrder(it, quantity) }
            .defaultIfEmpty(buildRejectedOrder(bookIsbn, quantity))
            .flatMap(orderRepository::save)
            .doOnNext(::publishOrderAcceptedEvent)

    fun consumeOrderDispatchedEvent(flux: Flux<OrderDispatchedMessage>): Flux<Order> =
        flux.flatMap { message -> orderRepository.findById(message.orderId) }
            .map(::buildDispatchedOrder)
            .flatMap(orderRepository::save)

    private fun publishOrderAcceptedEvent(order: Order) {
        if (order.status != ACCEPTED) {
            return
        }
        val orderAcceptedMessage = OrderAcceptedMessage(order.id!!)
        logger.info { "Sending order accepted event with id ${order.id}" }
        val result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage)
        logger.info { "Result of sending data for order with id ${order.id}: $result" }
    }

    companion object {
        fun buildRejectedOrder(bookIsbn: String, quantity: Int): Order =
            Order.of(bookIsbn, null, null, quantity, REJECTED)

        fun buildAcceptedOrder(book: Book, quantity: Int) =
            Order.of(book.isbn, "${book.title} - ${book.author}", book.price, quantity, ACCEPTED)

        fun buildDispatchedOrder(existingOrder: Order) =
            Order(
                existingOrder.id,
                existingOrder.bookIsbn,
                existingOrder.bookName,
                existingOrder.bookPrice,
                existingOrder.quantity,
                DISPATCHED,
                existingOrder.createdDate,
                existingOrder.createdBy,
                existingOrder.lastModifiedDate,
                existingOrder.lastModifiedBy,
                existingOrder.version
            )
    }
}