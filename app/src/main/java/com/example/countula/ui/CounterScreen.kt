package com.example.countula.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RestartAlt
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.countula.data.CounterTile
import com.example.countula.ui.components.CounterTileCard
import com.example.countula.ui.components.TileEditorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(viewModel: CounterViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editTile by remember { mutableStateOf<CounterTile?>(null) }
    var isEditorOpen by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Countula") },
                actions = {
                    IconButton(onClick = viewModel::requestResetAll) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Alle Counter auf 0 setzen")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editTile = null
                isEditorOpen = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Neue Kachel")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TotalSumCard(totalInCents = state.totalInCents)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(items = state.tiles, key = { _, item -> item.id }) { index, tile ->
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
                        onMoveUp = { viewModel.moveTileUp(tile.id) },
                        onMoveDown = { viewModel.moveTileDown(tile.id) }
                    )
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
                    text = { Text("Soll die Kachel '${dialog.tile.title}' wirklich gelöscht werden?") },
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
