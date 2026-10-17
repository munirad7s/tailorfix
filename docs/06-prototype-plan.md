# Prototype and test plan

## Hypotheses (riskiest first)

| # | Hypothesis | How we test it | Threshold to continue | Owner / when |
|---|---|---|---|---|
| H1 | A meaningful share of fit returns on trousers, suits, dresses and shirts are "almost fits" (length, waist, sleeves) rather than wrong size | customer interviews, question on the last fit return | ≥ 30 % of fit returns described as small alterations | interviews until 29.10. |
| H2 | Customers accept "keep it, fix it locally for free" instead of returning, if the tailor is within ~2 km / 15 minutes and turnaround ≤ 5 days | interviews + prototype test with the offer screen | ≥ 25 % would accept in the scenario | 29.10. and 26.11. |
| H3 | Tailors have capacity and want planned, prepaid jobs at a negotiated rate | tailor interviews (Erlangen, Nuremberg) | ≥ 3 of 6 tailors would sign a pilot agreement at list price −20 % | until 05.11. |
| H4 | Standard jobs cost 12 to 45 EUR; a 15 to 35 EUR voucher covers them | price lists from tailors | median hem ≤ 20 EUR, waist ≤ 35 EUR | until 05.11. |
| H5 | Customers trust a Zalando-certified tailor more than a random one | interviews, A/B wording in the offer screen | certification named as a reason to accept by most | 26.11. |
| H6 | Return cost + retained margin exceeds the voucher for items ≥ 50 EUR | expert interviews (Bamberg returns research, FAU SCM), published figures | expert confirms 5 to 20 EUR handling range | until 05.11. |
| H7 | Abuse (claiming fit to get a free alteration) can be contained by caps and the "altered = final sale" rule | expert view, interview question "would you game it?" | no interviewee sees an easy exploit without the caps | 03.12. |

## MVP scope (what the prototype shows)

- Return flow with fit reasons and the TailorFix offer screen (customer).
- Tailor selection by distance, voucher value and turnaround.
- QR voucher and the tailor portal with redemption and payout message.
- Eligibility rules and economics visible and adjustable (`application.yml`).
- Fallbacks: ineligible items go to the standard return.

Out of scope for the course: real payments, real tailor onboarding, authentication, Zalando integration.

## Prototype fidelity by session

| Session | Fidelity | Material |
|---|---|---|
| 19.11. intermediate presentation | clickable web prototype (this repo, mock mode) | 3-minute live demo, screenshots in slides |
| 26.11. prototyping | prototype v1 after feedback; optional Figma variant for wording tests | printed screens for the canvas work |
| 03.12. evaluation | prototype v1 with test script | 8 to 10 customer tests, 3 tailor tests of the portal |

## Test script (customer, 10 minutes)

1. Scenario: "You ordered these trousers for a wedding next week. They are 3 cm too long. Please do what you would normally do." Observe where they click.
2. When the offer appears: think aloud. Note first reaction, questions, trust signals.
3. Task: choose a tailor and get the voucher. Measure completion and time.
4. Questions: Would you really walk there? How far is too far? What if the tailor says it cannot be done? Would you want to pay extra for more work? Any concern about Zalando knowing your tailor visit?
5. Alternative wording test: "Kostenlos" vs. "Zalando übernimmt bis zu 15 €".

## Test script (tailor, 10 minutes)

1. Show the portal on a phone: enter a code, redeem. Observe.
2. Questions: Would you accept prepaid jobs at this value? How many per week could you take? What would stop you (payment delay, Zalando terms, quality disputes)? Which jobs would you refuse?

## Metrics we report on 03.12. and 17./18.12.

- Offer acceptance rate in the scenario, completion rate and time to voucher.
- Stated maximum distance and acceptable turnaround.
- Tailor willingness: number of pilot-ready shops, agreed rate range.
- Business case inputs updated: voucher value, return cost range, fit share.
