package com.grokfunnel.data.local.dao

import androidx.room.*
import com.grokfunnel.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PipelineStageDao {
    @Query("SELECT * FROM pipeline_stages ORDER BY orderIndex ASC")
    fun getAllStages(): Flow<List<PipelineStageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStages(stages: List<PipelineStageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStage(stage: PipelineStageEntity): Long

    @Update
    suspend fun updateStage(stage: PipelineStageEntity)

    @Delete
    suspend fun deleteStage(stage: PipelineStageEntity)

    @Query("UPDATE pipeline_stages SET orderIndex = :newIndex WHERE id = :id")
    suspend fun updateStageOrder(id: Long, newIndex: Int)
}

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY score DESC, createdAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE stage = :stage ORDER BY dealValue DESC")
    fun getLeadsByStage(stage: String): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE id = :id")
    suspend fun getLeadById(id: Long): LeadEntity?

    @Query("SELECT * FROM leads WHERE email = :email LIMIT 1")
    suspend fun getLeadByEmail(email: String): LeadEntity?

    @Query("SELECT * FROM leads WHERE paymentReference = :reference LIMIT 1")
    suspend fun getLeadByPaymentReference(reference: String): LeadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeads(leads: List<LeadEntity>)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Query("UPDATE leads SET stage = :newStage, isCustomer = :isCustomer WHERE id = :id")
    suspend fun updateLeadStage(id: Long, newStage: String, isCustomer: Boolean)

    @Query("UPDATE leads SET score = :score, scoreGrade = :grade, scoringBreakdown = :breakdown WHERE id = :id")
    suspend fun updateLeadScore(id: Long, score: Int, grade: String, breakdown: String)

    @Query("UPDATE leads SET paymentStatus = :paymentStatus, paidAmount = :paidAmount, paymentReference = :reference, paymentGateway = :gateway, paidAt = :paidAt, isCustomer = :isCustomer, stage = :newStage WHERE id = :id")
    suspend fun updateLeadPaymentStatus(
        id: Long,
        paymentStatus: String,
        paidAmount: Double,
        reference: String?,
        gateway: String?,
        paidAt: Long?,
        isCustomer: Boolean,
        newStage: String
    )

    @Delete
    suspend fun deleteLead(lead: LeadEntity)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLeadById(id: Long)
}

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents ORDER BY revenueGenerated DESC")
    fun getAllAgents(): Flow<List<AgentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgents(agents: List<AgentEntity>)

    @Update
    suspend fun updateAgent(agent: AgentEntity)

    @Query("UPDATE agents SET status = :status WHERE id = :id")
    suspend fun updateAgentStatus(id: String, status: String)

    @Query("UPDATE agents SET leadsFound = leadsFound + :newLeads WHERE id = :id")
    suspend fun incrementLeadsFound(id: String, newLeads: Int)

    @Query("UPDATE agents SET dealsClosed = dealsClosed + 1, revenueGenerated = revenueGenerated + :dealValue WHERE id = :id")
    suspend fun recordClosedDeal(id: String, dealValue: Double)
}

@Dao
interface EmailSequenceDao {
    @Query("SELECT * FROM email_sequences ORDER BY sentAt DESC")
    fun getAllSequences(): Flow<List<EmailSequenceEntity>>

    @Query("SELECT * FROM email_sequences WHERE leadId = :leadId ORDER BY stepNumber ASC")
    fun getSequencesForLead(leadId: Long): Flow<List<EmailSequenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSequence(sequence: EmailSequenceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSequences(sequences: List<EmailSequenceEntity>)

    @Query("UPDATE email_sequences SET status = :status WHERE id = :id")
    suspend fun updateSequenceStatus(id: Long, status: String)
}

@Dao
interface GigDao {
    @Query("SELECT * FROM monetization_gigs ORDER BY isTop5HighMargin DESC, profitMarginPercent DESC")
    fun getAllGigs(): Flow<List<GigEntity>>

    @Query("SELECT * FROM monetization_gigs WHERE isTop5HighMargin = 1 ORDER BY profitMarginPercent DESC LIMIT 5")
    fun getTop5Gigs(): Flow<List<GigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGigs(gigs: List<GigEntity>)

    @Update
    suspend fun updateGig(gig: GigEntity)

    @Query("UPDATE monetization_gigs SET status = :status, assignedAgent = :agent, deliverableSummary = :summary WHERE id = :id")
    suspend fun updateGigServiceStatus(id: String, status: String, agent: String?, summary: String?)

    @Query("UPDATE monetization_gigs SET status = 'PAYMENT_COLLECTED', paymentGateway = :gateway, transactionId = :txnId, paymentCollectedAt = :timestamp WHERE id = :id")
    suspend fun recordPaymentCollected(id: String, gateway: String, txnId: String, timestamp: Long)
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<ActivityLogEntity>)
}

@Dao
interface CrmDao {
    @Query("SELECT * FROM crm_configs")
    fun getAllCrmConfigs(): Flow<List<CrmConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<CrmConfigEntity>)

    @Query("UPDATE crm_configs SET isConnected = :connected, lastSyncTimestamp = :syncTime, recordsSynced = recordsSynced + :newRecords WHERE crmName = :name")
    suspend fun updateSyncStatus(name: String, connected: Boolean, syncTime: Long, newRecords: Int)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_records ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecordEntity)
}

@Dao
interface TeamSettingsDao {
    @Query("SELECT * FROM team_settings WHERE id = 1")
    fun getSettings(): Flow<TeamSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: TeamSettingsEntity)
}

@Dao
interface PaystackWebhookLogDao {
    @Query("SELECT * FROM paystack_webhook_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogs(): Flow<List<PaystackWebhookLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PaystackWebhookLogEntity): Long

    @Query("DELETE FROM paystack_webhook_logs")
    suspend fun clearLogs()
}

@Dao
interface ViralVideoDao {
    @Query("SELECT * FROM viral_videos ORDER BY viralityScore DESC, views DESC")
    fun getAllViralVideos(): Flow<List<ViralVideoEntity>>

    @Query("SELECT * FROM viral_videos WHERE platform = :platform ORDER BY viralityScore DESC")
    fun getVideosByPlatform(platform: String): Flow<List<ViralVideoEntity>>

    @Query("SELECT * FROM viral_videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: String): ViralVideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<ViralVideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: ViralVideoEntity)

    @Query("UPDATE viral_videos SET isCloned = :cloned WHERE id = :id")
    suspend fun markCloned(id: String, cloned: Boolean)
}

@Dao
interface VideoProjectDao {
    @Query("SELECT * FROM video_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): VideoProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VideoProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<VideoProjectEntity>)

    @Update
    suspend fun updateProject(project: VideoProjectEntity)

    @Query("UPDATE video_projects SET status = :status, postedAt = :postedAt, generatedViews = generatedViews + :views, trafficDriven = trafficDriven + :traffic, monetizationRevenue = monetizationRevenue + :revenue WHERE id = :id")
    suspend fun updateProjectMetrics(id: String, status: String, postedAt: Long?, views: Long, traffic: Long, revenue: Double)

    @Query("DELETE FROM video_projects WHERE id = :id")
    suspend fun deleteProject(id: String)
}

@Dao
interface TrafficMonetizationDao {
    @Query("SELECT * FROM traffic_monetization WHERE id = 1")
    fun getMonetization(): Flow<TrafficMonetizationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: TrafficMonetizationEntity)

    @Query("UPDATE traffic_monetization SET totalVisitors = totalVisitors + :visitors, totalViews = totalViews + :views, leadMagnetRevenue = leadMagnetRevenue + :leadMagnetAdd, affiliateCommissionRevenue = affiliateCommissionRevenue + :affiliateAdd, adCpmRevenue = adCpmRevenue + :cpmAdd, sponsorshipRevenue = sponsorshipRevenue + :sponsorAdd WHERE id = 1")
    suspend fun addTrafficRevenue(visitors: Long, views: Long, leadMagnetAdd: Double, affiliateAdd: Double, cpmAdd: Double, sponsorAdd: Double)

    @Query("UPDATE traffic_monetization SET totalPayoutWithdrawn = totalPayoutWithdrawn + :amount WHERE id = 1")
    suspend fun recordPayout(amount: Double)
}

@Dao
interface MonetizationConversionLogDao {
    @Query("SELECT * FROM monetization_conversion_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogs(): Flow<List<MonetizationConversionLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MonetizationConversionLogEntity): Long
}
