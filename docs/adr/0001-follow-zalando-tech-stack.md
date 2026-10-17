# ADR 0001: Build the prototype on Zalando's published tech stack

Date: 2026-10-17 · Status: accepted

## Context
The course asks for a platform feature that could realistically be implemented on the chosen platform. The team lead usually prefers Rust for new systems; Rust is on HOLD in Zalando's Tech Radar, while Java/Kotlin, TypeScript/React, PostgreSQL, Kafka and Kubernetes are ADOPT.

## Decision
Use what Zalando publishes as its stack: Kotlin on the JVM with Spring Boot for the service, PostgreSQL, OpenAPI-first REST following the Zalando RESTful API Guidelines, React with TypeScript for the UI, Docker for packaging. Document every mapping in docs/04-zalando-tech-stack.md with sources.

## Consequences
- The prototype is credible as "this is how it would be built inside Zalando" and the presentation can cite the Tech Radar and the API guidelines.
- The team lead builds in Kotlin and TypeScript instead of Rust for this project; teammates without a JDK can still run the frontend in mock mode.
- Reverse-engineering Zalando's apps to learn the stack was rejected: public sources are sufficient and citable, decompiling would violate Zalando's terms and only reveal the client.
