# Problem and idea

## Problem

Online fashion lives with returns. Zalando states that on average about **50 % of ordered items are returned** and that **size-related issues account for around a third of all returns** (Zalando corporate site, "Returns at Zalando", accessed 17.10.2026). A Deloitte survey of 1,000 German consumers (April 2026) found that about **two thirds** had returned fashion items mainly because of fit. Every return means a shipment back, inspection, repackaging and often a markdown, plus CO₂ for the transport. German industry studies put the handling cost at **5 to 10 EUR per returned item** (EHI 2023) and up to **19.51 EUR per return shipment** including transport (Uni Bamberg returns research group, 2019). The sale itself is lost.

A relevant share of fit problems are not "wrong size" but "almost right": trousers two centimetres too long, a waistband slightly loose, sleeves a touch long. These are routine jobs for an alteration tailor (Änderungsschneiderei), priced at roughly 12 to 45 EUR in Franconia (to be validated in the interviews). Today the customer has two options: return the item or pay the tailor out of pocket. Most return.

## Idea: the 1-Tap Local Tailor Fix

In the return flow, right after the customer selects a fit-related reason, Zalando shows one more option:

> **Behalten und vor Ort anpassen lassen. Kostenlos.**
> A certified tailor near you shortens the hem. Zalando and the brand pay up to 15 EUR.

One tap issues a voucher (QR code) for a certified local tailor. The customer walks the garment over, the tailor scans the code and gets paid by Zalando's partner settlement. The return never happens.

### Why it is a *secondary interaction* in the course's sense

Zalando's core interaction is "customer buys fashion from Zalando or a partner brand". TailorFix adds a new value unit (an alteration job) exchanged between two actor groups that could not transact through Zalando before: **shoppers with a fit problem** and **local tailors**. **Brand partners** co-fund it because they keep the sale. The platform mediates matching, payment and quality (certification), which is exactly the facilitator role from Rochet & Tirole.

### Actor groups and what each one gets

| Actor | Pain today | Value from TailorFix |
|---|---|---|
| Shopper | item almost fits; returning is effort, re-ordering is a gamble | keeps the item, perfect fit, no cost, no package |
| Brand partner | loses the sale, pays its share of return handling, markdown risk | keeps the sale; co-funds a 7 to 20 EUR voucher instead |
| Zalando | return cost, lost GMV, CO₂, complexity | avoided return cost, retained GMV, sustainability story, fit data |
| Local tailor | irregular walk-in business, no digital channel, price pressure | planned jobs with prepaid vouchers, new customers, visibility |

### Scope guard rails (why "premium only")

The team discussion settled on garments where alteration is normal and the item value carries the voucher: suits, blazers, trousers, dresses, shirts, coats, skirts. Not shoes, accessories, sportswear or low-priced basics. The prototype encodes this as an eligibility policy (`EligibilityPolicy.kt`): fit reason + alterable category + item price above a floor + voucher below the expected saving.

## Prior art, honestly

- **Zalando Care & Repair** (Berlin, Oct 2021 until 2023, with Save Your Wardrobe): repair, cleaning and shoe service with pickup for 5.95 EUR, the customer paid the service. Paused, "could become a business model" (neuhandeln.de, 14.09.2023). Differences to TailorFix: triggered at the return moment, subsidised, no pickup logistics, only fit alterations, premium categories.
- **Zalando size tools**: size advice (−10 % returns on covered items), body measurement (1.5 million users), virtual fitting room pilots (up to −40 %), together avoiding 8 % of size-related returns in 2025 (Zalando, 22.06.2026). TailorFix addresses the residual that prediction cannot fix: length preferences, asymmetric bodies, "between sizes".
- **Sojo** (London; Arket, M&S), **Tilli** (France), **The Seam** (London), **Repair Rebels** (Düsseldorf): tailoring marketplaces embedded by brands, mostly pickup or mail-in and customer-paid. None is wired into the return decision of a platform.
- **Connected Retail**, Zalando's programme for brick-and-mortar stores, is being discontinued (technology switched off by 30.06.2027, partners move to the zDirect partner programme). Tailors would therefore be onboarded as **service partners** in the partner programme, not via Connected Retail.

## Open questions the interviews must answer

1. How often is "almost fits" the real reason behind a fit return? (shoppers)
2. Would customers trust a Zalando-certified tailor and walk there? Within what distance and time?
3. What do tailors charge for the five standard jobs, what capacity do they have, would they accept prepaid vouchers at a negotiated rate?
4. Is 50 / 50 cost sharing between Zalando and the brand realistic? (brand view)
5. Where does gaming start (claiming a fit issue to get a free alteration) and which caps prevent it?
