package com.vasilecoste.babylog.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sick
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
            Spacer(modifier = Modifier.weight(1f))
            if (entry.poop) {
                Icon(painterResource(R.drawable.ic_poop), contentDescription = stringResource(R.string.cd_poop))
            }
            if (entry.pee) {
                Icon(painterResource(R.drawable.ic_pee), contentDescription = stringResource(R.string.cd_pee))
            }
            if (entry.puke) {
                Icon(
                    Icons.Outlined.Sick,
                    contentDescription = stringResource(R.string.cd_puke),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
            if (entry.vitamin) {
                Icon(painterResource(R.drawable.id_vitamins), contentDescription = stringResource(R.string.cd_vitamin))
            }
            if (entry.breastfed) {
                Icon(painterResource(R.drawable.id_breastfeed), contentDescription = stringResource(R.string.checkbox_breastfed))
            }
        }
    }
}
