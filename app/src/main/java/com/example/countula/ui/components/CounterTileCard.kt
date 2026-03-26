package com.example.countula.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.countula.data.CounterTile
import com.example.countula.ui.CurrencyFormatter
import com.example.countula.ui.colorFromStoredArgb
import kotlin.math.max
import kotlin.math.min

@Composable
fun CounterTileCard(
    tile: CounterTile,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val tileColor = colorFromStoredArgb(tile.colorHex)
    val contentColor = chooseBestContrastColor(tileColor)
    val titleText = tile.title.ifBlank { "Kachel" }
    val subtotal = CurrencyFormatter.formatEuroFromCents(tile.counter * tile.priceInCents)
    var overflowExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = tileColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Preis: ${CurrencyFormatter.formatEuroFromCents(tile.priceInCents)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Bearbeiten",
                            tint = contentColor
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Löschen",
                            tint = contentColor
                        )
                    }
                    IconButton(
                        onClick = { overflowExpanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Mehr Aktionen",
                            tint = contentColor
                        )
                    }

                    DropdownMenu(
                        expanded = overflowExpanded,
                        onDismissRequest = { overflowExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nach oben") },
                            onClick = {
                                overflowExpanded = false
                                onMoveUp()
                            },
                            enabled = canMoveUp,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nach unten") },
                            onClick = {
                                overflowExpanded = false
                                onMoveDown()
                            },
                            enabled = canMoveDown,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(contentColor.copy(alpha = 0.10f))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Counter: ${tile.counter}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtotal,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun chooseBestContrastColor(background: Color): Color {
    val contrastWithBlack = contrastRatio(background, Color.Black)
    val contrastWithWhite = contrastRatio(background, Color.White)
    return if (contrastWithBlack >= contrastWithWhite) Color.Black else Color.White
}

private fun contrastRatio(first: Color, second: Color): Double {
    val firstLuminance = relativeLuminance(first)
    val secondLuminance = relativeLuminance(second)
    val lighter = max(firstLuminance, secondLuminance)
    val darker = min(firstLuminance, secondLuminance)
    return (lighter + 0.05) / (darker + 0.05)
}

private fun relativeLuminance(color: Color): Double {
    val red = linearize(color.red)
    val green = linearize(color.green)
    val blue = linearize(color.blue)
    return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue)
}

private fun linearize(channel: Float): Double {
    val normalized = channel.toDouble()
    return if (normalized <= 0.03928) {
        normalized / 12.92
    } else {
        Math.pow((normalized + 0.055) / 1.055, 2.4)
    }
}
