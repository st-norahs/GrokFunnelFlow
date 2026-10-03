# GrokFunnelFlow

**Sales funnel automation dashboard fully adapted for xAI Grok.**

Original: private `st-norahs/Prompt-Audits` (FunnelFlow + Gemini).  
Adapted: complete Grok-powered rewrite with expanded modules.

## Features

- **Lead CRM & Pipeline** – custom stages, scoring, filtering, search
- **Autonomous Agents & Sequences** – Grok-generated email copy and cadence
- **Viral Studio** – Text-to-Video script generation via Grok + viral radar + auto-poster
- **Paystack Integration** – payments, webhooks, lead monetization
- **Advanced Analytics** – forecast, source performance, team comparison, trends
- **Gig Scanner** – high-margin monetization opportunities
- **Grok Core** – real xAI API client (OpenAI-compatible)

## Quick Start

```bash
# 1. Clone
git clone https://github.com/st-norahs/GrokFunnelFlow.git
cd GrokFunnelFlow

# 2. Secrets
cp .env.example .env
# Add your key:
# XAI_API_KEY=xai-...
# GROK_MODEL=grok-4

# 3. Open in Android Studio (AGP 8+, Kotlin 2.0+, Compose)
# Sync Gradle → Run on device/emulator (minSdk 24)
```

## Environment

```
XAI_API_KEY=your_xai_key_here
GROK_MODEL=grok-4
# Optional Paystack
PAYSTACK_SECRET_KEY=sk_test_...
PAYSTACK_PUBLIC_KEY=pk_test_...
```

## Architecture

```
com.grokfunnel
├── data
│   ├── local          # Room DB, DAOs, entities
│   ├── remote
│   │   ├── grok       # GrokApiService (new)
│   │   └── paystack   # Paystack + webhooks
│   └── repository     # FunnelRepository (expanded)
├── domain             # LeadScoringEngine (hybrid rules + Grok)
└── ui
    ├── viral          # ViralStudio + TextToVideo (Grok scripts)
    ├── agents         # Agents & Sequences (Grok copy)
    ├── leads          # Lead management
    ├── dashboard      # Main funnel dashboard
    ├── analytics      # Advanced analytics
    └── viewmodel      # FunnelViewModel (Grok wired)
```

## Key Grok Integrations

| Module              | Grok Usage                                      |
|---------------------|-------------------------------------------------|
| Text-to-Video       | Generate 3-scene viral scripts + hooks + CTA    |
| Agents / Sequences  | Generate subject lines + body copy              |
| Lead Scoring        | Optional enrichment + insight summary           |
| Viral Radar clone   | Rewrite viral hooks into new niche scripts      |

## Deploy

### Local APK
```bash
./gradlew assembleRelease
# APK: app/build/outputs/apk/release/app-release.apk
```

### GitHub Releases
Push a tag → GitHub Actions builds and attaches the APK.

## License

MIT – adapted from original FunnelFlow structure for Grok.
