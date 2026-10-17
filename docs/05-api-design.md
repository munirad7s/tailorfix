# API design against the Zalando RESTful API Guidelines

The contract is `api/openapi.yaml`. The backend serves the same file at `/openapi.yaml` (copied at build time) and Swagger UI at `/docs`.

| Guideline (number as on opensource.zalando.com/restful-api-guidelines) | How TailorFix applies it |
|---|---|
| #100 API first, #101 provide OpenAPI | spec written before the Kotlin code; DTOs mirror the schemas |
| #118 snake_case property names, #130 snake_case query parameters | `order_item_id`, `voucher_value`, `radius_km`; Jackson configured globally |
| #134 plural resource names, #141 no verbs in URLs | `/return-intents`, `/vouchers`, `/vouchers/{code}/redemptions` (a redemption is a resource, not an action) |
| #151 specify success and error responses, #176 problem JSON, #177 no stack traces | every operation lists 4xx responses with `application/problem+json`; Spring `ProblemDetail` via `@RestControllerAdvice` |
| #173 common money object | `Money {amount: decimal, currency: ISO 4217}` everywhere a price appears |
| #169 standard date/time formats | ISO 8601 UTC instants (`delivered_at`, `expires_at`, `redeemed_at`) |
| #240 enum values UPPER_SNAKE_CASE | `TOO_LONG`, `HEM_SHORTEN`, `ISSUED` |
| `X-Flow-ID` | accepted on every request, generated when missing, echoed in the response and put into the log MDC |
| 201 Created with `Location` | `POST /return-intents`, `POST /vouchers` |
| #115 no URL versioning, #116 semantic versioning | `info.version: 0.1.0`; media-type versioning only when a second consumer appears |
| #159 pagination | not needed yet (lists are bounded: one order, at most 5 tailors) |

## Resources

- `GET /order-items` – demo order of the prototype customer.
- `GET /tailors?lat&lon&radius_km` – certified tailors sorted by distance (Haversine, default radius 5 km, at most 5 results).
- `POST /return-intents` – registers the return attempt, runs the eligibility policy, returns an offer with nearby tailors or a rejection reason.
- `GET /return-intents/{return_intent_id}` – read back (used when the page is reloaded).
- `POST /vouchers` – issues the voucher for an offer at the chosen tailor (409 when already issued or expired).
- `GET /vouchers/{code}` – customer screen and tailor portal.
- `POST /vouchers/{code}/redemptions` – tailor redeems; 409 when already redeemed or expired.

## Error catalogue

| Situation | Status | `type` |
|---|---|---|
| unknown order item / tailor / offer / voucher | 404 | `https://tailorfix.dev/problems/not-found` |
| voucher already issued, already redeemed, expired | 409 | `.../conflict` |
| validation failure (missing field, out-of-range coordinate) | 400 | Spring's default problem detail |

## Linting

`.spectral.yaml` extends the OAS ruleset and adds two Zalando-flavoured checks (snake_case properties, plural first path segment). Run locally with `npx @stoplight/spectral-cli lint api/openapi.yaml --ruleset .spectral.yaml`. Zalando's own linter Zally (https://github.com/zalando/zally) can be pointed at the same file for the full rule set.
