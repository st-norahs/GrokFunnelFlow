package com.grokfunnel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.grokfunnel.data.local.entities.*
import com.grokfunnel.data.remote.grok.GrokApiService
import com.grokfunnel.data.remote.paystack.PaystackApiResult
import com.grokfunnel.data.repository.FunnelRepository
import com.grokfunnel.domain.LeadScoringEngine
import com.grokfunnel.domain.SalesForecast
import com.grokfunnel.domain.LeadSourcePerformance
import com.grokfunnel.domain.TeamComparison
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class FunnelStageStat(
    val stageKey: String,
    val displayName: String,
    val count: Int,
    val totalValue: Double,
    val defaultWinProbability: Double,
    val conversionRateFromStart: Double,
    val dropOffRateFromPrev: Double,
    val colorHex: String,
    val isWonStage: Boolean,
    val isLostStage: Boolean
)

data class TrendPoint(val dayLabel: String, val revenue: Double, val conversionRate: Double)

data class FunnelUiState(
    val leads: List<LeadEntity> = emptyList(),
    val stages: List<PipelineStageEntity> = emptyList(),
    val agents: List<AgentEntity> = emptyList(),
    val sequences: List<EmailSequenceEntity> = emptyList(),
    val gigs: List<GigEntity> = emptyList(),
    val top5Gigs: List<GigEntity> = emptyList(),
    val recentLogs: List<ActivityLogEntity> = emptyList(),
    val crmConfigs: List<CrmConfigEntity> = emptyList(),
    val paymentRecords: List<PaymentRecordEntity> = emptyList(),
    val teamSettings: TeamSettingsEntity = TeamSettingsEntity(),
    val stageStats: List<FunnelStageStat> = emptyList(),
    val totalPipelineValue: Double = 0.0,
    val totalClosedWonValue: Double = 0.0,
    val totalGigRevenueCollected: Double = 0.0,
    val overallWinRate: Double = 0.0,
    val contactToQualifiedRate: Double = 0.0,
    val qualifiedToCloseRate: Double = 0.0,
    val averageDealSize: Double = 0.0,
    val trendData: List<TrendPoint> = emptyList(),
    val salesForecast: SalesForecast = SalesForecast(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 18),
    val leadSourcePerformance: List<LeadSourcePerformance> = emptyList(),
    val teamComparisons: List<TeamComparison> = emptyList(),
    val paystackWebhookLogs: List<PaystackWebhookLogEntity> = emptyList(),
    val viralVideos: List<ViralVideoEntity> = emptyList(),
    val videoProjects: List<VideoProjectEntity> = emptyList(),
    val trafficMonetization: TrafficMonetizationEntity = TrafficMonetizationEntity(),
    val conversionLogs: List<MonetizationConversionLogEntity> = emptyList(),
    val isScanningViralVideos: Boolean = false,
    val isCloningVideoProject: Boolean = false,
    val isGeneratingTextToVideo: Boolean = false,
    val isAutoPosting: Boolean = false,
    val isMonetizingTraffic: Boolean = false,
    val selectedViralPlatformFilter: String? = null,
    val selectedVideoForCloning: ViralVideoEntity? = null,
    val selectedStageFilter: String? = null,
    val selectedScoreGradeFilter: String? = null,
    val searchQuery: String = "",
    val activeNavTab: Int = 0,
    val isScanningGigs: Boolean = false,
    val isScoutingLeads: Boolean = false,
    val isSyncingCrm: String? = null,
    val toastMessage: String? = null,
    val lastGrokScript: String? = null,
    val lastGrokInsight: String? = null,
    // Payment collection state
    val activePaymentGig: GigEntity? = null,
    val activePaystackLead: LeadEntity? = null,
    val isInitializingPaystack: Boolean = false,
    val isVerifyingPaystack: Boolean = false,
    val lastPaystackCheckoutUrl: String? = null,
    val lastPaystackReference: String? = null
)

class FunnelViewModel(private val repository: FunnelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FunnelUiState())
    val uiState: StateFlow<FunnelUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { repository.initializeDefaultDataIfEmpty() }
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.allLeads,
                repository.allStages,
                repository.allAgents,
                repository.teamSettings,
                repository.allViralVideos,
                repository.allVideoProjects,
                repository.allGigs,
                repository.top5Gigs,
                repository.paymentRecords
            ) { values ->
                values
            }.collect { values ->
                @Suppress("UNCHECKED_CAST")
                val leads = values[0] as List<LeadEntity>
                val stages = values[1] as List<PipelineStageEntity>
                val agents = values[2] as List<AgentEntity>
                val settings = (values[3] as TeamSettingsEntity?) ?: TeamSettingsEntity()
                val videos = values[4] as List<ViralVideoEntity>
                val projects = values[5] as List<VideoProjectEntity>
                val gigs = values[6] as List<GigEntity>
                val top5 = values[7] as List<GigEntity>
                val payments = values[8] as List<PaymentRecordEntity>

                val won = leads.filter { it.stage == "CLOSED_WON" || it.isCustomer }
                val pipeline = leads.filter { it.stage != "CLOSED_WON" && it.stage != "CLOSED_LOST" }
                val totalPipe = pipeline.sumOf { it.dealValue }
                val totalWon = won.sumOf { it.dealValue }
                val winRate = if (leads.isNotEmpty()) won.size.toDouble() / leads.size * 100.0 else 0.0
                val gigRevenue = payments.sumOf { it.netAmount }

                _uiState.update { current ->
                    current.copy(
                        leads = leads,
                        stages = stages,
                        agents = agents,
                        teamSettings = settings,
                        viralVideos = videos,
                        videoProjects = projects,
                        gigs = gigs,
                        top5Gigs = top5,
                        paymentRecords = payments,
                        totalPipelineValue = totalPipe,
                        totalClosedWonValue = totalWon,
                        totalGigRevenueCollected = gigRevenue,
                        overallWinRate = Math.round(winRate * 10.0) / 10.0
                    )
                }
            }
        }
    }

    fun setNavTab(tabIndex: Int) {
        _uiState.update { it.copy(activeNavTab = tabIndex) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    // ─── Payment collection ───────────────────────────────────────────────────

    fun openPaymentModal(gig: GigEntity) {
        _uiState.update { it.copy(activePaymentGig = gig) }
    }

    fun closePaymentModal() {
        _uiState.update { it.copy(activePaymentGig = null) }
    }

    fun confirmGigPayment(gig: GigEntity, gateway: String) {
        viewModelScope.launch {
            val txnId = "gff_" + UUID.randomUUID().toString().take(12)
            repository.collectGigPayment(gig.id, gateway, txnId)
            _uiState.update {
                it.copy(
                    activePaymentGig = null,
                    toastMessage = "Collected $${gig.budget} via $gateway ($txnId)"
                )
            }
        }
    }

    fun openPaystackLeadModal(lead: LeadEntity) {
        _uiState.update {
            it.copy(
                activePaystackLead = lead,
                lastPaystackCheckoutUrl = null,
                lastPaystackReference = null
            )
        }
    }

    fun closePaystackLeadModal() {
        _uiState.update { it.copy(activePaystackLead = null) }
    }

    fun initializePaystackCheckout(leadId: Long, amount: Double, currency: String = "USD") {
        viewModelScope.launch {
            val lead = _uiState.value.leads.find { it.id == leadId } ?: return@launch
            _uiState.update { it.copy(isInitializingPaystack = true) }
            try {
                val result = repository.paystackService.initializeTransaction(
                    email = lead.email,
                    amount = amount,
                    currency = currency,
                    metadata = mapOf("lead_id" to leadId.toString(), "lead_name" to lead.name)
                )
                when (result) {
                    is PaystackApiResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isInitializingPaystack = false,
                                lastPaystackCheckoutUrl = result.data.authorization_url,
                                lastPaystackReference = result.data.reference,
                                toastMessage = "Checkout ready — open URL or copy reference"
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isInitializingPaystack = false,
                                toastMessage = "Paystack init failed"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isInitializingPaystack = false, toastMessage = "Paystack error: ${e.message}")
                }
            }
        }
    }

    fun verifyPaystackPayment(reference: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isVerifyingPaystack = true) }
            try {
                val result = repository.paystackService.verifyTransaction(reference)
                when (result) {
                    is PaystackApiResult.Success -> {
                        if (result.data.status == "success") {
                            val lead = _uiState.value.activePaystackLead
                            if (lead != null) {
                                val amount = result.data.amount / 100.0
                                repository.markLeadPaid(lead.id, amount, reference)
                            }
                            _uiState.update {
                                it.copy(
                                    isVerifyingPaystack = false,
                                    activePaystackLead = null,
                                    toastMessage = "Payment verified — lead marked PAID / CLOSED_WON"
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(isVerifyingPaystack = false, toastMessage = "Status: ${result.data.status}")
                            }
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(isVerifyingPaystack = false, toastMessage = "Verify failed")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isVerifyingPaystack = false, toastMessage = "Verify error: ${e.message}")
                }
            }
        }
    }

    fun markLeadPaidLocally(leadId: Long, amount: Double, reference: String) {
        viewModelScope.launch {
            repository.markLeadPaid(leadId, amount, reference)
            _uiState.update {
                it.copy(
                    activePaystackLead = null,
                    toastMessage = "Lead marked PAID — $${amount}"
                )
            }
        }
    }

    // ─── Grok features ────────────────────────────────────────────────────────

    fun generateTextToVideo(
        prompt: String, niche: String, formatStyle: String,
        targetPlatform: String, voiceTone: String, ctaType: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingTextToVideo = true, toastMessage = "Grok is writing your viral script…") }
            try {
                val script = GrokApiService.generateTextToVideoScript(prompt, niche, formatStyle, targetPlatform, voiceTone, ctaType)
                val project = VideoProjectEntity(
                    id = UUID.randomUUID().toString(),
                    title = prompt.take(60),
                    script = script,
                    niche = niche,
                    formatStyle = formatStyle,
                    targetPlatform = targetPlatform,
                    status = "DRAFT",
                    generatedViews = 0,
                    trafficDriven = 0,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertVideoProject(project)
                _uiState.update {
                    it.copy(isGeneratingTextToVideo = false, lastGrokScript = script, toastMessage = "Script ready — Grok generated a 3-scene viral video")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGeneratingTextToVideo = false, toastMessage = "Grok error: ${e.message}") }
            }
        }
    }

    fun generateSequenceStep(leadId: Long, sequenceName: String, stepTitle: String) {
        viewModelScope.launch {
            val lead = _uiState.value.leads.find { it.id == leadId } ?: return@launch
            try {
                val (subject, body) = GrokApiService.generateSequenceCopy(lead.name, lead.company, sequenceName, stepTitle)
                repository.insertSequence(
                    EmailSequenceEntity(
                        leadId = leadId, leadName = lead.name, leadEmail = lead.email,
                        sequenceName = sequenceName, stepNumber = 1, stepTitle = stepTitle,
                        subject = subject, bodyText = body, status = "DRAFT"
                    )
                )
                _uiState.update { it.copy(toastMessage = "Grok wrote sequence copy for ${lead.name}") }
            } catch (e: Exception) {
                _uiState.update { it.copy(toastMessage = "Grok sequence error: ${e.message}") }
            }
        }
    }

    fun enrichLeadWithGrok(leadId: Long) {
        viewModelScope.launch {
            val lead = _uiState.value.leads.find { it.id == leadId } ?: return@launch
            try {
                val result = LeadScoringEngine.calculateWithGrokInsight(
                    lead.name, lead.jobTitle, lead.company, lead.companySize,
                    lead.websiteActivity, lead.emailEngagement, lead.notes
                )
                repository.updateLeadScore(leadId, result.score, result.grade)
                _uiState.update { it.copy(lastGrokInsight = result.grokInsight, toastMessage = "Grok insight ready for ${lead.name}") }
            } catch (e: Exception) {
                _uiState.update { it.copy(toastMessage = "Grok insight error: ${e.message}") }
            }
        }
    }

    fun scanViralVideos(platform: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanningViralVideos = true) }
            repository.seedViralVideosIfNeeded()
            _uiState.update {
                it.copy(isScanningViralVideos = false, selectedViralPlatformFilter = platform, toastMessage = "Viral radar updated")
            }
        }
    }

    fun cloneViralWithGrok(video: ViralVideoEntity, newNiche: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCloningVideoProject = true) }
            try {
                val rewritten = GrokApiService.rewriteViralHook(video.title, video.platform, newNiche)
                val project = VideoProjectEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Clone: ${video.title.take(40)}",
                    script = rewritten,
                    niche = newNiche,
                    formatStyle = "AI Kinetic Motion",
                    targetPlatform = video.platform,
                    status = "DRAFT",
                    generatedViews = 0,
                    trafficDriven = 0,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertVideoProject(project)
                _uiState.update {
                    it.copy(isCloningVideoProject = false, lastGrokScript = rewritten, toastMessage = "Grok rewrote viral hook for $newNiche")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isCloningVideoProject = false, toastMessage = "Clone error: ${e.message}") }
            }
        }
    }
}

class FunnelViewModelFactory(private val repository: FunnelRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FunnelViewModel::class.java)) {
            return FunnelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
