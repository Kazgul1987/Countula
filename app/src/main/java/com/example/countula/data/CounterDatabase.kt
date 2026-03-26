package com.example.countula.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CounterTileEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CounterDatabase : RoomDatabase() {
    abstract fun counterTileDao(): CounterTileDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                        UPDATE counter_tiles
                        SET colorHex = colorHex | 4278190080
                        WHERE (colorHex & 4294967295) <= 4294967295
                          AND ((colorHex >> 24) & 255) = 0
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: CounterDatabase? = null

        fun getInstance(context: Context): CounterDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CounterDatabase::class.java,
                    "countula.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
