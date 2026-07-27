package com.vasilecoste.babylog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vasilecoste.babylog.ui.app.BabyLogApp
import com.vasilecoste.babylog.ui.main.MainViewModel
import com.vasilecoste.babylog.ui.theme.BabyLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
            val uiState by viewModel.uiState.collectAsState()
            BabyLogTheme(theme = uiState.activeTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BabyLogApp(viewModel = viewModel)
                }
            }
        }
    }
}
