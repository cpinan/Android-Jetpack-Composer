package com.carlospinan.jetpackexample.gmail.mock

import androidx.ui.graphics.Color

data class MockData(
    val title: String,
    val content: String,
    val author: String,
    val color: Color = Color.Blue
) {
    val shortAuthor: String = author[0].toString()
}

val items = listOf(
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan",
        Color.Red
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ),
    MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    ), MockData(
        "Title 01",
        "Content 00001",
        "Carlos Piñan"
    )

)