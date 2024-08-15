package de.bashburg.springbook.orderservice.book

data class Book(
    val isbn: String,
    val title: String,
    val author: String,
    val price: Double,
)
