package com.example.countula.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed as listItemsIndexed
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.countula.data.CounterTile
import com.example.countula.ui.components.CounterTileBar
import com.example.countula.ui.components.CounterTileCard
import com.example.countula.ui.components.TileEditorDialog
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editTile by remember { mutableStateOf<CounterTile?>(null) }
    var isEditorOpen by rememberSaveable { mutableStateOf(false) }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragX by remember { mutableStateOf(0f) }
    var dragY by remember { mutableStateOf(0f) }
    var tileSize by remember { mutableStateOf(IntSize.Zero) }
    val gridColumns = 2
    val minTileHeightPx = with(LocalDensity.current) { 80.dp.toPx() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Countula") },
                actions = {
                    IconButton(onClick = viewModel::toggleLayoutMode) {
                        if (state.layoutMode == TileLayoutMode.GRID) {
                            Icon(
                                imageVector = Icons.Default.ViewAgenda,
                                contentDescription = "Balkenansicht aktivieren"
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Rasteransicht aktivieren"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = viewModel::requestResetAll) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Alle Counter auf 0 setzen")
                }
                Spacer(modifier = Modifier.width(12.dp))
                FloatingActionButton(onClick = {
                    editTile = null
                    isEditorOpen = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Neue Kachel")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TotalSumCard(totalInCents = state.totalInCents)

            when (state.layoutMode) {
                TileLayoutMode.GRID -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        gridItemsIndexed(items = state.tiles, key = { _, item -> item.id }) { index, tile ->
                            val isDragged = draggedIndex == index
                            CounterTileCard(
                                tile = tile,
                                canMoveUp = index > 0,
                                canMoveDown = index < state.tiles.lastIndex,
                                onClick = { viewModel.incrementTile(tile) },
                                onEdit = {
                                    editTile = tile
                                    isEditorOpen = true
                                },
                                onDelete = { viewModel.requestDelete(tile) },
                                onDecrement = { viewModel.decrementTile(tile) },
                                onMoveUp = { viewModel.moveTileUp(tile.id) },
                                onMoveDown = { viewModel.moveTileDown(tile.id) },
                                modifier = Modifier
                                    .onSizeChanged {
                                        if (it.width > 0 && it.height > 0) {
                                            tileSize = it
                                        }
                                    }
                                    .graphicsLayer {
                                        if (isDragged) {
                                            translationX = dragX
                                            translationY = dragY
                                        }
                                    }
                                    .pointerInput(index, state.tiles.size, tileSize) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                draggedIndex = index
                                                dragX = 0f
                                                dragY = 0f
                                            },
                                            onDragCancel = {
                                                draggedIndex = null
                                                dragX = 0f
                                                dragY = 0f
                                            },
                                            onDragEnd = {
                                                draggedIndex = null
                                                dragX = 0f
                                                dragY = 0f
                                            },
                                            onDrag = { change, dragAmount ->
                                                if (draggedIndex == null) {
                                                    draggedIndex = index
                                                }
                                                change.consume()
                                                dragX += dragAmount.x
                                                dragY += dragAmount.y

                                                val activeIndex = draggedIndex ?: index
                                                val widthPx = tileSize.width.toFloat().coerceAtLeast(1f)
                                                val heightPx = tileSize.height.toFloat().coerceAtLeast(minTileHeightPx)
                                                val rowDelta = (dragY / heightPx).roundToInt()
                                                val colDelta = (dragX / widthPx).roundToInt()

                                                val activeRow = activeIndex / gridColumns
                                                val activeCol = activeIndex % gridColumns
                                                val targetRow = (activeRow + rowDelta).coerceAtLeast(0)
                                                val targetCol = (activeCol + colDelta).coerceIn(0, gridColumns - 1)
                                                val targetIndex = (targetRow * gridColumns + targetCol)
                                                    .coerceIn(0, state.tiles.lastIndex)

                                                if (targetIndex != activeIndex) {
                                                    viewModel.moveTile(activeIndex, targetIndex)
                                                    draggedIndex = targetIndex
                                                    dragX = 0f
                                                    dragY = 0f
                                                }
                                            }
                                        )
                                    }
                            )
                        }
                    }
                }

                TileLayoutMode.BAR -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listItemsIndexed(items = state.tiles, key = { _, item -> item.id }) { index, tile ->
                            CounterTileBar(
                                tile = tile,
                                canMoveUp = index > 0,
                                canMoveDown = index < state.tiles.lastIndex,
                                onIncrement = { viewModel.incrementTile(tile) },
                                onEdit = {
                                    editTile = tile
                                    isEditorOpen = true
                                },
                                onDelete = { viewModel.requestDelete(tile) },
                                onDecrement = { viewModel.decrementTile(tile) },
                                onMoveUp = { viewModel.moveTileUp(tile.id) },
                                onMoveDown = { viewModel.moveTileDown(tile.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        if (isEditorOpen) {
            TileEditorDialog(
                initialTile = editTile,
                onDismiss = { isEditorOpen = false },
                onConfirm = { title, priceInCents, colorHex ->
                    val tile = editTile
                    if (tile == null) {
                        viewModel.addTile(title, priceInCents, colorHex)
                    } else {
                        viewModel.updateTile(tile, title, priceInCents, colorHex)
                    }
                    isEditorOpen = false
                }
            )
        }

        when (val dialog = state.dialogState) {
            is DialogState.ConfirmDelete -> {
                AlertDialog(
                    onDismissRequest = viewModel::dismissDialog,
                    title = { Text("Kachel löschen?") },
                    text = { Text("Soll die Kachel '${dialog.tile.displayTitle()}' wirklich gelöscht werden?") },
                    confirmButton = {
                        TextButton(onClick = { viewModel.confirmDelete(dialog.tile) }) {
                            Text("Löschen")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::dismissDialog) {
                            Text("Abbrechen")
                        }
                    }
                )
            }

            DialogState.ConfirmResetAll -> {
                AlertDialog(
                    onDismissRequest = viewModel::dismissDialog,
                    title = { Text("Alle Counter zurücksetzen?") },
                    text = { Text("Möchtest du wirklich alle Counter auf 0 setzen?") },
                    confirmButton = {
                        TextButton(onClick = viewModel::confirmResetAll) {
                            Text("Zurücksetzen")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::dismissDialog) {
                            Text("Abbrechen")
                        }
                    }
                )
            }

            DialogState.None -> Unit
        }
    }
}

@Composable
private fun TotalSumCard(totalInCents: Long) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Gesamtsumme",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = CurrencyFormatter.formatEuroFromCents(totalInCents),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
