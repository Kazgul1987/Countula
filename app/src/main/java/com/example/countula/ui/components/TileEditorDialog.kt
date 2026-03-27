package com.example.countula.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.countula.data.CounterTile
import com.example.countula.ui.colorFromStoredArgb
import com.example.countula.ui.toStoredArgbLong
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TileEditorDialog(
    initialTile: CounterTile?,
    onDismiss: () -> Unit,
    onConfirm: (title: String, priceInCents: Long, colorHex: Long) -> Unit
) {
    var title by remember(initialTile) { mutableStateOf(initialTile?.title.orEmpty()) }
    var price by remember(initialTile) { mutableStateOf(initialTile?.priceInCents?.let { (it / 100.0).toString() }.orEmpty()) }
    var selectedColor by remember(initialTile) {
        mutableStateOf(initialTile?.colorHex?.let(::colorFromStoredArgb) ?: colorOptions.first())
    }

    val parsedPrice = price.replace(',', '.').toDoubleOrNull()
    val priceError = parsedPrice == null || parsedPrice <= 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialTile == null) "Neue Kachel" else "Kachel bearbeiten")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titel (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Preis in Euro") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = priceError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (priceError) {
                    Text(
                        text = "Preis muss positiv und gültig sein.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Farbe")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorOptions.forEach { color ->
                        val selected = color == selectedColor
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!priceError) {
                        onConfirm(
                            title.trim(),
                            ((parsedPrice ?: 0.0) * 100).toLong(),
                            selectedColor.toStoredArgbLong()
                        )
                    }
                }
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

private val colorOptions = listOf(
    Color(0xFFE57373),
    Color(0xFFFFB74D),
    Color(0xFFFFF176),
    Color(0xFF81C784),
    Color(0xFF4DD0E1),
    Color(0xFF64B5F6),
    Color(0xFF9575CD),
    Color(0xFFF06292)
)
