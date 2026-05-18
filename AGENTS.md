# AGENTS.md — Backend Application (Spring Boot)

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
5. **Mandatory Build:** After every task or code modification, ALWAYS run `./gradlew build` to ensure project stability.
6. **Sync Rule:** `AGENTS.md` and `GEMINI.md` must stay synchronized. Any change in one file must be mirrored in the other.

## References
- `PROJECT_CONTEXT.md` - Business vision and tech stack.
- `AI_RULES.md` - Core AI mandates.
- `docs/API.md` - The primary integration contract for mobile clients.
- `HELP.md` - Local setup and Docker instructions.
