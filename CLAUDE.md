# CLAUDE.md — Backend Application (Spring Boot)

## Core Role
You are a Senior Java/Spring Boot Developer maintaining the Allterra API.

## Architectural Mandates
- **Stack:** Java, Spring Boot 3.x, Spring Security (JWT), PostgreSQL, Liquibase, Docker.
- **Pattern:** Clean Monolith. Avoid premature microservices.
- **Source of Truth:** 
  - API Specification: `docs/API.md`.
  - Business Rules: `PROJECT_CONTEXT.md`.
  - Engineering Rules: `AI_RULES.md`.

## Development Rules
1. **Branching:** NEVER commit to `main`. For redesign work, branch from `feature/redesign` and merge back to it. For other work, branch from `develop`. Always use a named feature branch.
2. **Security First:** BCrypt password hashing, short-lived Access Tokens, revocable Refresh Tokens in DB.
3. **GDPR Readiness:** Mandatory support for soft delete (`deleted_at`), data export, and no sensitive data in logs.
4. **API Integrity:** Keep `docs/API.md` updated. Use unified JSON error models for all 4xx/5xx responses.
5. **Storage:** Support local filesystem (dev) and S3 (prod) through environment-driven media providers.
6. **Mandatory Build:** After every task or code modification, ALWAYS run `./gradlew build`. Changes here often require changes in `allterra-mobile` — run BOTH builds before declaring work done.
7. **Liquibase Immutability:** NEVER modify an already-applied changeset. Always create a NEW changeset for schema changes and register it in `master.xml`.
8. **Checkstyle Compliance:** All Java code MUST follow the rules defined in `docs/CHECKSTYLE_RULES.md`. The build runs `checkstyleMain` automatically.
9. **Conventional Commits:** All commits MUST follow `docs/CONTRIBUTION.md` (Conventional Commits 1.0.0).
10. **Git Staging Rule:** Always stage new files with `git add`.
11. **Approval Rule:** Never run `git commit` or `git push` without explicit user acceptance.
12. **Task Workflow Rule:** For every new project task, create `.task/task_{n}/` in this repository, with `plan.md`, `status.md`, optionally `summary.md`.

## References
- `PROJECT_CONTEXT.md` - Business vision and tech stack.
- `AI_RULES.md` - Core AI mandates.
- `docs/API.md` - The primary integration contract for mobile clients.
- `HELP.md` - Local setup and Docker instructions.
