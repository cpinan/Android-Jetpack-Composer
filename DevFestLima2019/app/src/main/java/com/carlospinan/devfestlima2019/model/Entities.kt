package com.carlospinan.devfestlima2019.model

/**
 * @author Carlos Piñan
 */
data class Session(
    val title: String,
    val description: String,
    val author: Author
)

data class Author(
    val name: String,
    val company: String,
    val pictureId: Int
)