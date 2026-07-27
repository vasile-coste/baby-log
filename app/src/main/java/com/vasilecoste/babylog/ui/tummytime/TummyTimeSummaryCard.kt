package com.vasilecoste.babylog.ui.tummytime

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.R

@Composable
fun TummyTimeSummaryCard(totalSeconds: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Text(
            stringResource(
                R.string.tummy_time_total_today,
                totalSeconds / 60,
                totalSeconds % 60,
            ),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp),
        )
    }
}
