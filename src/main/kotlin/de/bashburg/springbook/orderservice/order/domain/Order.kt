package de.bashburg.springbook.orderservice.order.domain

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("orders")
data class Order(
    @Id
    val id: Long?,

    val bookIsbn: String,
    val bookName: String?,
    val bookPrice: Double?,
    val quantity: Int,
    val status: OrderStatus,

    @CreatedDate
    val createdDate: Instant?,

    @CreatedBy
    val createdBy: String?,

    @LastModifiedDate
    val lastModifiedDate: Instant?,

    @LastModifiedBy
    val lastModifiedBy: String?,

    @Version
    val version: Int = 0
) {
    companion object {
        fun of(
            bookIsbn: String,
            bookName: String?,
            bookPrice: Double?,
            quantity: Int,
            status: OrderStatus
        ): Order {
            return Order(
                bookIsbn = bookIsbn,
                bookName = bookName,
                bookPrice = bookPrice,
                quantity = quantity,
                status = status,
                lastModifiedDate = null,
                lastModifiedBy = null,
                createdDate = null,
                createdBy = null,
                id = null
            )
        }
    }
}
