package com.grokfunnel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grokfunnel.data.local.AppDatabase
import com.grokfunnel.data.remote.grok.GrokApiService
import com.grokfunnel.data.repository.FunnelRepository
import com.grokfunnel.ui.dashboard.FunnelDashboardScreen
import com.grokfunnel.ui.theme.GrokFunnelTheme
import com.grokfunnel.ui.viewmodel.FunnelViewModel
import com.grokfunnel.ui.viewmodel.FunnelViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: FunnelViewModel by viewModels {
        GrokApiService.apiKey = BuildConfig.XAI_API_KEY ?: ""
        GrokApiService.model = BuildConfig.GROK_MODEL ?: "grok-4"

        val database = AppDatabase.getInstance(applicationContext)
        val repository = FunnelRepository(database)
        FunnelViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrokFunnelTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                BackHandler(enabled = uiState.activeNavTab != 0) {
                    viewModel.setNavTab(0)
                }

                FunnelDashboardScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}
