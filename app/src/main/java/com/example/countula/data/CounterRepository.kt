package com.example.countula.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CounterRepository(
    private val dao: CounterTileDao
) {
    val tiles: Flow<List<CounterTile>> = dao.observeTiles().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun addTile(title: String, priceInCents: Long, colorHex: Long) {
        val nextPosition = dao.getMaxPosition() + 1
        dao.insert(
            CounterTileEntity(
                title = title,
                priceInCents = priceInCents,
                colorHex = colorHex,
                counter = 0,
                position = nextPosition
            )
        )
    }

    suspend fun updateTile(tile: CounterTile) {
        dao.update(tile.toEntity())
    }

    suspend fun deleteTile(tile: CounterTile) {
        dao.delete(tile.toEntity())
    }

    suspend fun incrementTile(tile: CounterTile) {
        dao.update(tile.copy(counter = tile.counter + 1).toEntity())
    }

    suspend fun decrementTile(tile: CounterTile) {
        val nextCounter = (tile.counter - 1).coerceAtLeast(0)
        dao.update(tile.copy(counter = nextCounter).toEntity())
    }

    suspend fun resetAllCounters() {
        dao.resetAllCounters()
    }

    suspend fun reorderTiles(tiles: List<CounterTile>) {
        tiles.forEachIndexed { index, tile ->
            dao.update(tile.copy(position = index).toEntity())
        }
    }
}
