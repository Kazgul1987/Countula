package com.example.countula.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterTileDao {
    @Query("SELECT * FROM counter_tiles ORDER BY position ASC")
    fun observeTiles(): Flow<List<CounterTileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tile: CounterTileEntity): Long

    @Update
    suspend fun update(tile: CounterTileEntity)

    @Delete
    suspend fun delete(tile: CounterTileEntity)

    @Query("UPDATE counter_tiles SET counter = 0")
    suspend fun resetAllCounters()

    @Query("SELECT COALESCE(MAX(position), -1) FROM counter_tiles")
    suspend fun getMaxPosition(): Int
}
