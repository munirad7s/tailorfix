# Business case (v0, 17.10.2026)

Status: working model with sourced inputs and explicit assumptions. The interviews in sessions 3 to 5 replace the assumptions; the final business case is due with the final presentation (16.12.2026).

## 1. Zalando in numbers (context)

| Figure | Value | Source |
|---|---|---|
| GMV 2025 | 17,560 million EUR (+14.7 %) | Zalando FY 2025 fact sheet, 12.03.2026 |
| Revenue 2025 | 12,346 million EUR | same |
| Adjusted EBIT 2025 | 591 million EUR (4.8 % margin) | same |
| Active customers | 62.0 million (FY 2025), 62.5 million (Q2 2026) | FY 2025 and Q2 2026 fact sheets |
| Orders 2025 | 278.6 million (≈ 63 EUR GMV per order, computed) | FY 2025 fact sheet |
| Partner brands / markets | more than 7,000 brands, 29 markets | Zalando corporate, 19.09.2025; Annual Report 2025 |
| Partner share of GMV | 32.1 % (2025) | Annual Report 2025 |
| Return rate | about 50 % of ordered items | corporate.zalando.com, "Returns at Zalando" |
| Size-related share of returns | about one third (Zalando); about two thirds of consumers name fit as the main reason (Deloitte DE survey, n = 1,000, April 2026) | Zalando corporate; Deloitte Consumer Industry Briefing, 03.05.2026 |
| Return window | 30 days since 07.01.2025 (previously 100), returns stay free | Händlerbund, 06.01.2025; FashionUnited, 31.12.2024 |
| Size tools effect | 8 % of size-related returns avoided in 2025; VFR pilots up to −40 % | Zalando corporate, 22.06.2026 |
| Care & Repair pilot | Berlin, Oct 2021 to 2023, pickup 5.95 EUR, paused | neuhandeln.de, 14.09.2023 |

Pro-forma growth in Q2 2026 was only +4.4 % GMV (reported +20.7 % includes ABOUT YOU). Retention of existing GMV therefore matters more than ever.

## 2. Cost of one return (inputs)

| Source | Figure |
|---|---|
| EHI, DACH retailers, Dec 2023 | 5 to 10 EUR per returned item on average; inspection and quality control the largest driver |
| EHI, 124 DACH retailers, Nov 2025 | 53 % of retailers: handling cost up to 10 EUR; 14 %: up to 20 EUR; 27 % cannot quantify |
| Uni Bamberg returns research, 2019 | 19.51 EUR per return shipment, about half of it transport |
| Zalando | does not disclose return costs |

**Working assumption:** 11 EUR direct cost per returned item (`tailorfix.economics.return-cost-cents = 1100`), sensitivity 5 to 20 EUR. On top comes the lost contribution of the sale; we use a retained gross margin of 40 % of the item price as a placeholder for Zalando-plus-brand economics (Zalando's own gross margin is in that region; the split between platform and brand is part of the brand interviews).

## 3. Unit economics per eligible item

Example: suit trousers, 89.95 EUR, reason "too long", hem alteration list price 15 EUR.

| | Standard return | TailorFix |
|---|---|---|
| Return handling | −11.00 EUR | 0 |
| Sale | lost (−35.98 EUR margin at 40 %) | kept |
| Voucher | 0 | −15.00 EUR (brand 7.50, Zalando 7.50) |
| Net for platform + brand vs. return | — | **+31.98 EUR better than the return** |
| CO₂ (transport back, conservative) | ≈ 500 g | 0 |

The rule in the prototype: offer only when `voucher ≤ return cost + retained margin`, and never more than 35 % of the item price. That makes a cheap T-shirt ineligible and keeps the voucher honest.

Two effects reduce the gain and must be measured:
- **Cannibalisation:** some customers would have kept the item anyway or paid the tailor themselves. Mitigation: show the offer only after a fit reason in the return flow (the customer has already decided to return), cap at N vouchers per customer per year.
- **Tailor-check failures:** if the alteration is not feasible, the return happens anyway and the visit was wasted. Mitigation: `requires_tailor_check` flag and clear expectations.

## 4. Funnel for a pilot (Nuremberg + Erlangen), formula with assumptions

```
eligible_offers_per_month =
  orders_in_region_per_month            (assumption, from Zalando density in a 0.8 million metro)
  × items_per_order                     (≈ 2.5, assumption)
  × return_rate                         (0.50, Zalando)
  × fit_share                           (0.33 to 0.66, Zalando vs. Deloitte)
  × alterable_premium_share             (0.15 to 0.25, assumption: suits, trousers, dresses, shirts, coats above 50 EUR)
  × "almost fits" share within fit      (0.3 to 0.5, hypothesis H1)
accepted_vouchers = eligible_offers × acceptance_rate   (0.2 to 0.4, hypothesis H2)
platform_gain      = accepted_vouchers × (return_cost + retained_margin − voucher_value) − programme_cost
```

Programme cost: tailor onboarding and certification (one visit per shop), settlement, support, fraud checks. Target for the pilot: break-even at a few hundred vouchers per month per city; the exact figure comes out of the interviews and the tailor rate.

## 5. Revenue model options (to be decided in session 4)

1. **Cost avoidance** (base case): Zalando and brand split the voucher; the platform earns by not losing the sale and the return cost.
2. **Service fee on the tailor side:** tailors pay a small commission per voucher job in exchange for guaranteed, prepaid volume (classic two-sided pricing: subsidise the side with the strong network effect, charge the other).
3. **Customer upsell:** alterations beyond the voucher value (e.g. full suit fitting) paid by the customer through Zalando, with a commission.
4. **Zalando Plus perk:** higher voucher cap for Plus members (16 million members, more than 40 % of GMV in 2025) as a retention feature.

## 6. Network effects and launch strategy (draft for Part 2)

- **Cross-side:** more certified tailors → shorter distance and faster turnaround → higher acceptance → more voucher volume → more tailors apply.
- **Data loop:** every alteration records what was wrong by how much per brand and size; feeds Zalando's size advice, which reduces future fit returns at the source.
- **Chicken and egg:** start in two dense cities (Nuremberg, Erlangen) with 15 to 25 certified tailors recruited in person, premium categories only, voucher fully funded by Zalando during the pilot, brands join once data shows retained sales. Tailors are onboarded as service partners in the zDirect partner programme (Connected Retail ends 30.06.2027).
- **Governance:** certification visit, standard price list per job, turnaround SLA, customer rating, dispute process.

## 7. Sources

- Zalando FY 2025 fact sheet (12.03.2026): https://corporate.zalando.com/sites/default/files/media-download/zalando-se_fy-2025_fact-sheet.pdf
- Zalando Q2 2026 fact sheet (04.08.2026): https://corporate.zalando.com/sites/default/files/media-download/Zalando-SE_Q2-2026_Fact-Sheet.pdf
- Zalando Annual Report 2025: https://corporate.zalando.com/sites/default/files/media-download/zalando-se_annual-report-2025_eng_261003_s.pdf
- Returns at Zalando: https://corporate.zalando.com/en/node/10413
- Zalando size and fit technology (22.06.2026): https://corporate.zalando.com/en/node/11013
- Zalando on excessive returns (19.09.2025): https://corporate.zalando.com/en/node/10824
- Return window 30 days: https://ohn.haendlerbund.de/themen/marktplaetze/zalando-reduziert-rueckgabefristen (06.01.2025); https://fashionunited.de/nachrichten/business/verkurzt-zalando-die-ruckgabefrist-von-100-auf-30-tage/2024123159592
- Deloitte Consumer Industry Briefing (03.05.2026): https://www.deloitte.com/de/de/our-thinking/industry-thinking/blogs/2026/consumer-industry-briefing-weniger-fashion-retouren-dank-ki.html
- EHI return costs (05.12.2023): https://www.boerse-online.de/dpa-afx/studie-retouren-kosten-onlinehaendler-im-schnitt-fuenf-bis-zehn-euro-334018.html
- EHI study 2025 (27.11.2025): https://www.verkehrsrundschau.de/nachrichten/transport-logistik/e-commerce-kaempft-mit-hohen-retouren-und-steigenden-kosten-3741060
- Uni Bamberg 19.51 EUR per return (2019): https://fashionunited.de/nachrichten/business/studie-retouren-kosten-im-schnitt-fast-20-euro/2019042931707
- Care & Repair status (14.09.2023): https://www.neuhandeln.de/beitraege/db/nh_148494.html
- Connected Retail discontinued (23.04.2026): https://fashionunited.de/nachrichten/business/zalando-konsolidiert-partnerschaftsmodelle-connected-retail-wird-eingestellt/2026042366425
