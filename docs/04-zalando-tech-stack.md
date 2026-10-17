# Zalando tech stack and how this prototype matches it

Sources (accessed 17.10.2026): Zalando Tech Radar configuration (https://github.com/zalando/tech-radar, `docs/config.json`; rings ADOPT / TRIAL / ASSESS / HOLD), Zalando RESTful API Guidelines (https://opensource.zalando.com/restful-api-guidelines/), Zalando open-source repositories (GitHub API), Zalando Engineering Blog posts 2025 and 2026. Where a statement rests on a blog post, the post date is given.

## Zalando's published choices

| Area | Zalando (ADOPT unless noted) | Evidence |
|---|---|---|
| Backend languages | Java, Kotlin, Scala, Go, Python | Tech Radar languages quadrant; Skipper and Postgres Operator in Go; Java libraries Logbook, Problem, Riptide; Python for AI tooling (blog 14.08.2026) |
| API style | REST with OpenAPI first, GraphQL for aggregation | Tech Radar (OpenAPI, GraphQL on ADOPT); API guidelines; Zally linter (Kotlin) |
| Web frontend | TypeScript, JavaScript, React | Tech Radar; micro-frontends / "Interface Framework" (blog 03/2021) |
| Mobile | React Native with TypeScript on top of an internal rendering engine, GraphQL data layer; previously Swift and Kotlin | blog 03.10.2025 "Accelerating mobile app development at Zalando with Rendering Engine and React Native" |
| Datastores | PostgreSQL, Elasticsearch, AWS S3 / DynamoDB / ElastiCache / Redshift; MySQL, MongoDB, Redis, Cassandra on HOLD | Tech Radar datastores quadrant; Postgres Operator, Patroni, Spilo |
| Messaging / data | Kafka, Flink, Spark, Airflow, Databricks, AWS SNS/SQS/Kinesis; Nakadi archived 2024 | Tech Radar data quadrant; blogs 07/2026 (Flink), 12/2025 (Debezium); github.com/zalando/nakadi archive notice |
| Infrastructure | AWS, Kubernetes (EKS), Docker, CloudFormation, Skipper (HTTP router), OpenTelemetry, ZMON | Tech Radar infrastructure quadrant; blogs 04/2026 (Skipper webhook), 06/2026 (client-side load balancing) |
| Observability | OpenTelemetry, Lightstep with PromQL | Tech Radar; blog 06/2026 |
| AI engineering | LiteLLM proxy, Amazon Bedrock (TRIAL), Copilot / Claude Code, pydantic-ai, MCP, kagent | blog 14.08.2026 |
| On HOLD | Rust, Clojure, Haskell, GraalVM | Tech Radar |

## What the prototype uses and why

| Prototype | Matches | Deliberate deviation |
|---|---|---|
| Kotlin 2.2 + Spring Boot 3.5 on JDK 21 | Java/Kotlin backend, Spring ecosystem (Zalando's Riptide, Logbook and Problem are Spring/Java libraries) | Spring Boot's built-in `ProblemDetail` instead of `zalando/problem` to keep dependencies small |
| `api/openapi.yaml` first, served at `/openapi.yaml`, Swagger UI at `/docs` | API-first (guideline #100/#101), spec published | Zally not wired into CI; Spectral with two custom rules lints the spec |
| snake_case JSON, plural nouns, no verbs, `X-Flow-ID`, `application/problem+json`, `Money {amount, currency}` | guidelines #118, #134, #141, #176, #173 | no media-type versioning yet (single internal consumer) |
| PostgreSQL 16 + Flyway | PostgreSQL ADOPT | H2 in PostgreSQL mode for `dev` and `test` profiles so teammates need no Docker |
| React 19 + TypeScript + Vite | TypeScript/React web stack; React Native direction on mobile | Vite instead of Zalando's internal tooling; plain CSS instead of their design system |
| Docker Compose | Docker ADOPT | Kubernetes manifests and Skipper routes left for a later iteration |
| No Kafka in the prototype | — | events are described in docs/03; Kafka would carry them in production (Nakadi is archived) |
| Rust not used | Rust is HOLD at Zalando | the team's usual preference for Rust is set aside on purpose for this course |

## Zalando open-source projects referenced

| Project | Purpose | Status (17.10.2026) |
|---|---|---|
| zalando/restful-api-guidelines | the API rules this repo follows | active |
| zalando/zally | API linter for the guidelines (Kotlin) | active |
| zalando/skipper | HTTP router / ingress (Go) | active |
| zalando/postgres-operator, patroni/patroni, zalando/spilo | PostgreSQL on Kubernetes | active |
| zalando/logbook, zalando/problem, zalando/riptide | Java HTTP logging, problem JSON, Spring HTTP client | active |
| zalando/nakadi | event bus over Kafka | archived 07.06.2024 |
| zalando/jackson-datatype-money | money serialisation | archived; migrated to FasterXML (blog 03/2026) |

## Caveats

- The Tech Radar entries were read from the repository configuration, not from the rendered page; the ring mapping (0 = ADOPT … 3 = HOLD) follows the radar's own ordering. Nakadi still appears as ADOPT in that file although the project is archived, so the file is not perfectly current.
- Job advertisements and blog posts describe teams, not a company-wide mandate; Zalando lets teams choose within the radar.
