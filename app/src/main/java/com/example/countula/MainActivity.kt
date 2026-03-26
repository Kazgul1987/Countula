package com.example.countula

import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = CounterDatabase.getInstance(applicationContext)
        val repository = CounterRepository(database.counterTileDao())

        setContent {
            CountulaTheme {
                val vm: CounterViewModel = viewModel(
                    factory = CounterViewModelFactory(repository)
                )
                CounterScreen(viewModel = vm)
            }
        }
    }
}

private class CounterViewModelFactory(
    private val repository: CounterRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
            return CounterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
