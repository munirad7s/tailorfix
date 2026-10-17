# ADR 0002: API-first OpenAPI contract with snake_case JSON and problem JSON

Date: 2026-10-17 · Status: accepted

## Context
Zalando's guidelines require the OpenAPI specification to exist before the implementation, snake_case property names, plural resource names without verbs, RFC 9457 problem details for errors and a flow id on every request.

## Decision
`api/openapi.yaml` is the single source of truth. The backend copies it into its static resources and serves it at `/openapi.yaml`; Swagger UI reads it from there (springdoc code generation is disabled). Jackson is configured for snake_case globally. Errors use Spring's `ProblemDetail`. A servlet filter generates or propagates `X-Flow-ID`. Redemption is modelled as a sub-resource (`POST /vouchers/{code}/redemptions`) to keep URLs free of verbs.

## Consequences
- Frontend types (`frontend/src/types.ts`) and Kotlin DTOs are written against the spec, which keeps the mock and the real backend aligned.
- Spectral lints the spec in CI with two Zalando-flavoured rules; Zally can be run for the complete rule set.
