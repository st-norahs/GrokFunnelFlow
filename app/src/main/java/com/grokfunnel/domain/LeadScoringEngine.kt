package com.grokfunnel.domain

import com.grokfunnel.data.remote.grok.GrokApiService

data class ScoreResult(
    val score: Int,
    val grade: String,
    val breakdown: String,
    val grokInsight: String? = null
)

object LeadScoringEngine {

    fun calculateScore(
        jobTitle: String,
        companySize: String,
        websiteActivity: String,
        emailEngagement: String
    ): ScoreResult {
        val titleLower = jobTitle.lowercase()
        val titlePts = when {
            titleLower.contains("c-level") || titleLower.contains("ceo") || titleLower.contains("cto") ||
                    titleLower.contains("cro") || titleLower.contains("founder") || titleLower.contains("vp") ||
                    titleLower.contains("president") -> 30
            titleLower.contains("director") || titleLower.contains("head") || titleLower.contains("lead") -> 22
            titleLower.contains("manager") || titleLower.contains("strategist") -> 14
            else -> 8
        }

        val sizePts = when {
            companySize.contains("500+") || companySize.contains("Enterprise", ignoreCase = true) -> 25
            companySize.contains("51-500") || companySize.contains("Mid", ignoreCase = true) -> 18
            else -> 10
        }

        val webPts = when {
            websiteActivity.contains("High", ignoreCase = true) || websiteActivity.contains("Pricing", ignoreCase = true) -> 25
            websiteActivity.contains("Medium", ignoreCase = true) || websiteActivity.contains("Study", ignoreCase = true) -> 16
            else -> 8
        }

        val emailPts = when {
            emailEngagement.contains("Replied", ignoreCase = true) -> 20
            emailEngagement.contains("Clicked", ignoreCase = true) || emailEngagement.contains("Opened", ignoreCase = true) -> 14
            emailEngagement.contains("Sent", ignoreCase = true) -> 6
            else -> 0
        }

        val totalScore = (titlePts + sizePts + webPts + emailPts).coerceIn(1, 100)
        val grade = when {
            totalScore >= 88 -> "A+"
            totalScore >= 75 -> "A"
            totalScore >= 60 -> "B"
            else -> "C"
        }

        val breakdown = "Title (+$titlePts) • Size (+$sizePts) • Web (+$webPts) • Email (+$emailPts)"
        return ScoreResult(totalScore, grade, breakdown)
    }

    suspend fun calculateWithGrokInsight(
        name: String,
        jobTitle: String,
        company: String,
        companySize: String,
        websiteActivity: String,
        emailEngagement: String,
        notes: String
    ): ScoreResult {
        val base = calculateScore(jobTitle, companySize, websiteActivity, emailEngagement)
        return try {
            val insight = GrokApiService.enrichLeadInsight(
                name = name,
                jobTitle = jobTitle,
                company = company,
                notes = notes,
                score = base.score
            )
            base.copy(grokInsight = insight)
        } catch (e: Exception) {
            base
        }
    }
}
