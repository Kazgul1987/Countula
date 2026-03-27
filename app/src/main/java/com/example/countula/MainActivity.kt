package com.example.countula

import android.os.Bundle
import androidx.core.content.edit
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.countula.data.CounterDatabase
import com.example.countula.data.CounterRepository
import com.example.countula.ui.CounterScreen
import com.example.countula.ui.CounterViewModel
import com.example.countula.ui.theme.CountulaTheme

class MainActivity : ComponentActivity() {
    private val layoutPrefsName = "countula_prefs"
    private val layoutModeKey = "tile_layout_mode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = CounterDatabase.getInstance(applicationContext)
        val repository = CounterRepository(database.counterTileDao())
        val sharedPreferences = getSharedPreferences(layoutPrefsName, MODE_PRIVATE)
        val savedLayoutMode = runCatching {
            com.example.countula.ui.TileLayoutMode.valueOf(
                sharedPreferences.getString(layoutModeKey, com.example.countula.ui.TileLayoutMode.GRID.name)
                    ?: com.example.countula.ui.TileLayoutMode.GRID.name
            )
        }.getOrDefault(com.example.countula.ui.TileLayoutMode.GRID)

        setContent {
            CountulaTheme {
                val vm: CounterViewModel = viewModel(
                    factory = CounterViewModelFactory(
                        repository = repository,
                        initialLayoutMode = savedLayoutMode,
                        onLayoutModeChanged = { mode ->
                            sharedPreferences.edit {
                                putString(layoutModeKey, mode.name)
                            }
                        }
                    )
                )
                CounterScreen(viewModel = vm)
            }
        }
    }
}

private class CounterViewModelFactory(
    private val repository: CounterRepository,
    private val initialLayoutMode: com.example.countula.ui.TileLayoutMode,
    private val onLayoutModeChanged: (com.example.countula.ui.TileLayoutMode) -> Unit
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
            return CounterViewModel(
                repository = repository,
                initialLayoutMode = initialLayoutMode,
                onLayoutModeChanged = onLayoutModeChanged
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
