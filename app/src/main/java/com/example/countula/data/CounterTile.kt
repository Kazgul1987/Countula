package com.example.countula.data

data class CounterTile(
    val id: Long,
    val title: String,
    val priceInCents: Long,
    val colorHex: Long,
    val counter: Int,
    val position: Int
)

fun CounterTileEntity.toDomain(): CounterTile = CounterTile(
    id = id,
    title = title,
    priceInCents = priceInCents,
    colorHex = colorHex,
    counter = counter,
    position = position
)

fun CounterTile.toEntity(): CounterTileEntity = CounterTileEntity(
    id = id,
    title = title,
    priceInCents = priceInCents,
    colorHex = colorHex,
    counter = counter,
    position = position
)
