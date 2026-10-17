# TailorFix: the 1-Tap Local Tailor Fix for Zalando

Prototype for the Innovation Challenge of the FAU course **Implementing Innovation** (WI1, Prof. Möslein, WS 2026/27), Group 2.
Platform: **Zalando**. Secondary interaction: when a customer wants to return a garment because it does not fit,
Zalando offers a subsidised voucher for a **certified local alteration tailor** instead of the return.

> Student prototype. Not affiliated with Zalando SE. Brands, orders and tailors in the demo data are fictional.

## Why

- Zalando customers return about **50 % of ordered items**; Zalando itself attributes about **a third** of returns to size and fit problems, a 2026 Deloitte survey of German consumers puts fit at roughly two thirds ([docs/02-business-case.md](docs/02-business-case.md)).
- Each return costs the platform money (industry figures: 5 to 20 EUR per returned item) and the sale is lost. Many fit problems ("trousers 3 cm too long", "waist slightly loose") are a 15 to 35 EUR job for a tailor around the corner.
- Zalando already works on the problem from the other side (size advice, body measurement, virtual fitting room) and piloted a repair service in Berlin in 2021 ("Care & Repair", paused). TailorFix targets the moment those tools missed: the return intent itself.

The feature creates a new three-sided transaction between **shoppers**, **brand partners** and **local tailors** on top of Zalando's existing partner platform. Details, assumptions and sources: [docs/01-problem-and-idea.md](docs/01-problem-and-idea.md) and [docs/02-business-case.md](docs/02-business-case.md).

## What is in this repository

| Part | Stack | Why this stack |
|---|---|---|
| `api/openapi.yaml` | OpenAPI 3.0, API-first | Zalando RESTful API Guidelines: spec first, snake_case, problem JSON, `X-Flow-ID` ([docs/05-api-design.md](docs/05-api-design.md)) |
| `backend/` | Kotlin, Spring Boot 3.5, JDK 21, Flyway, PostgreSQL | Java/Kotlin and PostgreSQL are ADOPT on the Zalando Tech Radar ([docs/04-zalando-tech-stack.md](docs/04-zalando-tech-stack.md)) |
| `frontend/` | React 19, TypeScript, Vite | TypeScript/React is Zalando's web stack; the mobile apps moved to React Native in 2025 |
| `docker-compose.yml` | Postgres + backend + frontend (nginx) | Docker and Kubernetes are ADOPT at Zalando; compose is the local stand-in |
| `docs/` | Course context, business case, architecture, API design, prototype and interview plan, ADRs | The written part of the challenge |

Architecture overview: [docs/03-architecture.md](docs/03-architecture.md).

## Run it

### Fastest: click-dummy without any backend
```bash
cd frontend
pnpm install
pnpm dev            # http://localhost:5173, falls back to the in-browser mock when no backend answers
```

### Backend with in-memory database (no Docker needed)
```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'   # JDK 21 required
# API docs: http://localhost:8080/docs   spec: http://localhost:8080/openapi.yaml   H2 console: /h2-console
```
Then start the frontend as above; it detects the backend automatically.

### Everything with PostgreSQL
```bash
docker compose up --build
# frontend http://localhost:8081, backend http://localhost:8080
```

### Tests
```bash
cd backend && ./gradlew test     # unit tests for the eligibility policy and distance math, HTTP flow test on H2
cd frontend && pnpm build        # type check + production build
```

## The flow in 30 seconds

1. **Meine Bestellungen** → "Zurückgeben" on the suit trousers.
2. Reason **Zu lang** → `POST /return-intents` evaluates category, price and reason.
3. **Behalten und vor Ort anpassen lassen. Kostenlos.** Nearby certified tailors sorted by distance, voucher value, CO₂ badge.
4. **Gutschein holen** → `POST /vouchers` → QR code screen.
5. **Schneiderei-Portal** (`/tailor`): the tailor enters the code → `POST /vouchers/{code}/redemptions` → payout triggered.
6. Sneakers or "Gefällt mir nicht" fall back to the standard return.

Every business rule lives in one place: [`EligibilityPolicy.kt`](backend/src/main/kotlin/de/tailorfix/domain/EligibilityPolicy.kt), with the numbers in [`application.yml`](backend/src/main/resources/application.yml) under `tailorfix.economics`. They are working assumptions until the interviews (sessions 3 to 5) replace them.

## Course timeline this repo serves

| Date | Milestone | Repo deliverable |
|---|---|---|
| 22.10.2026 | Session 2, platform essentials | 5-minute Zalando overview (docs/00) |
| 29.10.2026 | Session 3, understanding the customer | interview guides (docs/07), first findings |
| 05.11.2026 | Session 4, business models | business case v1 (docs/02) |
| 12.11.2026 | Session 5, preparing implementation | prototype plan + hypotheses (docs/06) |
| 13.11.2026 12:00 | reflection paper (individual) | — |
| 18.11.2026 | intermediate presentation | demo v0 (this code) |
| 26.11.2026 | Session 7, prototyping | prototype v1 incl. feedback changes |
| 03.12.2026 | Session 8, evaluation and testing | test results in docs/06 |
| 16.12.2026 | final submission, presentations 17./18.12. | launch strategy + business case + evaluated prototype |

Full plan: [docs/00-course-context.md](docs/00-course-context.md). Work is tracked in GitHub issues and milestones.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Short version: branch from `main`, open a pull request, keep `./gradlew test` and `pnpm build` green, document every number you add with a source.

GitHub Actions is configured ([.github/workflows/ci.yml](.github/workflows/ci.yml)) but may be paused on this account; run the checks locally before merging.

## License

MIT, see [LICENSE](LICENSE).
