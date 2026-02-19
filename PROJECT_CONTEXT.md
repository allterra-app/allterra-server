# Project Context

## 1. Vision

This is a commercial mobile application built by a solo founder.

Goal:
Build a scalable, production-ready product (not a pet project) starting with a small alpha group (~50 users), with a clear path to public release in app stores.

The app is conceptually similar to Strava-like domain logic (user accounts + activity-related data), but currently focused on building a solid technical foundation.

Primary objective:
Build a clean, secure, scalable architecture from the beginning to avoid painful rewrites.

---

## 2. Business Intent

- 100% business-oriented project.
- Initial alpha: ~50 real users (friends and domain-related audience).
- Future: public publishing in:
  - Apple App Store
  - Google Play
- Monetization: subscription model (planned).
- Must comply with GDPR (EU users expected).

---

## 3. Tech Stack

### Mobile (Client)

- Kotlin
- Compose Multiplatform (Android + iOS)
- Clean-ish architecture
- Secure token storage:
  - Android: Encrypted storage
  - iOS: Keychain

Networking:
- Ktor or Retrofit (to be finalized)
- JWT-based authentication

---

### Backend

- Java
- Spring Boot
- Spring Security
- JWT authentication
- REST API (versioned: /api/v1)
- Dockerized

Database:
- PostgreSQL
- Liquibase for migrations

Planned deployment:
- AWS
- RDS (PostgreSQL)
- Docker container on EC2 / ECS (initially simple setup)

---

## 4. Current State

- Basic CRUD server exists.
- Login + password registration implemented (primitive).
- Running locally in Docker.
- No production-grade auth yet.
- No UI implemented yet (only Figma mockups exist).

---

## 5. Architectural Principles

1. Start as a clean monolith.
2. Avoid premature microservices.
3. Production-grade authentication from early stage.
4. Security-first mindset.
5. Business-ready foundation even at 50 users.
6. Keep complexity proportional to user base.

---

## 6. Authentication Requirements

Must include:

- BCrypt or Argon2 password hashing.
- Access token (short-lived).
- Refresh token (stored in DB, revocable).
- Email verification.
- Role system (USER / ADMIN).
- Soft delete (deleted_at field).

Future-proofing:
- OAuth/social login possible later.
- Subscription status attached to user entity.

---

## 7. User Model (Minimum)

Fields:

- id (UUID)
- email
- password_hash
- role
- email_verified
- subscription_status
- subscription_expires_at
- created_at
- updated_at
- deleted_at (nullable)

---

## 8. GDPR Readiness (Required)

Must support:

- Account deletion endpoint.
- Data export endpoint.
- No logging of sensitive data (passwords, tokens).
- Secure storage of secrets.
- HTTPS only in production.

---

## 9. Subscription Model (Planned)

Planned integration:

- In-App Purchases via:
  - Apple App Store
  - Google Play

Backend responsibilities:

- Receipt validation
- Subscription state tracking
- Webhook handling

---

## 10. AWS Strategy

Development phase:
- Fully local (Docker + Postgres).

Pre-alpha:
- Minimal AWS deployment:
  - EC2 (small instance)
  - RDS (micro)
  - HTTPS via ACM
  - Secrets Manager

No Kubernetes at early stage.
No microservices.

---

## 11. Non-Goals (For Now)

- No microservices.
- No event sourcing.
- No complex CQRS.
- No over-engineering.
- No premature scaling optimizations.

---

## 12. AI Agent Usage Rules

AI should:

- Suggest clean, maintainable solutions.
- Avoid unnecessary abstraction.
- Prefer simplicity over architectural hype.
- Assume future scaling, but optimize for current stage.
- Follow security best practices.
- Respect business-oriented development mindset.

AI should NOT:

- Introduce unnecessary frameworks.
- Add distributed complexity.
- Suggest microservices prematurely.
- Store secrets in code.
- Log sensitive user data.

---

## 13. Target Milestones

Phase 1:
Production-grade auth + basic domain logic.

Phase 2:
Compose UI + full auth flow.

Phase 3:
Alpha deployment (50 users).

Phase 4:
Store preparation + subscription integration.

---

End of context.
