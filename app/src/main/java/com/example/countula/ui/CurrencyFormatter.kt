package com.example.countula.ui

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)

    fun formatEuroFromCents(cents: Long): String = formatter.format(cents / 100.0)
}
