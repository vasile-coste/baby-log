package com.vasilecoste.babylog

import android.content.Intent
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
import com.vasilecoste.babylog.ui.app.AppScreen
import com.vasilecoste.babylog.ui.app.BabyLogApp
import com.vasilecoste.babylog.ui.main.MainViewModel
import com.vasilecoste.babylog.ui.theme.BabyLogTheme
import com.vasilecoste.babylog.ui.tummytime.TummyTimeService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val viewModel: MainViewModel by lazy { 
            androidx.lifecycle.ViewModelProvider(this, MainViewModel.Factory)[MainViewModel::class.java]
        }
        
        handleIntent(intent, viewModel)

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            BabyLogTheme(theme = uiState.activeTheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BabyLogApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val viewModel = androidx.lifecycle.ViewModelProvider(this, MainViewModel.Factory)[MainViewModel::class.java]
        handleIntent(intent, viewModel)
    }

    private fun handleIntent(intent: Intent?, viewModel: MainViewModel) {
        val screenName = intent?.getStringExtra(TummyTimeService.EXTRA_SCREEN)
        if (screenName == "TUMMY_TIME") {
            viewModel.navigateTo(AppScreen.TUMMY_TIME)
            // Clear the extra so it doesn't trigger again on rotation if the activity is recreated without intent flags
            intent.removeExtra(TummyTimeService.EXTRA_SCREEN)
        }
    }
}
