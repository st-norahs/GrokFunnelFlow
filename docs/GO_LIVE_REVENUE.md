# GO LIVE — Revenue Path to Fund Your Grok Upgrade

This system is built so **closed deals + fulfilled gigs → real Paystack balance**. Use that cash for SuperGrok / API credits.

---

## What’s ready now

| Piece | Status |
|-------|--------|
| Grok API client (scripts, sequences, lead insights) | ✅ |
| Paystack init + verify + sandbox fallback | ✅ |
| Lead Collect Payment UI + mark PAID | ✅ |
| Gig Collect Payment UI + net revenue | ✅ |
| Seed data (leads, gigs, agents) | ✅ |
| Room DAOs + repository | ✅ |
| Version catalog + wrapper props | ✅ |
| Interactive web demo | ✅ |

---

## Money path (do this in order)

### 1. Paystack account (required for real cash)
1. Sign up: https://dashboard.paystack.com/signup  
2. Complete business verification (needed for live payouts).  
3. **Settings → API Keys & Webhooks** → copy **Live** Secret + Public keys.  
4. Put them in `.env` (never commit):

```env
XAI_API_KEY=xai-...
GROK_MODEL=grok-4
PAYSTACK_SECRET_KEY=sk_live_...
PAYSTACK_PUBLIC_KEY=pk_live_...
```

Test keys work for demos; **live keys** put money in your Paystack balance.

### 2. Collect on a lead (deal value)
1. Open app → **Leads** tab.  
2. Tap **Collect Payment** on an unpaid lead.  
3. **Initialize Checkout Link** → share URL with client (WhatsApp / email).  
4. Client pays on Paystack hosted page.  
5. **Verify** (or webhook later) → lead becomes `PAID` / `CLOSED_WON`.  
6. Funds appear in Paystack → **Withdraw** to your bank.

### 3. Collect on a gig (high margin)
1. **Gigs** tab → **Collect**.  
2. Choose Paystack (1.5% fee).  
3. **Authorize & Collect** → net = budget − fee logged in app.  
4. Same withdrawal path from Paystack dashboard.

### 4. Use Grok to *create* more deals (not just collect)
- **Viral Studio**: generate hooks → post → inbound leads.  
- **Agents / Sequences**: Grok-written nurture → higher close rate.  
- **Lead scoring**: prioritize who to Collect first.

More pipeline → more Collect taps → more Paystack balance → pay Grok subscription.

---

## Realistic first-week targets

| Action | Example | Net to you |
|--------|---------|------------|
| Close 1 mid deal | $5,000–$15,000 | Almost all (minus ~1.5%) |
| Fulfill 1 gig | $6k–$8k budget, 90%+ margin | ~$5.5k–$7k |
| Viral lead magnet | $29–$99 × conversions | Scales with traffic |

One solid close or two gigs can cover a month of SuperGrok + API usage.

---

## Still optional (next upgrades)

1. **Gradle wrapper jar** — open in Android Studio once; Studio generates `gradlew` if missing.  
2. **Chrome Custom Tab** — open `authorization_url` in-app instead of copy/paste.  
3. **Webhook server** — auto-mark paid without tapping Verify (Node/Cloud Function).  
4. **Grok thank-you + upsell** after payment (sequence already scaffolded).

None of these block collecting money today.

---

## Open the UI now

- Web: https://htmlpreview.github.io/?https://github.com/st-norahs/GrokFunnelFlow/blob/main/web/index.html  
- Android: clone → Android Studio → Run  
- Repo: https://github.com/st-norahs/GrokFunnelFlow  

**Priority:** live Paystack keys + Collect on one real client. Revenue first; polish second.
