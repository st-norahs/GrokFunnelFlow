package com.grokfunnel.domain

data class SalesForecast(
    val weightedPipelineValue: Double,
    val bestCaseForecast: Double,
    val conservativeForecast: Double,
    val day30Projection: Double,
    val day60Projection: Double,
    val day90Projection: Double,
    val velocityDaysAvg: Int
)

data class LeadSourcePerformance(
    val sourceName: String,
    val totalLeads: Int,
    val wonLeads: Int,
    val winRate: Double,
    val totalWonRevenue: Double,
    val avgDealValue: Double,
    val avgLeadScore: Int,
    val colorHex: String
)

data class TeamComparison(
    val teamName: String,
    val membersCount: Int,
    val dealsClosed: Int,
    val revenueWon: Double,
    val quotaAttainmentPercent: Double
)
