package com.grokfunnel.data.remote.grok

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/**
 * xAI Grok API client (OpenAI-compatible).
 * Base: https://api.x.ai/v1
 */
interface GrokApi {
    @POST("chat/completions")
    suspend fun chatCompletions(@Body request: GrokChatRequest): GrokChatResponse
}

@JsonClass(generateAdapter = true)
data class GrokChatRequest(
    val model: String,
    val messages: List<GrokMessage>,
    val temperature: Double = 0.7,
    @Json(name = "max_tokens") val maxTokens: Int = 2048,
    val stream: Boolean = false
)

@JsonClass(generateAdapter = true)
data class GrokMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class GrokChatResponse(
    val id: String? = null,
    val choices: List<GrokChoice> = emptyList(),
    val usage: GrokUsage? = null
)

@JsonClass(generateAdapter = true)
data class GrokChoice(
    val index: Int = 0,
    val message: GrokMessage? = null,
    @Json(name = "finish_reason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GrokUsage(
    @Json(name = "prompt_tokens") val promptTokens: Int = 0,
    @Json(name = "completion_tokens") val completionTokens: Int = 0,
    @Json(name = "total_tokens") val totalTokens: Int = 0
)

object GrokApiService {

    private const val BASE_URL = "https://api.x.ai/v1/"

    @Volatile
    var apiKey: String = ""
    @Volatile
    var model: String = "grok-4"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                    .header("Content-Type", "application/json")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            })
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val api: GrokApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GrokApi::class.java)
    }

    suspend fun chat(system: String, user: String, temperature: Double = 0.7, maxTokens: Int = 2048): String {
        require(apiKey.isNotBlank()) { "XAI_API_KEY is not set" }
        val request = GrokChatRequest(
            model = model,
            messages = listOf(
                GrokMessage("system", system),
                GrokMessage("user", user)
            ),
            temperature = temperature,
            maxTokens = maxTokens
        )
        val response = api.chatCompletions(request)
        return response.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("Empty Grok response")
    }

    suspend fun generateTextToVideoScript(
        prompt: String,
        niche: String,
        formatStyle: String,
        targetPlatform: String,
        voiceTone: String,
        ctaType: String
    ): String {
        val system = """
            You are an elite viral short-form video scriptwriter for YouTube Shorts and TikTok.
            Output ONLY a clean 3-scene script in this exact format:

            HOOK (0-3s):
            <one powerful opening line>

            SCENE 1:
            <visual + voiceover>

            SCENE 2:
            <visual + voiceover>

            SCENE 3:
            <visual + voiceover + CTA>

            CTA:
            <exact call-to-action text>

            Keep total spoken length under 45 seconds. Use the requested tone and niche.
        """.trimIndent()

        val user = """
            Topic / Hook concept: $prompt
            Niche: $niche
            Visual style: $formatStyle
            Platforms: $targetPlatform
            Voice tone: $voiceTone
            CTA goal: $ctaType
        """.trimIndent()

        return chat(system, user, temperature = 0.85, maxTokens = 1200)
    }

    suspend fun generateSequenceCopy(
        leadName: String,
        company: String,
        sequenceName: String,
        stepTitle: String,
        previousContext: String = ""
    ): Pair<String, String> {
        val system = """
            You are a top-performing B2B sales copywriter.
            Return ONLY valid JSON with two keys: "subject" and "body".
            Subject max 60 chars. Body max 180 words. Personal, direct, value-first.
        """.trimIndent()

        val user = """
            Lead: $leadName at $company
            Sequence: $sequenceName
            Current step: $stepTitle
            Context: $previousContext
        """.trimIndent()

        val raw = chat(system, user, temperature = 0.7, maxTokens = 600)
        val subject = Regex("\"subject\"\\s*:\\s*\"([^\"]+)\"").find(raw)?.groupValues?.get(1)
            ?: "Quick update for $leadName"
        val body = Regex("\"body\"\\s*:\\s*\"([^\"]+)\"").find(raw)?.groupValues?.get(1)
            ?: raw.take(500)
        return subject to body
    }

    suspend fun enrichLeadInsight(
        name: String,
        jobTitle: String,
        company: String,
        notes: String,
        score: Int
    ): String {
        val system = "You are a senior sales analyst. Give a 2-3 sentence actionable insight and next best action for this lead. Be concrete."
        val user = "Lead: $name ($jobTitle) at $company. Score: $score. Notes: $notes"
        return chat(system, user, temperature = 0.5, maxTokens = 300)
    }

    suspend fun rewriteViralHook(
        originalTitle: String,
        originalPlatform: String,
        newNiche: String
    ): String {
        val system = "Rewrite the viral hook into a fresh, high-retention version for the new niche. Output only the new hook + 3 bullet scene ideas."
        val user = "Original ($originalPlatform): $originalTitle\nNew niche: $newNiche"
        return chat(system, user, temperature = 0.9, maxTokens = 500)
    }
}
