package com.vasilecoste.babylog.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.ui.components.SimpleTopBar

@Composable
fun AboutScreen(onMenuClick: () -> Unit) {
    Scaffold(topBar = { SimpleTopBar(title = stringResource(R.string.about_title), onMenuClick = onMenuClick) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.about_version_label, "1.0"))
            Text(stringResource(R.string.about_app_description))
            Text(stringResource(R.string.about_tech_stack), style = MaterialTheme.typography.bodySmall)
        }
    }
}
