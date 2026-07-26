package com.vasilecoste.babylog.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuickStatCard(
            icon = Icons.Filled.Restaurant,
            text = stringResource(R.string.quick_total_food, stats.totalFoodMl)
        )

        QuickStatCard(
            icon = Icons.Filled.Medication,
            text = stringResource(
                R.string.quick_vitamin,
                stringResource(if (stats.vitaminTaken) R.string.label_yes else R.string.label_no),
            ),
            tint = if (stats.vitaminTaken) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )

        QuickStatCard(
            icon = Icons.Filled.WaterDrop,
            text = stringResource(R.string.quick_poops, stats.poopCount)
        )

        QuickStatCard(
            icon = Icons.Filled.LocalDrink,
            text = stringResource(R.string.quick_pees, stats.peeCount)
        )

        QuickStatCard(
            icon = Icons.Filled.Sick,
            text = stringResource(R.string.quick_pukes, stats.pukeCount),
            tint = if (stats.pukeCount == 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun QuickStatCard(
    icon: ImageVector,
    text: String,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint)
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
