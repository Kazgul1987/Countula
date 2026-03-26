package com.example.countula.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun Color.toStoredArgbLong(): Long = toArgb().toLong()

fun colorFromStoredArgb(colorHex: Long): Color = Color(colorHex.toInt())
