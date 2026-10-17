# ADR 0003: PostgreSQL in production profiles, H2 in PostgreSQL mode for dev and test

Date: 2026-10-17 · Status: accepted

## Context
PostgreSQL is ADOPT at Zalando (Postgres Operator, Patroni). Four of five teammates are business students without Docker or a JDK; the prototype must be demonstrable by anyone on the team.

## Decision
Flyway migrations written in SQL that runs on both PostgreSQL 16 and H2 in `MODE=PostgreSQL`. Default profile targets PostgreSQL (docker compose); `dev` and `test` profiles use in-memory H2. The frontend additionally ships an in-browser mock so the click-dummy works with no backend at all.

## Consequences
- `./gradlew test` needs no database service; the HTTP flow test runs on H2.
- Migrations avoid PostgreSQL-only features (arrays, jsonb) until the prototype grows; `services` is a comma-separated string for that reason.
- Mock and backend rules must be kept in sync by hand (CONTRIBUTING.md).
