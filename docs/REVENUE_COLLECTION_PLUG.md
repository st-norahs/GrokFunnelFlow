# Revenue Collection Plug — Make GrokFunnelFlow Actually Earn Money

This document is the **practical playbook** to turn the app into a revenue-generating system using Paystack (already ported) + a lightweight payment plug.

---

## 1. Paystack remote module — step-by-step port (already done)

| Step | File | Action |
|------|------|--------|
| 1 | `PaystackModels.kt` | Request/response DTOs + `PaystackApiResult` sealed class |
| 2 | `PaystackApiService.kt` | Retrofit interface (`/transaction/initialize`, `/verify`, `/customer`) |
| 3 | `PaystackService.kt` | Client with live keys + sandbox fallback |
| 4 | Wire keys | `.env` → `PAYSTACK_SECRET_KEY` / `PAYSTACK_PUBLIC_KEY` |
| 5 | Call sites | `FunnelRepository.paystackService` + ViewModel payment methods |

**Usage in code:**

```kotlin
val result = repository.paystackService.initializeTransaction(
    email = lead.email,
    amount = lead.dealValue,
    currency = "USD",
    metadata = mapOf("lead_id" to lead.id.toString())
)
when (result) {
    is PaystackApiResult.Success -> openBrowser(result.data.authorization_url)
    else -> showError(result)
}
```

After user pays → call `verifyTransaction(reference)` → on success call `repository.markLeadPaid(...)` or `collectGigPayment(...)`.

---

## 2. Payment Collection Plug (the revenue engine)

### What it does
1. **Lead checkout** — one-click Paystack link from any lead card  
2. **Gig collection** — mark gig as paid + record net revenue  
3. **Webhook listener** (optional server) — auto-mark paid on `charge.success`  
4. **Grok upsell** — after payment, auto-generate thank-you + upsell sequence via Grok  

### Minimal plug architecture

```
[Lead / Gig Card]
       │
       ▼
[Collect Payment button]
       │
       ▼
PaystackService.initializeTransaction()
       │
       ▼
Open authorization_url (Chrome Custom Tab / WebView)
       │
       ▼
User pays on Paystack
       │
       ├─ (App) verifyTransaction()  OR
       └─ (Server) webhook charge.success
       │
       ▼
repository.markLeadPaid() / collectGigPayment()
       │
       ▼
Activity log + optional Grok “thank you + upsell” email
```

### Concrete revenue levers already in the app

| Lever | How it makes money |
|-------|--------------------|
| **Closed-won leads** | Collect deal value via Paystack → `markLeadPaid` |
| **Monetization gigs** | Fulfill → `collectGigPayment` → net = budget − cost |
| **Viral traffic** | Text-to-video → drive traffic → lead magnet / affiliate (TrafficMonetizationEntity) |
| **Agent sequences** | Grok-written nurture → higher close rate → more Paystack checkouts |

### Live keys (do this once)
1. Create Paystack account → https://dashboard.paystack.com  
2. Copy **Secret** + **Public** keys into `.env`  
3. (Production) Set webhook URL to your backend that calls `markLeadPaid`  

### Recommended next code additions
1. `PaymentCollectionViewModel` methods:
   - `startLeadCheckout(leadId)`
   - `startGigCheckout(gigId)`
   - `onPaystackCallback(reference)`
2. UI: “Collect $X” button on Lead card and Gig card  
3. Optional: tiny Node/Cloud Function webhook that verifies signature and POSTs to your app  

### Quick revenue math (from seed data)
- 1 closed deal @ $28k = **$28,000**  
- 1 gig @ $7.8k budget − $700 cost ≈ **$7,100** net  
- Viral lead magnet @ $49 × 100 conversions = **$4,900**  

Repeat with Grok-generated content + agent outreach → compounding.

---

## 3. Checklist to go live

- [ ] Replace test keys with live Paystack keys  
- [ ] Implement Chrome Custom Tab / WebView for `authorization_url`  
- [ ] Wire `verifyTransaction` after redirect or via webhook  
- [ ] Call `markLeadPaid` / `collectGigPayment` on success  
- [ ] (Optional) Grok thank-you + upsell sequence after payment  
- [ ] Track net revenue in Analytics tab  

Once the above is live, every Closed-Won lead and every Serviced gig becomes real cash in the Paystack balance.
