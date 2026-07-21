package com.vasilecoste.babylog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.ui.main.QuickStats

@Composable
fun QuickInfoCard(stats: QuickStats, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Total food: ${stats.totalFoodMl} ml", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                VitaminIndicator(taken = stats.vitaminTaken)
                Text("Poops: ${stats.poopCount}")
                Text("Pees: ${stats.peeCount}")
            }
        }
    }
}

@Composable
private fun VitaminIndicator(taken: Boolean) {
    Row {
        Icon(
            imageVector = if (taken) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (taken) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        )
        Text(" Vitamin: ${if (taken) "Yes" else "No"}")
    }
}
