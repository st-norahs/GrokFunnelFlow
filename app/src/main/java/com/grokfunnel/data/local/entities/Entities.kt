package com.grokfunnel.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val company: String,
    val jobTitle: String = "Director of Growth",
    val companySize: String = "Mid-Market (51-500)",
    val websiteActivity: String = "High (Visited Pricing)",
    val emailEngagement: String = "Opened & Clicked",
    val email: String,
    val phone: String,
    val dealValue: Double,
    val stage: String,
    val source: String,
    val score: Int,
    val scoreGrade: String = "A",
    val scoringBreakdown: String = "",
    val assignedAgent: String,
    val lastContactedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = "",
    val currentSequenceStep: Int = 0,
    val isCustomer: Boolean = false,
    val paymentStatus: String = "UNPAID",
    val paymentReference: String? = null,
    val paidAmount: Double = 0.0,
    val paymentGateway: String? = null,
    val paidAt: Long? = null
)

@Entity(tableName = "pipeline_stages")
data class PipelineStageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val name: String,
    val orderIndex: Int,
    val colorHex: String,
    val defaultWinProbability: Double,
    val isWonStage: Boolean = false,
    val isLostStage: Boolean = false
)

@Entity(tableName = "agents")
data class AgentEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val team: String = "Enterprise Core",
    val role: String,
    val status: String,
    val leadsFound: Int,
    val dealsClosed: Int,
    val revenueGenerated: Double,
    val activeSequences: Int,
    val accuracyRate: Double,
    val avatarColorHex: String
)

@Entity(tableName = "email_sequences")
data class EmailSequenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val leadId: Long,
    val leadName: String,
    val leadEmail: String,
    val sequenceName: String,
    val stepNumber: Int,
    val stepTitle: String,
    val subject: String,
    val bodyText: String,
    val status: String,
    val sentAt: Long? = null,
    val openedAt: Long? = null
)

@Entity(tableName = "monetization_gigs")
data class GigEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val clientName: String,
    val clientRating: Double,
    val sourcePlatform: String,
    val budget: Double,
    val costToFulfill: Double,
    val profitMarginPercent: Double,
    val isTop5HighMargin: Boolean,
    val status: String,
    val tags: String,
    val estimatedHours: Int,
    val assignedAgent: String? = null,
    val deliverableSummary: String? = null,
    val paymentGateway: String? = null,
    val transactionId: String? = null,
    val paymentCollectedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val actor: String,
    val category: String,
    val title: String,
    val description: String,
    val amount: Double? = null,
    val badgeColorHex: String = "#38BDF8"
)

@Entity(tableName = "crm_configs")
data class CrmConfigEntity(
    @PrimaryKey
    val crmName: String,
    val isConnected: Boolean,
    val apiKeyMasked: String,
    val lastSyncTimestamp: Long,
    val recordsSynced: Int,
    val autoSyncEnabled: Boolean
)

@Entity(tableName = "payment_records")
data class PaymentRecordEntity(
    @PrimaryKey
    val id: String,
    val gigOrDealTitle: String,
    val clientName: String,
    val amount: Double,
    val fee: Double,
    val netAmount: Double,
    val gateway: String,
    val status: String,
    val timestamp: Long,
    val receiptCode: String
)

@Entity(tableName = "team_settings")
data class TeamSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val teamName: String = "Velocity Sales & Automation Group",
    val currencySymbol: String = "$",
    val monthlyQuota: Double = 250000.0,
    val autoAgentLeadGenEnabled: Boolean = true,
    val autoEmailSequenceEnabled: Boolean = true,
    val instantStripePayoutEnabled: Boolean = true,
    val leadScoringPreset: String = "B2B Enterprise"
)

@Entity(tableName = "paystack_webhook_logs")
data class PaystackWebhookLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String,
    val reference: String,
    val amount: Double,
    val currency: String = "USD",
    val customerEmail: String,
    val leadId: Long? = null,
    val leadName: String? = null,
    val signatureValid: Boolean,
    val signatureReceived: String,
    val status: String,
    val failureReason: String? = null,
    val rawPayload: String
)

@Entity(tableName = "viral_videos")
data class ViralVideoEntity(
    @PrimaryKey
    val id: String,
    val platform: String,
    val title: String,
    val creatorHandle: String,
    val views: Long,
    val likes: Long,
    val shares: Long,
    val engagementRate: Double,
    val viralityScore: Int,
    val velocityPerHour: Long,
    val category: String,
    val hookText: String,
    val scriptSummary: String,
    val audioTrack: String,
    val hashtags: String,
    val scannedAt: Long = System.currentTimeMillis(),
    val isCloned: Boolean = false
)

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey
    val id: String,
    val sourceViralId: String? = null,
    val sourcePlatform: String = "YouTube",
    val projectTitle: String = "",
    val title: String = "",
    val niche: String,
    val hook: String = "",
    val scene1Hook: String = "",
    val scene2Body: String = "",
    val scene3Cta: String = "",
    val captionsAndHashtags: String = "",
    val targetPlatform: String = "YouTube Shorts & TikTok",
    val formatStyle: String = "AI Kinetic Motion",
    val status: String = "DRAFT",
    val scheduledPostTime: Long = System.currentTimeMillis() + 3600_000L,
    val postedAt: Long? = null,
    val generatedViews: Long = 0,
    val trafficDriven: Long = 0,
    val monetizationRevenue: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val voiceTone: String = "Energetic Creator",
    val visualBrollPrompt: String = "",
    val scriptText: String = "",
    val script: String = ""
)

@Entity(tableName = "traffic_monetization")
data class TrafficMonetizationEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalVisitors: Long = 34820L,
    val totalViews: Long = 894500L,
    val overallConversionRate: Double = 4.82,
    val leadMagnetRevenue: Double = 14200.0,
    val affiliateCommissionRevenue: Double = 8950.0,
    val sponsorshipRevenue: Double = 6500.0,
    val adCpmRevenue: Double = 10820.0,
    val autoMonetizationEnabled: Boolean = true,
    val activeLeadMagnetTitle: String = "AI Funnel Automation Blueprint & Code",
    val leadMagnetPrice: Double = 49.0,
    val cpmRate: Double = 12.50,
    val affiliatePartnerName: String = "CloudScale CRM Enterprise",
    val affiliateCommissionRate: Double = 35.0,
    val totalPayoutWithdrawn: Double = 18500.0
)

@Entity(tableName = "monetization_conversion_logs")
data class MonetizationConversionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val channel: String,
    val sourceVideoTitle: String,
    val visitorInfo: String,
    val amountEarned: Double,
    val gatewayOrNetwork: String = "Paystack",
    val receiptOrRef: String
)
