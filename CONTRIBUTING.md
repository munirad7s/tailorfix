# Contributing

This is a course project with a hard deadline (final presentation 17./18.12.2026), so the rules are few and practical.

## Workflow
1. Pick or create an issue; every issue belongs to a milestone that matches a course session.
2. Branch from `main`: `feat/<short-topic>`, `docs/<short-topic>`, `fix/<short-topic>`.
3. Open a pull request early (draft is fine). Describe what changed and which hypothesis or session it serves.
4. Before merging: `cd backend && ./gradlew test` and `cd frontend && pnpm build` must pass locally.
5. Squash-merge, delete the branch.

## Rules for numbers
- Every figure in `docs/` carries a source (URL + date) or is marked as an assumption.
- Business assumptions that drive the prototype live in `backend/src/main/resources/application.yml` (`tailorfix.economics`) and in `EligibilityPolicy.kt`. Change them there, then update `docs/02-business-case.md`.
- Interview findings go to `docs/07-interview-guides.md` (guides) and `docs/06-prototype-plan.md` (what we learned per hypothesis). Personal data of interviewees never enters the repository.

## Code
- Kotlin: official code style, no comments that repeat the code, business rules in `domain/`, HTTP in `api/`.
- API changes start in `api/openapi.yaml` (API-first), then the Kotlin DTOs, then the frontend types.
- JSON on the wire is `snake_case`; errors are `application/problem+json`.
- Frontend: keep the mock (`frontend/src/mock.ts`) in sync with backend rules so the click-dummy stays truthful.

## AI tools
AI assistants are welcome (the course encourages it) and anything AI-generated that ends up in a graded document must be marked as such in that document. Code in this repository is reviewed by a human before merge.
