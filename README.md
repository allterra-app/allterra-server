# allterra-server

## API documentation

Complete current API contract is documented in `docs/API.md`.
This is the primary integration document for Kotlin Compose Multiplatform clients (Android + iOS).

## Roles

The application supports two roles:

- `USER`: base role for regular API usage.
- `ADMIN`: management role. Can access admin endpoints and update user roles.

### Access rules

- `/auth/**` - public.
- `/admin/**` - `ADMIN`.
- `GET /users` - `ADMIN`.
- `PUT /users/{id}/roles` - `ADMIN`.
- `GET/PUT/DELETE /users/{id}`, `GET /users/{id}/export`, `POST /users/{id}/subscription/purchase` - owner or `ADMIN`.
- all other protected endpoints - `USER` or `ADMIN`.

All REST endpoints are served under `/api/v1`.

## Subscription

User now has subscription fields:

- `subscriptionPlan` (`FREE`, `MONTHLY`, `YEARLY`)
- `subscriptionStartedAt`
- `subscriptionExpiresAt`

### Buy subscription API

`POST /users/{id}/subscription/purchase`

Request example:

```json
{
  "plan": "MONTHLY",
  "durationDays": 30
}
```

`durationDays` is optional. If omitted, default duration from plan is used.

## GDPR endpoints

- Account deletion (soft delete): `DELETE /users/{id}`
- User data export: `GET /users/{id}/export`

## Admin seed user

A default admin user can be seeded on startup.

Environment variables:

- `SEED_ADMIN_ENABLED` (`true`/`false`, default `false`)
- `SEED_ADMIN_EMAIL` (default `admin@allterra.local`)
- `SEED_ADMIN_PASSWORD` (required when enabled)

Example:

```bash
export SEED_ADMIN_ENABLED=true
export SEED_ADMIN_EMAIL=admin@allterra.local
export SEED_ADMIN_PASSWORD='StrongAdminPassword123!'
```

If enabled and user does not exist, an admin account with role `ADMIN` is created.

## How to use ADMIN

### ADMIN

Recommended for production operations.

Typical flow:

1. Login as admin via `/auth/login`.
2. Use admin token for `/admin/status`.
3. Grant/revoke roles via `PUT /users/{id}/roles`.

Example role update request:

```json
{
  "roles": ["USER", "ADMIN"]
}
```

## JWT and roles

JWT contains `roles` claim. During authentication, roles are mapped to authorities as `ROLE_<NAME>`.

## Auth token flow

- `POST /auth/register` returns `accessToken` + `refreshToken`.
- `POST /auth/login` returns `accessToken` + `refreshToken`.
- `POST /auth/refresh` rotates refresh token and returns a new token pair.
- `DELETE /auth/logout` revokes refresh token.

Refresh tokens are stored in DB (`refresh_tokens`) in hashed form and can be revoked.
