package com.grokfunnel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.grokfunnel.data.local.entities.*
import com.grokfunnel.data.remote.grok.GrokApiService
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

data class TrendPoint(
    val dayLabel: String,
    val revenue: Double,
    val conversionRate: Double
)

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
    val lastGrokInsight: String? = null
)

class FunnelViewModel(private val repository: FunnelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FunnelUiState())
    val uiState: StateFlow<FunnelUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
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
                repository.allVideoProjects
            ) { leads, stages, agents, settings, videos, projects ->
                Triple(leads, stages, agents) to Triple(settings ?: TeamSettingsEntity(), videos, projects)
            }.collect { (core, secondary) ->
                val (leads, stages, agents) = core
                val (settings, videos, projects) = secondary
                _uiState.update { current ->
                    current.copy(
                        leads = leads,
                        stages = stages,
                        agents = agents,
                        teamSettings = settings,
                        viralVideos = videos,
                        videoProjects = projects
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

    fun generateTextToVideo(
        prompt: String,
        niche: String,
        formatStyle: String,
        targetPlatform: String,
        voiceTone: String,
        ctaType: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingTextToVideo = true, toastMessage = "Grok is writing your viral script…") }
            try {
                val script = GrokApiService.generateTextToVideoScript(
                    prompt, niche, formatStyle, targetPlatform, voiceTone, ctaType
                )
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
                    it.copy(
                        isGeneratingTextToVideo = false,
                        lastGrokScript = script,
                        toastMessage = "Script ready — Grok generated a 3-scene viral video"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGeneratingTextToVideo = false,
                        toastMessage = "Grok error: ${e.message}"
                    )
                }
            }
        }
    }

    fun generateSequenceStep(leadId: Long, sequenceName: String, stepTitle: String) {
        viewModelScope.launch {
            val lead = _uiState.value.leads.find { it.id == leadId } ?: return@launch
            try {
                val (subject, body) = GrokApiService.generateSequenceCopy(
                    leadName = lead.name,
                    company = lead.company,
                    sequenceName = sequenceName,
                    stepTitle = stepTitle
                )
                repository.insertSequence(
                    EmailSequenceEntity(
                        leadId = leadId,
                        leadName = lead.name,
                        leadEmail = lead.email,
                        sequenceName = sequenceName,
                        stepNumber = 1,
                        stepTitle = stepTitle,
                        subject = subject,
                        bodyText = body,
                        status = "DRAFT",
                        sentAt = null,
                        openedAt = null
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
                    name = lead.name,
                    jobTitle = lead.jobTitle,
                    company = lead.company,
                    companySize = lead.companySize,
                    websiteActivity = lead.websiteActivity,
                    emailEngagement = lead.emailEngagement,
                    notes = lead.notes
                )
                repository.updateLeadScore(leadId, result.score, result.grade)
                _uiState.update {
                    it.copy(
                        lastGrokInsight = result.grokInsight,
                        toastMessage = "Grok insight ready for ${lead.name}"
                    )
                }
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
                it.copy(
                    isScanningViralVideos = false,
                    selectedViralPlatformFilter = platform,
                    toastMessage = "Viral radar updated"
                )
            }
        }
    }

    fun cloneViralWithGrok(video: ViralVideoEntity, newNiche: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCloningVideoProject = true) }
            try {
                val rewritten = GrokApiService.rewriteViralHook(
                    originalTitle = video.title,
                    originalPlatform = video.platform,
                    newNiche = newNiche
                )
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
                    it.copy(
                        isCloningVideoProject = false,
                        lastGrokScript = rewritten,
                        toastMessage = "Grok rewrote viral hook for $newNiche"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isCloningVideoProject = false, toastMessage = "Clone error: ${e.message}")
                }
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
