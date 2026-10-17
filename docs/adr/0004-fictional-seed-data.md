# ADR 0004: Fictional tailors and orders in the public seed data

Date: 2026-10-17 · Status: accepted

## Context
The prototype needs tailors around Nuremberg and Erlangen for the distance search and the pilot story. Real shops exist and will be interviewed, but listing them in a public repository as "certified partners" would misrepresent a relationship that does not exist.

## Decision
Seed data (`V3__seed_demo_tailors.sql`, `frontend/src/mock.ts`) contains fictional shop names at real street-level coordinates in both cities. The real interview list with addresses and phone numbers stays in the team's private notes. A shop that agrees to a pilot can be added with its consent.

## Consequences
- Demo looks realistic (distances, districts, turnaround) without naming anyone.
- Brands in the demo order are real brand names with generic product titles and typical list prices; no claim about those brands is made.
