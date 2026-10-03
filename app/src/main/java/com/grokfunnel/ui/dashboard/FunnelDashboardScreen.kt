package com.grokfunnel.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.grokfunnel.data.local.entities.GigEntity
import com.grokfunnel.data.local.entities.LeadEntity
import com.grokfunnel.ui.gigs.PaymentGatewayDialog
import com.grokfunnel.ui.leads.PaystackLeadPaymentDialog
import com.grokfunnel.ui.theme.*
import com.grokfunnel.ui.viewmodel.FunnelUiState
import com.grokfunnel.ui.viewmodel.FunnelViewModel

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
        modifier = Modifier.fillMaxSize().testTag("grok_funnel_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(CyanPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = SoftSlate950, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("GrokFunnelFlow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                            Text(uiState.teamSettings.teamName, style = MaterialTheme.typography.labelSmall, color = CyanGlow, fontSize = 10.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars).testTag("main_navigation_bar"),
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (uiState.activeNavTab) {
                0 -> FunnelHomeTab(uiState)
                1 -> LeadsTab(uiState, viewModel)
                2 -> GigsTab(uiState, viewModel)
                3 -> PlaceholderTab("Viral Studio", "Grok text-to-video wired in ViewModel")
                4 -> PlaceholderTab("Agents", "Grok sequence copy wired")
                5 -> PlaceholderTab("Analytics", "Forecast + source performance")
            }
        }
    }

    // Gig payment dialog
    uiState.activePaymentGig?.let { gig ->
        PaymentGatewayDialog(
            gig = gig,
            currencySymbol = uiState.teamSettings.currencySymbol,
            onDismiss = { viewModel.closePaymentModal() },
            onConfirmPayment = { g, gateway -> viewModel.confirmGigPayment(g, gateway) }
        )
    }

    // Lead Paystack dialog
    uiState.activePaystackLead?.let { lead ->
        PaystackLeadPaymentDialog(
            lead = lead,
            isInitializing = uiState.isInitializingPaystack,
            isVerifying = uiState.isVerifyingPaystack,
            lastCheckoutUrl = uiState.lastPaystackCheckoutUrl,
            lastReference = uiState.lastPaystackReference,
            onDismiss = { viewModel.closePaystackLeadModal() },
            onInitializeCheckout = { amount, currency ->
                viewModel.initializePaystackCheckout(lead.id, amount, currency)
            },
            onVerifyPayment = { ref -> viewModel.verifyPaystackPayment(ref) },
            onMarkPaidLocally = { amount, ref -> viewModel.markLeadPaidLocally(lead.id, amount, ref) }
        )
    }
}

@Composable
private fun FunnelHomeTab(uiState: FunnelUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Text("Pipeline Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Pipeline", "$${uiState.totalPipelineValue.toInt()}", Modifier.weight(1f), CyanPrimary)
                MetricCard("Closed Won", "$${uiState.totalClosedWonValue.toInt()}", Modifier.weight(1f), EmeraldSuccess)
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Win Rate", "${uiState.overallWinRate}%", Modifier.weight(1f), VioletSecondary)
                MetricCard("Gigs Earned", "$${uiState.totalGigRevenueCollected.toInt()}", Modifier.weight(1f), AmberWarning)
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = SoftSlate900), shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Revenue Collection Ready", fontWeight = FontWeight.Bold, color = CyanGlow)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Open Leads or Gigs tab → Collect Payment. Paystack init → verify → mark paid. See docs/REVENUE_COLLECTION_PLUG.md.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftSlate400
                    )
                }
            }
        }
        item {
            Text("Leads: ${uiState.leads.size}  •  Agents: ${uiState.agents.size}  •  Gigs: ${uiState.gigs.size}", style = MaterialTheme.typography.labelMedium, color = SoftSlate400)
        }
    }
}

@Composable
private fun LeadsTab(uiState: FunnelUiState, viewModel: FunnelViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Text("Leads (${uiState.leads.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(uiState.leads, key = { it.id }) { lead ->
            LeadCard(lead, uiState.teamSettings.currencySymbol) {
                viewModel.openPaystackLeadModal(lead)
            }
        }
    }
}

@Composable
private fun LeadCard(lead: LeadEntity, currency: String, onCollect: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SoftSlate900),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(lead.name, fontWeight = FontWeight.Bold, color = SoftSlate100)
                Text("$currency${lead.dealValue.toInt()}", fontWeight = FontWeight.Bold, color = CyanGlow)
            }
            Text("${lead.company} • ${lead.stage} • ${lead.scoreGrade}", fontSize = 12.sp, color = SoftSlate400)
            if (lead.paymentStatus != "PAID") {
                Button(
                    onClick = onCollect,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp), tint = SoftSlate950)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Collect Payment", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftSlate950)
                }
            } else {
                Text("PAID", fontWeight = FontWeight.Bold, color = EmeraldSuccess, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun GigsTab(uiState: FunnelUiState, viewModel: FunnelViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Text("Monetization Gigs (${uiState.gigs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(uiState.gigs, key = { it.id }) { gig ->
            GigCard(gig, uiState.teamSettings.currencySymbol) {
                viewModel.openPaymentModal(gig)
            }
        }
    }
}

@Composable
private fun GigCard(gig: GigEntity, currency: String, onCollect: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SoftSlate900),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(gig.title, fontWeight = FontWeight.Bold, color = SoftSlate100)
            Text("${gig.clientName} • ${gig.sourcePlatform}", fontSize = 12.sp, color = SoftSlate400)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("$currency${gig.budget.toInt()}  (${gig.profitMarginPercent}% margin)", fontWeight = FontWeight.SemiBold, color = CyanGlow)
                if (gig.status != "PAYMENT_COLLECTED") {
                    Button(
                        onClick = onCollect,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Collect", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftSlate950)
                    }
                } else {
                    Text("COLLECTED", fontWeight = FontWeight.Bold, color = EmeraldSuccess, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier, color: androidx.compose.ui.graphics.Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = SoftSlate900), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = SoftSlate400)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 20.sp)
        }
    }
}

@Composable
private fun PlaceholderTab(title: String, subtitle: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = SoftSlate400)
        }
    }
}

private val SoftSlate100 = Slate100
private val SoftSlate400 = SoftSlate400Alias
private val SoftSlate900 = Slate900
private val SoftSlate950 = SoftSlate950Alias
private val SoftSlate400Alias = Slate400
private val SoftSlate950Alias = SoftSlate950Fixed
private val SoftSlate950Fixed = Slate950
