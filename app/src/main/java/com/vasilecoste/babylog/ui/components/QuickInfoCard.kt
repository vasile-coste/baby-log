package com.vasilecoste.babylog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.ui.main.QuickStats

@Composable
fun QuickInfoCard(stats: QuickStats, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.quick_total_food, stats.totalFoodMl),
                style = MaterialTheme.typography.titleMedium,
            )
            VitaminIndicator(taken = stats.vitaminTaken)
            StatRow(icon = Icons.Filled.WaterDrop, text = stringResource(R.string.quick_poops, stats.poopCount))
            StatRow(icon = Icons.Filled.LocalDrink, text = stringResource(R.string.quick_pees, stats.peeCount))
            StatRow(
                icon = Icons.Filled.Sick,
                text = stringResource(R.string.quick_pukes, stats.pukeCount),
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun StatRow(icon: ImageVector, text: String, tint: Color = MaterialTheme.colorScheme.onSurface) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint)
        Text(" $text")
    }
}

@Composable
private fun VitaminIndicator(taken: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (taken) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            contentDescription = null,
            tint = if (taken) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        )
        Text(
            " " + stringResource(
                R.string.quick_vitamin,
                stringResource(if (taken) R.string.label_yes else R.string.label_no),
            ),
        )
    }
}
