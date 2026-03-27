package com.example.countula.ui

import com.example.countula.data.CounterTile

fun CounterTile.displayTitle(): String {
    return title.ifBlank { CurrencyFormatter.formatEuroFromCents(priceInCents) }
}
