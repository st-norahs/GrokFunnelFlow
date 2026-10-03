# GrokFunnelFlow

**Sales funnel + payment collection powered by xAI Grok.**  
Close deals → Collect via Paystack → Fund your Grok upgrade.

## See it now (no install)

| Link | |
|------|--|
| **[Interactive web UI](https://htmlpreview.github.io/?https://github.com/st-norahs/GrokFunnelFlow/blob/main/web/index.html)** | Click Leads/Gigs → Collect Payment |
| [CDN mirror](https://cdn.jsdelivr.net/gh/st-norahs/GrokFunnelFlow@main/web/index.html) | Same demo |

## Go live & make money

Full playbook: **[docs/GO_LIVE_REVENUE.md](docs/GO_LIVE_REVENUE.md)**

1. Create Paystack account → live API keys in `.env`  
2. Open app → **Leads** or **Gigs** → **Collect Payment**  
3. Client pays → Verify / Mark Paid → cash in Paystack → withdraw  
4. Use Grok (Viral + Sequences) to fill the pipeline  

One closed deal or two high-margin gigs can cover SuperGrok + API usage.

## Features

- **Lead CRM & Pipeline** – stages, scoring, Collect Payment (Paystack)
- **Autonomous Agents & Sequences** – Grok-generated email copy
- **Viral Studio** – Grok text-to-video scripts + viral clone
- **Paystack** – init, verify, gig collection, net revenue logging
- **Analytics** – pipeline value, win rate, gig earnings
- **Grok Core** – xAI API (OpenAI-compatible)

## Android quick start

```bash
git clone https://github.com/st-norahs/GrokFunnelFlow.git
cd GrokFunnelFlow
cp .env.example .env
# XAI_API_KEY=xai-...
# PAYSTACK_SECRET_KEY=sk_live_...   # live keys = real money
# PAYSTACK_PUBLIC_KEY=pk_live_...
```

Open in **Android Studio** → Sync Gradle → Run (minSdk 24).

Studio will fetch the Gradle distribution from `gradle/wrapper/gradle-wrapper.properties` on first sync.

## Environment

```env
XAI_API_KEY=your_xai_key_here
GROK_MODEL=grok-4
PAYSTACK_SECRET_KEY=sk_live_...   # or sk_test_ for sandbox
PAYSTACK_PUBLIC_KEY=pk_live_...
```

## Architecture

```
com.grokfunnel
├── data/local          # Room + DAOs + seed
├── data/remote/grok    # GrokApiService
├── data/remote/paystack
├── data/repository     # FunnelRepository + payment helpers
├── domain              # LeadScoringEngine
└── ui                  # Dashboard, Leads, Gigs, payment dialogs
```

## Docs

- [GO_LIVE_REVENUE.md](docs/GO_LIVE_REVENUE.md) — revenue path  
- [REVENUE_COLLECTION_PLUG.md](docs/REVENUE_COLLECTION_PLUG.md) — Paystack plug design  
- [ADAPTATION.md](ADAPTATION.md) — Gemini → Grok notes  

## License

MIT — adapted from FunnelFlow structure for Grok.
