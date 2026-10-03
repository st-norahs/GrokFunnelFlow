# Full Adaptation Guide – GrokFunnelFlow

## What was done

1. **Package rename** `com.example` → `com.grokfunnel`
2. **Theme** rebranded to Grok dark palette (cyan + violet)
3. **GrokApiService** – complete Retrofit client for `https://api.x.ai/v1`
4. **LeadScoringEngine** – hybrid rules + optional Grok insight
5. **FunnelViewModel** – wired:
   - `generateTextToVideo(...)` → real Grok script
   - `generateSequenceStep(...)` → Grok subject + body
   - `enrichLeadWithGrok(...)` → Grok insight
   - `cloneViralWithGrok(...)` → Grok rewrite of viral hooks
6. **Secrets** switched from GEMINI to XAI_API_KEY + GROK_MODEL
7. **metadata.json** updated
8. **README + CI workflow** added

## Remaining original files to port

From the private `Prompt-Audits` commit, copy these (they are large but stable):

- `app/src/main/java/.../data/local/*` (AppDatabase, DAOs, Entities)
- `app/src/main/java/.../data/remote/paystack/*`
- `app/src/main/java/.../data/repository/FunnelRepository.kt` (merge with new Grok methods)
- All remaining UI screens under `ui/` (dashboard, leads, agents, viral, analytics, crm, gigs…)
- `app/build.gradle.kts` – change namespace, remove `firebase.ai`, keep Room / Compose / Retrofit / Moshi
- Gradle version catalogs / wrapper

## Gradle changes (app/build.gradle.kts)

```kotlin
android {
  namespace = "com.grokfunnel"
  defaultConfig {
    applicationId = "com.grokfunnel.flow"
    // ...
    buildConfigField("String", "XAI_API_KEY", "\"${System.getenv("XAI_API_KEY") ?: ""}\"")
    buildConfigField("String", "GROK_MODEL", "\"${System.getenv("GROK_MODEL") ?: "grok-4"}\"")
  }
}

// Remove: implementation(libs.firebase.ai)
// Keep: retrofit, moshi, okhttp, room, compose, etc.
```

## Deploy checklist

1. Set `XAI_API_KEY` in GitHub Secrets (or local `.env`)
2. `./gradlew assembleRelease` (after adding signing config)
3. Upload APK to GitHub Releases or Firebase App Distribution
4. Optional: mirror key metrics to a Vercel dashboard using the same Grok client in Node/Python

## Expanded modules included

| Module              | Status                          |
|---------------------|---------------------------------|
| Grok API client     | Complete                        |
| Text-to-Video       | Complete + Grok scripts         |
| Agents / Sequences  | Complete + Grok copy            |
| Lead Scoring        | Hybrid rules + Grok insight     |
| Viral clone rewrite | Complete                        |
| Theme / Branding    | Grok dark palette               |
| ViewModel wiring    | Complete for all Grok paths     |
| CI build workflow   | Scaffold ready                  |
