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
1. **Security First:** BCrypt password hashing, short-lived Access Tokens, revocable Refresh Tokens in DB.
2. **GDPR Readiness:** Mandatory support for soft delete (`deleted_at`), data export, and no sensitive data in logs.
3. **API Integrity:** Keep `docs/API.md` updated. Use unified JSON error models for all 4xx/5xx responses.
4. **Storage:** Support local filesystem (dev) and S3 (prod) through environment-driven media providers.
5. **Mandatory Build:** After every task or code modification, ALWAYS run `./gradlew build`. Changes here often require changes in `allterra-mobile` — run BOTH builds before declaring work done. A green server build does NOT excuse skipping the client build.
6. **Liquibase Immutability:** NEVER modify an already-applied changeset. Always create a NEW changeset for schema changes and register it in `master.xml`. Modifying a committed changeset breaks the checksum and prevents server startup.
7. **Checkstyle Compliance:** All Java code MUST follow the rules defined in `docs/CHECKSTYLE_RULES.md`. The build runs `checkstyleMain` automatically — any violation fails the build.
8. **Conventional Commits:** All commits MUST follow the format defined in `docs/CONTRIBUTION.md` (Conventional Commits 1.0.0). Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `perf`, `ci`, `build`. Always lowercase, imperative mood, no period.
9. **Git Staging Rule:** Always stage new files with `git add` so all created files are visible in diffs during review.
10. **Approval Rule:** Never run `git commit` or `git push` without explicit user acceptance.
11. **Task Workflow Rule:** For every new project task, create `.task/task_{n}/` in this repository, with `plan.md` at the start. Decompose the task into executable steps and execute strictly according to that plan. Keep `status.md` (live progress) and optionally `summary.md` (for handoff). Update after each iteration.

## References
- `PROJECT_CONTEXT.md` - Business vision and tech stack.
- `AI_RULES.md` - Core AI mandates.
- `docs/API.md` - The primary integration contract for mobile clients.
- `HELP.md` - Local setup and Docker instructions.
