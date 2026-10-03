package com.grokfunnel.data.repository

import com.grokfunnel.data.local.AppDatabase
import com.grokfunnel.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Repository skeleton. Wire full DAO implementations from original Prompt-Audits
 * and merge the Grok methods already called by FunnelViewModel.
 */
class FunnelRepository(private val db: AppDatabase) {

    val allLeads: Flow<List<LeadEntity>> = flowOf(emptyList())
    val allStages: Flow<List<PipelineStageEntity>> = flowOf(emptyList())
    val allAgents: Flow<List<AgentEntity>> = flowOf(emptyList())
    val allSequences: Flow<List<EmailSequenceEntity>> = flowOf(emptyList())
    val allGigs: Flow<List<GigEntity>> = flowOf(emptyList())
    val top5Gigs: Flow<List<GigEntity>> = flowOf(emptyList())
    val recentLogs: Flow<List<ActivityLogEntity>> = flowOf(emptyList())
    val crmConfigs: Flow<List<CrmConfigEntity>> = flowOf(emptyList())
    val paymentRecords: Flow<List<PaymentRecordEntity>> = flowOf(emptyList())
    val teamSettings: Flow<TeamSettingsEntity?> = flowOf(TeamSettingsEntity())
    val paystackWebhookLogs: Flow<List<PaystackWebhookLogEntity>> = flowOf(emptyList())
    val allViralVideos: Flow<List<ViralVideoEntity>> = flowOf(emptyList())
    val allVideoProjects: Flow<List<VideoProjectEntity>> = flowOf(emptyList())
    val trafficMonetization: Flow<TrafficMonetizationEntity?> = flowOf(TrafficMonetizationEntity())
    val conversionLogs: Flow<List<MonetizationConversionLogEntity>> = flowOf(emptyList())

    suspend fun initializeDefaultDataIfEmpty() {
        // Seed logic lives in original FunnelRepository – port when full DAOs are ready
    }

    suspend fun insertVideoProject(project: VideoProjectEntity) {
        // db.videoProjectDao().insert(project)
    }

    suspend fun insertSequence(sequence: EmailSequenceEntity) {
        // db.emailSequenceDao().insert(sequence)
    }

    suspend fun updateLeadScore(leadId: Long, score: Int, grade: String) {
        // db.leadDao().updateScore(leadId, score, grade)
    }

    suspend fun seedViralVideosIfNeeded() {
        // seed sample viral videos
    }
}
