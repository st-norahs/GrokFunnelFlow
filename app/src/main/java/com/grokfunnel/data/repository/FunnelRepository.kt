package com.grokfunnel.data.repository

import com.grokfunnel.data.local.AppDatabase
import com.grokfunnel.data.local.entities.*
import com.grokfunnel.data.remote.paystack.PaystackService
import com.grokfunnel.domain.LeadScoringEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FunnelRepository(private val db: AppDatabase) {

    val allLeads: Flow<List<LeadEntity>> = db.leadDao().getAllLeads()
    val allStages: Flow<List<PipelineStageEntity>> = db.pipelineStageDao().getAllStages()
    val allAgents: Flow<List<AgentEntity>> = db.agentDao().getAllAgents()
    val allSequences: Flow<List<EmailSequenceEntity>> = db.emailSequenceDao().getAllSequences()
    val allGigs: Flow<List<GigEntity>> = db.gigDao().getAllGigs()
    val top5Gigs: Flow<List<GigEntity>> = db.gigDao().getTop5Gigs()
    val recentLogs: Flow<List<ActivityLogEntity>> = db.activityLogDao().getRecentLogs()
    val crmConfigs: Flow<List<CrmConfigEntity>> = db.crmDao().getAllCrmConfigs()
    val paymentRecords: Flow<List<PaymentRecordEntity>> = db.paymentDao().getAllPayments()
    val teamSettings: Flow<TeamSettingsEntity?> = db.teamSettingsDao().getSettings()
    val paystackWebhookLogs: Flow<List<PaystackWebhookLogEntity>> = db.paystackWebhookLogDao().getAllLogs()
    val allViralVideos: Flow<List<ViralVideoEntity>> = db.viralVideoDao().getAllViralVideos()
    val allVideoProjects: Flow<List<VideoProjectEntity>> = db.videoProjectDao().getAllProjects()
    val trafficMonetization: Flow<TrafficMonetizationEntity?> = db.trafficMonetizationDao().getMonetization()
    val conversionLogs: Flow<List<MonetizationConversionLogEntity>> = db.monetizationConversionLogDao().getAllLogs()

    val paystackService = PaystackService()

    suspend fun initializeDefaultDataIfEmpty() {
        val existingStages = db.pipelineStageDao().getAllStages().first()
        if (existingStages.isEmpty()) seedPipelineStages()
        val existingLeads = db.leadDao().getAllLeads().first()
        if (existingLeads.isEmpty()) seedInitialData()
        val existingVideos = db.viralVideoDao().getAllViralVideos().first()
        if (existingVideos.isEmpty()) seedViralVideosAndProjects()
    }

    private suspend fun seedPipelineStages() {
        val defaultStages = listOf(
            PipelineStageEntity(key = "PROSPECTING", name = "Prospecting", orderIndex = 0, colorHex = "#38BDF8", defaultWinProbability = 0.15),
            PipelineStageEntity(key = "QUALIFICATION", name = "Qualification", orderIndex = 1, colorHex = "#818CF8", defaultWinProbability = 0.35),
            PipelineStageEntity(key = "PROPOSAL", name = "Proposal Sent", orderIndex = 2, colorHex = "#F59E0B", defaultWinProbability = 0.60),
            PipelineStageEntity(key = "NEGOTIATION", name = "Negotiation", orderIndex = 3, colorHex = "#F97316", defaultWinProbability = 0.80),
            PipelineStageEntity(key = "CLOSED_WON", name = "Closed Won", orderIndex = 4, colorHex = "#10B981", defaultWinProbability = 1.00, isWonStage = true),
            PipelineStageEntity(key = "CLOSED_LOST", name = "Closed Lost", orderIndex = 5, colorHex = "#EF4444", defaultWinProbability = 0.00, isLostStage = true)
        )
        db.pipelineStageDao().insertStages(defaultStages)
    }

    private suspend fun seedInitialData() {
        db.teamSettingsDao().insertOrUpdate(
            TeamSettingsEntity(
                teamName = "Grok Velocity Sales Group",
                currencySymbol = "$",
                monthlyQuota = 250000.0,
                autoAgentLeadGenEnabled = true,
                autoEmailSequenceEnabled = true,
                instantStripePayoutEnabled = true
            )
        )

        val initialAgents = listOf(
            AgentEntity(id = "agent_scout_alpha", name = "Hunter Scout AI", team = "Growth Automation", role = "Lead Discovery & Inbound", status = "SCANNING", leadsFound = 142, dealsClosed = 28, revenueGenerated = 98400.0, activeSequences = 18, accuracyRate = 96.4, avatarColorHex = "#0EA5E9"),
            AgentEntity(id = "agent_nurture_echo", name = "Cadence Sequence AI", team = "Inbound SMB", role = "Automated Email Sequences", status = "NURTURING", leadsFound = 89, dealsClosed = 34, revenueGenerated = 142500.0, activeSequences = 42, accuracyRate = 92.8, avatarColorHex = "#8B5CF6"),
            AgentEntity(id = "agent_closer_titan", name = "Titan Deal Negotiator", team = "Enterprise Core", role = "Proposal & Closing", status = "ACTIVE", leadsFound = 38, dealsClosed = 26, revenueGenerated = 185000.0, activeSequences = 9, accuracyRate = 98.1, avatarColorHex = "#10B981"),
            AgentEntity(id = "agent_gig_service", name = "Gig Automator Pro", team = "Growth Automation", role = "Monetization Gigs Fulfiller", status = "ACTIVE", leadsFound = 64, dealsClosed = 19, revenueGenerated = 78600.0, activeSequences = 6, accuracyRate = 95.0, avatarColorHex = "#F59E0B")
        )
        db.agentDao().insertAgents(initialAgents)

        val currentTime = System.currentTimeMillis()
        val day = 86400_000L

        fun createScoredLead(
            name: String, company: String, jobTitle: String, companySize: String,
            websiteActivity: String, emailEngagement: String, email: String, phone: String,
            dealValue: Double, stage: String, source: String, assignedAgent: String,
            notes: String, isCustomer: Boolean = false, paymentStatus: String = "UNPAID",
            paymentReference: String? = null, paidAmount: Double = 0.0,
            paymentGateway: String? = null, paidAt: Long? = null
        ): LeadEntity {
            val scoreResult = LeadScoringEngine.calculateScore(jobTitle, companySize, websiteActivity, emailEngagement)
            return LeadEntity(
                name = name, company = company, jobTitle = jobTitle, companySize = companySize,
                websiteActivity = websiteActivity, emailEngagement = emailEngagement,
                email = email, phone = phone, dealValue = dealValue, stage = stage,
                source = source, score = scoreResult.score, scoreGrade = scoreResult.grade,
                scoringBreakdown = scoreResult.breakdown, assignedAgent = assignedAgent,
                lastContactedAt = currentTime - day, notes = notes, isCustomer = isCustomer,
                paymentStatus = paymentStatus, paymentReference = paymentReference,
                paidAmount = paidAmount, paymentGateway = paymentGateway, paidAt = paidAt
            )
        }

        val initialLeads = listOf(
            createScoredLead("Elena Rostova", "HyperScale Logistics Inc.", "VP of Global Supply Chain", "Enterprise (500+)", "High (Visited Pricing & API Docs)", "Replied to Demo", "e.rostova@hyperscale.io", "+1 (415) 890-3211", 28000.0, "CLOSED_WON", "Automation Scout", "Titan Deal Negotiator", "Signed multi-year enterprise automation contract.", true, "PAID", "gff_elena_28k", 28000.0, "Paystack", currentTime - 2 * day),
            createScoredLead("Marcus Vance", "FinTech Orbit Global", "Chief Technology Officer", "Enterprise (500+)", "High (Visited Pricing)", "Replied to Demo", "marcus.v@orbitfintech.com", "+1 (212) 555-0194", 35000.0, "NEGOTIATION", "LinkedIn Outreach", "Titan Deal Negotiator", "Reviewing final SLA terms and billing setup."),
            createScoredLead("Dr. Aris Thorne", "Nexus Health Intelligence", "Head of Diagnostic AI", "Enterprise (500+)", "High (Visited Pricing)", "Opened & Clicked", "athorne@nexushealth.ai", "+1 (617) 492-8812", 42000.0, "PROPOSAL", "Website Inbound", "Cadence Sequence AI", "Proposal for 5 automated diagnostic intake funnels sent."),
            createScoredLead("Chloe Dubois", "LuxeRetail Brands", "Director of E-Commerce", "Mid-Market (51-500)", "Medium (Downloaded Case Study)", "Opened & Clicked", "cdubois@luxeretail.fr", "+33 1 42 68 55 00", 18500.0, "QUALIFICATION", "Gig Inbound", "Hunter Scout AI", "Need cart abandonment omni-channel recovery flow.")
        )
        db.leadDao().insertLeads(initialLeads)

        val initialGigs = listOf(
            GigEntity(id = "gig_001_lead_engine", title = "Autonomous Multi-Platform Lead Sourcing Engine", clientName = "VentureScale Capital LLC", clientRating = 4.95, sourcePlatform = "Upwork Enterprise", budget = 8500.0, costToFulfill = 650.0, profitMarginPercent = 92.4, isTop5HighMargin = true, status = "AVAILABLE", tags = "AI Agent • Python • Webhook", estimatedHours = 6, deliverableSummary = "Deploy headless multi-channel scraper with automated email verification.", createdAt = currentTime - day),
            GigEntity(id = "gig_002_crm_zapier", title = "Enterprise Sales Funnel Automation & Pipedrive Integration", clientName = "CloudForge SaaS Technologies", clientRating = 5.0, sourcePlatform = "Freelancer Pro", budget = 6200.0, costToFulfill = 480.0, profitMarginPercent = 92.2, isTop5HighMargin = true, status = "AVAILABLE", tags = "CRM • Zapier • Make.com", estimatedHours = 4, deliverableSummary = "Connect inbound demo requests to lead qualification bot.", createdAt = currentTime - 18 * 3600_000L),
            GigEntity(id = "gig_003_paystack_billing", title = "Paystack Custom Checkout & Instant Invoice Gateway", clientName = "Nordic Health Metrics", clientRating = 4.9, sourcePlatform = "GrowthBoard RFP", budget = 7800.0, costToFulfill = 700.0, profitMarginPercent = 91.0, isTop5HighMargin = true, status = "SERVICED", tags = "Fintech • Paystack API • Webhooks", estimatedHours = 5, assignedAgent = "Gig Automator Pro", deliverableSummary = "Tokenized payment collection flow completed.", createdAt = currentTime - 2 * day)
        )
        db.gigDao().insertGigs(initialGigs)

        db.activityLogDao().insertLog(ActivityLogEntity(actor = "System", category = "SYSTEM", title = "GrokFunnelFlow seeded", description = "Default pipeline, agents, leads and gigs loaded."))
    }

    private suspend fun seedViralVideosAndProjects() {
        val now = System.currentTimeMillis()
        val videos = listOf(
            ViralVideoEntity(id = "vv_yt_001", platform = "YouTube", title = "How 3 AI agents closed $48k while sleeping", creatorHandle = "@growthhacker", views = 1240000, likes = 89000, shares = 12400, engagementRate = 8.2, viralityScore = 94, velocityPerHour = 18200, category = "AI & Tech", hookText = "Stop writing cold emails at 2am", scriptSummary = "3-scene kinetic motion showing autonomous outreach", audioTrack = "Trending tech beat", hashtags = "#AI #SalesAutomation #Grok", scannedAt = now),
            ViralVideoEntity(id = "vv_tt_002", platform = "TikTok", title = "Paystack webhook that prints money", creatorHandle = "@fintechops", views = 890000, likes = 112000, shares = 34000, engagementRate = 11.4, viralityScore = 91, velocityPerHour = 22100, category = "FinTech", hookText = "One webhook. $450/day.", scriptSummary = "Screen demo of Paystack charge.success → auto invoice", audioTrack = "Original sound", hashtags = "#Paystack #SideHustle #Automation", scannedAt = now)
        )
        db.viralVideoDao().insertVideos(videos)
    }

    suspend fun insertVideoProject(project: VideoProjectEntity) {
        db.videoProjectDao().insertProject(project)
    }

    suspend fun insertSequence(sequence: EmailSequenceEntity) {
        db.emailSequenceDao().insertSequence(sequence)
    }

    suspend fun updateLeadScore(leadId: Long, score: Int, grade: String) {
        db.leadDao().updateLeadScore(leadId, score, grade, "Grok enriched")
    }

    suspend fun seedViralVideosIfNeeded() {
        val existing = db.viralVideoDao().getAllViralVideos().first()
        if (existing.isEmpty()) seedViralVideosAndProjects()
    }

    suspend fun collectGigPayment(gigId: String, gateway: String, txnId: String) {
        val now = System.currentTimeMillis()
        db.gigDao().recordPaymentCollected(gigId, gateway, txnId, now)
        db.activityLogDao().insertLog(ActivityLogEntity(actor = "Paystack", category = "PAYMENT", title = "Payment collected", description = "Gig $gigId paid via $gateway ($txnId)"))
    }

    suspend fun markLeadPaid(leadId: Long, amount: Double, reference: String, gateway: String = "Paystack") {
        db.leadDao().updateLeadPaymentStatus(leadId, "PAID", amount, reference, gateway, System.currentTimeMillis(), true, "CLOSED_WON")
        db.paymentDao().insertPayment(PaymentRecordEntity(id = reference, gigOrDealTitle = "Lead deal #$leadId", clientName = "Lead $leadId", amount = amount, fee = amount * 0.015, netAmount = amount * 0.985, gateway = gateway, status = "SUCCESS", timestamp = System.currentTimeMillis(), receiptCode = reference))
    }
}
