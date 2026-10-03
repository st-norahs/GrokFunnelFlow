package com.grokfunnel.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grokfunnel.ui.theme.*
import com.grokfunnel.ui.viewmodel.FunnelUiState
import com.grokfunnel.ui.viewmodel.FunnelViewModel

/**
 * Major UI shell — bottom nav + top bar.
 * Individual tab content screens (Leads, Gigs, Viral, Agents, Analytics)
 * can be expanded from original Prompt-Audits UI modules.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunnelDashboardScreen(
    viewModel: FunnelViewModel,
    uiState: FunnelUiState
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("grok_funnel_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Logo",
                                tint = Slate950,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GrokFunnelFlow",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = uiState.teamSettings.teamName,
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanGlow,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf(
                    Triple(0, Icons.Default.Dashboard, "Funnel"),
                    Triple(1, Icons.Default.People, "Leads"),
                    Triple(2, Icons.Default.MonetizationOn, "Gigs"),
                    Triple(3, Icons.Default.SmartDisplay, "Viral"),
                    Triple(4, Icons.Default.SmartToy, "Agents"),
                    Triple(5, Icons.Default.QueryStats, "Analytics")
                )
                tabs.forEach { (index, icon, label) ->
                    NavigationBarItem(
                        selected = uiState.activeNavTab == index,
                        onClick = { viewModel.setNavTab(index) },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 10.sp) },
                        modifier = Modifier.testTag("nav_tab_$index")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState.activeNavTab) {
                0 -> FunnelHomeTab(uiState)
                1 -> PlaceholderTab("Leads Pipeline", "Port LeadManagementView from original")
                2 -> PlaceholderTab("Gigs & Pay", "Collect payments via Paystack")
                3 -> PlaceholderTab("Viral Studio", "Grok text-to-video already wired in ViewModel")
                4 -> PlaceholderTab("Agents", "Grok sequence copy already wired")
                5 -> PlaceholderTab("Analytics", "Forecast + source performance")
            }
        }
    }
}

@Composable
private fun FunnelHomeTab(uiState: FunnelUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Pipeline Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Pipeline",
                    value = "$${uiState.totalPipelineValue.toInt()}",
                    modifier = Modifier.weight(1f),
                    color = CyanPrimary
                )
                MetricCard(
                    title = "Closed Won",
                    value = "$${uiState.totalClosedWonValue.toInt()}",
                    modifier = Modifier.weight(1f),
                    color = EmeraldSuccess
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Win Rate",
                    value = "${uiState.overallWinRate}%",
                    modifier = Modifier.weight(1f),
                    color = VioletSecondary
                )
                MetricCard(
                    title = "Gigs Earned",
                    value = "$${uiState.totalGigRevenueCollected.toInt()}",
                    modifier = Modifier.weight(1f),
                    color = AmberWarning
                )
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Revenue Collection Ready",
                        fontWeight = FontWeight.Bold,
                        color = CyanGlow
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Paystack module is live. Use Collect Payment on any lead or gig to open a real checkout. See docs/REVENUE_COLLECTION_PLUG.md.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                }
            }
        }
        item {
            Text(
                text = "Leads: ${uiState.leads.size}  •  Agents: ${uiState.agents.size}  •  Gigs: ${uiState.gigs.size}",
                style = MaterialTheme.typography.labelMedium,
                color = Slate400
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = tinSlate900),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Slate400)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 20.sp)
        }
    }
}

// local alias to avoid import conflict if needed
private val tinSlate900 = Slate900

@Composable
private fun PlaceholderTab(title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Slate400)
        }
    }
}
