package com.example.countula.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CounterTileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CounterDatabase : RoomDatabase() {
    abstract fun counterTileDao(): CounterTileDao

    companion object {
        @Volatile
        private var INSTANCE: CounterDatabase? = null

        fun getInstance(context: Context): CounterDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CounterDatabase::class.java,
                    "countula.db"
                ).build().also { INSTANCE = it }
            }
    }
}
