package com.example.countula.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "counter_tiles")
data class CounterTileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val priceInCents: Long,
    val colorHex: Long,
    val counter: Int,
    val position: Int
)
