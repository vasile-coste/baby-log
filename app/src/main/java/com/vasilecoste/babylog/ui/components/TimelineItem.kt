package com.vasilecoste.babylog.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.data.db.entity.Entry
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun TimelineItem(entry: Entry, onLongPress: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongPress,
            ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(entry.time.format(timeFormatter), style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(56.dp))
            if (entry.foodMl != null) {
                Text(
                    stringResource(R.string.timeline_food_amount, entry.foodMl),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.width(72.dp),
                )
            }
            if (entry.poop) {
                Icon(Icons.Filled.WaterDrop, contentDescription = stringResource(R.string.cd_poop))
            }
            if (entry.pee) {
                Icon(Icons.Filled.LocalDrink, contentDescription = stringResource(R.string.cd_pee))
            }
            if (entry.puke) {
                Icon(
                    Icons.Filled.Sick,
                    contentDescription = stringResource(R.string.cd_puke),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
            if (entry.vitamin) {
                Icon(Icons.Filled.Medication, contentDescription = stringResource(R.string.cd_vitamin))
            }
            if (entry.breastfed) {
                Text(stringResource(R.string.checkbox_breastfed), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
