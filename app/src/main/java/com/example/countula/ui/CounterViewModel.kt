package com.example.countula.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countula.data.CounterRepository
import com.example.countula.data.CounterTile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CounterViewModel(
    private val repository: CounterRepository,
    initialLayoutMode: TileLayoutMode = TileLayoutMode.GRID,
    private val onLayoutModeChanged: (TileLayoutMode) -> Unit = {}
) : ViewModel() {

    private val dialogState = MutableStateFlow<DialogState>(DialogState.None)
    private val layoutMode = MutableStateFlow(initialLayoutMode)

    val uiState: StateFlow<CounterUiState> = combine(repository.tiles, dialogState, layoutMode) { tiles, dialog, mode ->
        CounterUiState(
            tiles = tiles,
            totalInCents = tiles.sumOf { it.priceInCents * it.counter },
            dialogState = dialog,
            layoutMode = mode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CounterUiState()
    )

    fun incrementTile(tile: CounterTile) {
        viewModelScope.launch {
            repository.incrementTile(tile)
        }
    }

    fun addTile(title: String, priceInCents: Long, colorHex: Long) {
        viewModelScope.launch {
            repository.addTile(title.trim(), priceInCents, colorHex)
        }
    }

    fun updateTile(tile: CounterTile, title: String, priceInCents: Long, colorHex: Long) {
        viewModelScope.launch {
            repository.updateTile(
                tile.copy(
                    title = title.trim(),
                    priceInCents = priceInCents,
                    colorHex = colorHex
                )
            )
        }
    }

    fun requestDelete(tile: CounterTile) {
        dialogState.value = DialogState.ConfirmDelete(tile)
    }

    fun confirmDelete(tile: CounterTile) {
        viewModelScope.launch {
            repository.deleteTile(tile)
            dialogState.value = DialogState.None
        }
    }

    fun requestResetAll() {
        dialogState.value = DialogState.ConfirmResetAll
    }

    fun confirmResetAll() {
        viewModelScope.launch {
            repository.resetAllCounters()
            dialogState.value = DialogState.None
        }
    }

    fun dismissDialog() {
        dialogState.value = DialogState.None
    }

    fun setLayoutMode(mode: TileLayoutMode) {
        layoutMode.value = mode
        onLayoutModeChanged(mode)
    }

    fun toggleLayoutMode() {
        val nextMode = when (layoutMode.value) {
            TileLayoutMode.GRID -> TileLayoutMode.BAR
            TileLayoutMode.BAR -> TileLayoutMode.GRID
        }
        setLayoutMode(nextMode)
    }

    fun moveTileUp(tileId: Long) {
        moveTile(tileId, direction = -1)
    }

    fun moveTileDown(tileId: Long) {
        moveTile(tileId, direction = 1)
    }

    fun moveTile(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            val current = uiState.value.tiles
            if (fromIndex !in current.indices || toIndex !in current.indices || fromIndex == toIndex) {
                return@launch
            }

            val mutable = current.toMutableList()
            val moved = mutable.removeAt(fromIndex)
            mutable.add(toIndex, moved)
            repository.reorderTiles(mutable)
        }
    }

    fun decrementTile(tile: CounterTile) {
        viewModelScope.launch {
            repository.decrementTile(tile)
        }
    }

    private fun moveTile(tileId: Long, direction: Int) {
        viewModelScope.launch {
            val current = uiState.value.tiles
            val index = current.indexOfFirst { it.id == tileId }
            if (index == -1) return@launch
            val target = index + direction
            if (target !in current.indices) return@launch

            val mutable = current.toMutableList()
            val temp = mutable[index]
            mutable[index] = mutable[target]
            mutable[target] = temp
            repository.reorderTiles(mutable)
        }
    }
}

sealed interface DialogState {
    data object None : DialogState
    data object ConfirmResetAll : DialogState
    data class ConfirmDelete(val tile: CounterTile) : DialogState
}

enum class TileLayoutMode {
    GRID,
    BAR
}

data class CounterUiState(
    val tiles: List<CounterTile> = emptyList(),
    val totalInCents: Long = 0,
    val dialogState: DialogState = DialogState.None,
    val layoutMode: TileLayoutMode = TileLayoutMode.GRID
)
