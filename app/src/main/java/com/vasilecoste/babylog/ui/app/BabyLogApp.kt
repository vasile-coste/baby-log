package com.vasilecoste.babylog.ui.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vasilecoste.babylog.R
import com.vasilecoste.babylog.ui.about.AboutScreen
import com.vasilecoste.babylog.ui.chart.StatisticsScreen
import com.vasilecoste.babylog.ui.importexport.ImportExportScreen
import com.vasilecoste.babylog.ui.main.MainScreen
import com.vasilecoste.babylog.ui.main.MainViewModel
import com.vasilecoste.babylog.ui.profile.ProfileScreen
import com.vasilecoste.babylog.ui.tummytime.TummyTimeScreen
import com.vasilecoste.babylog.ui.weight.GrowthScreen
import java.time.LocalDate
import kotlinx.coroutines.launch

private enum class AppScreen { MAIN, TUMMY_TIME, GROWTH, STATISTICS, PROFILE, IMPORT_EXPORT, ABOUT }

@Composable
fun BabyLogApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var screen by remember { mutableStateOf(AppScreen.MAIN) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun navigateTo(target: AppScreen) {
        screen = target
        scope.launch { drawerState.close() }
    }

    val onMenuClick: () -> Unit = { scope.launch { drawerState.open() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    stringResource(R.string.drawer_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_main_app)) },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    selected = screen == AppScreen.MAIN,
                    onClick = { navigateTo(AppScreen.MAIN) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_tummy_time)) },
                    icon = { Icon(Icons.Filled.Timer, contentDescription = null) },
                    selected = screen == AppScreen.TUMMY_TIME,
                    onClick = { navigateTo(AppScreen.TUMMY_TIME) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_growth)) },
                    icon = { Icon(painterResource(R.drawable.id_growth), contentDescription = null) },
                    selected = screen == AppScreen.GROWTH,
                    onClick = { navigateTo(AppScreen.GROWTH) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_statistics)) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null) },
                    selected = screen == AppScreen.STATISTICS,
                    onClick = { navigateTo(AppScreen.STATISTICS) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_baby_profile)) },
                    icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
                    selected = screen == AppScreen.PROFILE,
                    onClick = { navigateTo(AppScreen.PROFILE) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_import_export)) },
                    icon = { Icon(Icons.Filled.ImportExport, contentDescription = null) },
                    selected = screen == AppScreen.IMPORT_EXPORT,
                    onClick = { navigateTo(AppScreen.IMPORT_EXPORT) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_about)) },
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    selected = screen == AppScreen.ABOUT,
                    onClick = { navigateTo(AppScreen.ABOUT) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        },
    ) {
        val today = LocalDate.now()
        val isNotToday = uiState.selectedDate != today

        // Placed inside ModalNavigationDrawer's content (rather than as a sibling above it) so this
        // handler is registered after — and takes priority over — the drawer's own internal
        // BackHandler, which would otherwise just close the drawer without resetting the screen.
        BackHandler(enabled = drawerState.isOpen || screen != AppScreen.MAIN || isNotToday) {
            if (drawerState.isOpen) {
                scope.launch { drawerState.close() }
            } else if (isNotToday) {
                viewModel.selectDate(today)
            } else if (screen != AppScreen.MAIN) {
                screen = AppScreen.MAIN
            }
        }

        when (screen) {
            AppScreen.MAIN -> MainScreen(onMenuClick = onMenuClick, viewModel = viewModel)
            AppScreen.TUMMY_TIME -> TummyTimeScreen(onMenuClick = onMenuClick, viewModel = viewModel)
            AppScreen.GROWTH -> GrowthScreen(onMenuClick = onMenuClick, viewModel = viewModel)
            AppScreen.STATISTICS -> StatisticsScreen(onMenuClick = onMenuClick, viewModel = viewModel)
            AppScreen.PROFILE -> ProfileScreen(onMenuClick = onMenuClick, viewModel = viewModel)
            AppScreen.IMPORT_EXPORT -> ImportExportScreen(onMenuClick = onMenuClick)
            AppScreen.ABOUT -> AboutScreen(onMenuClick = onMenuClick)
        }
    }
}
