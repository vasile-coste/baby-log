package com.vasilecoste.babylog.ui.growth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.db.entity.WeightRecord
import kotlin.math.roundToInt

private data class GrowthMetricText(val current: String, val change: String?)

@Composable
fun GrowthSummaryCard(records: List<WeightRecord>, modifier: Modifier = Modifier) {
    val weightValues = records.filter { it.weightKg != null }.sortedBy { it.date }.map { it.weightKg!! }
    val heightValues = records.filter { it.heightCm != null }.sortedBy { it.date }.map { it.heightCm!! }

    val weightText = weightSummary(weightValues)
    val heightText = heightSummary(heightValues)

    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(painterResource(R.drawable.id_weight), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)

            Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(weightText.current, style = MaterialTheme.typography.titleMedium)
                weightText.change?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(painterResource(R.drawable.id_height), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)

            Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(heightText.current, style = MaterialTheme.typography.titleMedium)
                heightText.change?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
private fun weightSummary(values: List<Double>): GrowthMetricText {
    if (values.size < 2) {
        return GrowthMetricText(stringResource(R.string.growth_current_weight_insufficient), null)
    }
    val current = values.last()
    val diff = current - values[values.size - 2]
    val roundedDiff = (diff * 10).roundToInt() / 10.0
    val currentText = stringResource(R.string.growth_current_weight, current)
    val changeText = when {
        roundedDiff == 0.0 -> null
        roundedDiff > 0 -> stringResource(R.string.growth_change_increase_kg, diff)
        else -> stringResource(R.string.growth_change_decrease_kg, -diff)
    }
    return GrowthMetricText(currentText, changeText)
}

@Composable
private fun heightSummary(values: List<Double>): GrowthMetricText {
    if (values.size < 2) {
        return GrowthMetricText(stringResource(R.string.growth_current_height_insufficient), null)
    }
    val current = values.last()
    val diff = current - values[values.size - 2]
    val roundedDiff = (diff * 10).roundToInt() / 10.0
    val currentText = stringResource(R.string.growth_current_height, current)
    val changeText = when {
        roundedDiff == 0.0 -> null
        roundedDiff > 0 -> stringResource(R.string.growth_change_increase_cm, diff)
        else -> stringResource(R.string.growth_change_decrease_cm, -diff)
    }
    return GrowthMetricText(currentText, changeText)
}
