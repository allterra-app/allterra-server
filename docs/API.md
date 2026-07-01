# Allterra Server API (v1)

This document describes the current backend API contract for mobile clients (Kotlin Compose Multiplatform: Android + iOS).

Base URL:

- Local: `http://localhost:8080/api/v1`
- All paths below are relative to `/api/v1`

Content type:

- Request: `Content-Type: application/json`
- Response: `application/json` (except `/test`, plain text)

Authentication:

- Access token in header: `Authorization: Bearer <access_token>`
- Refresh token is sent in request body for refresh/logout operations

## 1. Security and Access

Roles currently used:

- `USER`
- `ADMIN`

Route-level access:

- Public: `/auth/**`
- Admin-only: `/admin/**`
- Admin-only: `GET /users`
- Admin-only: `PUT /users/{id}/roles`
- All other routes: `USER` or `ADMIN`

Method-level access (important):

- `GET /users/{id}`, `PUT /users/{id}`, `DELETE /users/{id}`, `GET /users/{id}/export`, `PUT /users/{id}/verify-email`, `POST /users/{id}/subscription/purchase`
  - owner (`id == current user`) or `ADMIN`

JWT notes:

- JWT includes `roles` claim (`["USER"]`, `["USER","ADMIN"]`, etc.)
- Current secret is required (`JWT_SECRET`)
- Previous secret is optional for key rotation (`JWT_PREVIOUS_SECRET`)

## 2. Common Types

ID format:

- All IDs are UUID strings (`xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`)

Enums:

- `UserRole`: `USER`, `ADMIN`
- `SubscriptionPlan`: `FREE`, `MONTHLY`, `YEARLY`

Date/time:

- `LocalDateTime`: ISO-8601, e.g. `2026-02-19T18:10:00`

## 3. Error Model

The API now uses unified JSON error responses for 4xx/5xx statuses:

```json
{
  "status": "error",
  "code": 400,
  "error": "Bad Request",
  "message": "Validation failed for request body.",
  "path": "/api/v1/users",
  "timestamp": "2026-02-19T19:00:00Z",
  "validationErrors": {
    "email": "Email should be valid"
  }
}
```

Field notes:

- `validationErrors` is empty object when not applicable.
- `401` is returned for missing/invalid/expired access token.
- `403` is returned when token is valid but role/ownership is insufficient.
- `404` is returned for missing entities (post/poi/photo/etc).
- `404` is also returned for unknown routes.
- `415` is returned when request content type is not supported.
- `500` returns a safe message: `Internal server error.`

Client recommendation:

- Treat any non-2xx as error.
- Parse by `code`, `message`, and optional `validationErrors`.

## 4. Auth API

### POST `/auth/register`

Register new user and return token pair.

Request:

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123!"
}
```

Response `200`:

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<opaque_refresh_token>"
}
```

Possible errors:

- `409` if email already exists

### POST `/auth/login`

Authenticate and return token pair.

Request:

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123!"
}
```

Response `200`:

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<opaque_refresh_token>"
}
```

### POST `/auth/refresh`

Rotate refresh token and issue new token pair.

Request:

```json
{
  "refreshToken": "<opaque_refresh_token>"
}
```

Response `200`:

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<new_opaque_refresh_token>"
}
```

### DELETE `/auth/logout`

Revoke refresh token.

Request:

```json
{
  "refreshToken": "<opaque_refresh_token>"
}
```

Response:

- `204 No Content`

## 5. Admin API

### GET `/admin/status`

Requires `ADMIN`.

Response `200`:

```json
{
  "status": "ok",
  "scope": "admin"
}
```

## 6. User API

### UserResponseDto (current shape)

```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "emailVerified": true,
  "firstName": "string",
  "lastName": "string",
  "phoneNumber": "+12345678901",
  "birthDate": "dd-MM-yyyy",
  "city": "string",
  "roles": ["USER"],
  "subscriptionPlan": "FREE",
  "subscriptionStartedAt": "2026-02-19T18:10:00",
  "subscriptionExpiresAt": "2026-03-21T18:10:00",
  "deletedAt": null,
  "userPhoto": { "id": "uuid", "url": "https://..." },
  "posts": [],
  "pois": []
}
```

### GET `/users`

Requires `ADMIN`.

Response `200`: `UserResponseDto[]`

### GET `/users/{id}`

Requires owner or `ADMIN`.

Response:

- `200` with `UserResponseDto`
- `404` if user not found

### POST `/users`

Protected endpoint (`USER` or `ADMIN` by global rule).

Request (`UserCreateRequestDto`):

```json
{
  "username": "john",
  "password": "StrongPassword123!",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+12345678901",
  "birthDate": "31-12-1990",
  "city": "Berlin",
  "userPhoto": null,
  "posts": [],
  "pois": []
}
```

Validation:

- `username`, `password`, `email`, `phoneNumber` required
- phone regexp: `\+?[0-9]{10,15}`
- birthDate regexp: `dd-MM-yyyy`

Response `200`: `UserResponseDto`

### PUT `/users/{id}`

Requires owner or `ADMIN`.

Request (`UserUpdateRequestDto`):

```json
{
  "id": "uuid",
  "username": "john",
  "password": "newPassword",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+12345678901",
  "birthDate": "31-12-1990",
  "city": "Berlin",
  "userPhoto": "https://cdn.example.com/avatar.jpg",
  "subscriptionPlan": "MONTHLY",
  "roles": ["USER"]
}
```

Response:

- `200` with `UserResponseDto`
- `404` if user not found

### DELETE `/users/{id}`

Requires owner or `ADMIN`.

Behavior:

- soft delete (`deletedAt` is set)

Response:

- `204 No Content`

### PUT `/users/{id}/roles`

Requires `ADMIN`.

Request:

```json
{
  "roles": ["USER", "ADMIN"]
}
```

Response:

- `200` with `UserResponseDto`
- `404` if user not found

### POST `/users/{id}/subscription/purchase`

Requires owner or `ADMIN`.

Request:

```json
{
  "plan": "MONTHLY",
  "durationDays": 30
}
```

`durationDays` is optional. If omitted, default from plan is used (`MONTHLY=30`, `YEARLY=365`, `FREE=0`).

Response:

- `200` with updated `UserResponseDto`
- `404` if user not found

### GET `/users/{id}/export`

Requires owner or `ADMIN`.

Response:

- `200` with `UserResponseDto`
- `404` if user not found

### PUT `/users/{id}/verify-email`

Requires owner or `ADMIN`.

Response:

- `200` with updated `UserResponseDto`
- `404` if user not found

## 7. Post API

### PostResponseDto (current shape)

```json
{
  "id": "uuid",
  "user": { "id": "uuid", "email": "user@example.com" },
  "title": "My post",
  "body": "Text",
  "audience": "PUBLIC",
  "tripId": "uuid",
  "routeId": "uuid",
  "photos": [],
  "createdAt": "2026-05-27T10:00:00"
}
```

### POST `/posts`

Request (`PostCreateRequestDto`):

```json
{
  "userId": "uuid",
  "title": "My post",
  "body": "Text",
  "audience": "PUBLIC",
  "tripId": "uuid",
  "routeId": "uuid"
}
```

Validation:

- `userId` required
- `title` required
- `tripId`, `routeId` must exist if provided

Response `200`: `PostResponseDto`

### POST `/posts/users/{userId}`

Same payload as `/posts`, explicitly tied to `userId`.

Response `200`: `PostResponseDto`

### GET `/posts`

Response `200`: `PostResponseDto[]`

### GET `/posts/{id}`

Response:

- `200` with `PostResponseDto`
- `404` when controller gets `null` from service

### GET `/posts/users/{userId}`

Returns all posts for a specific user.

Response `200`: `PostResponseDto[]`

### GET `/posts/users/{userId}/saved`

Returns saved posts for this user.

Response `200`: `PostResponseDto[]`

### PUT `/posts/users/{userId}/saved/{postId}`

Saves post for this user.

Response:
- `204 No Content`

### DELETE `/posts/users/{userId}/saved/{postId}`

Removes saved post for this user.

Response:
- `204 No Content`

### GET `/posts/users/{userId}/{postId}`

Response `200`: `PostResponseDto`

### PUT `/posts/{postId}`

Request (`PostUpdateRequestDto`):

```json
{
  "title": "Updated title",
  "body": "Updated text",
  "audience": "FRIENDS",
  "tripId": "uuid",
  "routeId": "uuid"
}
```

Response `200`: `PostResponseDto`

### DELETE `/posts/{postId}`

Response:

- `204 No Content`

### DELETE `/posts/users/{userId}/{postId}`

Response:

- `204 No Content`

## 8. Feed API

### FeedPageResponseDto

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalItems": 100,
  "hasNext": true
}
```

### GET `/feed`

Returns paginated community feed.

Query parameters:
- `page` (default 0)
- `size` (default 20, max 50)
- `audience` (optional: `PUBLIC`, `FRIENDS`, `CLUB`)

Response `200`: `FeedPageResponseDto`

## 9. Notification API

### NotificationResponseDto

```json
{
  "id": "uuid",
  "title": "Notification title",
  "body": "Notification body",
  "read": false,
  "createdAt": "2026-05-27T10:00:00"
}
```

### GET `/notifications`

Returns notifications for current user.

Response `200`: `NotificationResponseDto[]`

### PUT `/notifications/{id}/read`

Marks notification as read.

Response `200`: `NotificationResponseDto`

## 10. POI API

### PoiResponseDto (current shape)

```json
{
  "id": "uuid",
  "name": "POI name",
  "description": "Desc",
  "users": [],
  "rating": 5,
  "actual": true,
  "url": "https://...",
  "poiPhoto": null,
  "createdAt": "2026-02-19T18:10:00",
  "modifiedAt": "2026-02-19T18:10:00"
}
```

### POST `/pois`

Request (`PoiCreateRequestDto`):

```json
{
  "user": { "id": "uuid" },
  "name": "POI",
  "description": "Desc",
  "rating": 4,
  "actual": true,
  "url": "https://...",
  "poiPhoto": null
}
```

Validation:

- `user` required
- `name` required

Response `200`: `PoiResponseDto`

### POST `/pois/users/{userId}`

Same payload as `/pois`, explicitly tied to `userId`.

Response `200`: `PoiResponseDto`

### GET `/pois`

Response `200`: `PoiResponseDto[]`

### GET `/pois/{id}`

Response `200`: `PoiResponseDto`

### GET `/pois/users/{userId}`

Response `200`: `PoiResponseDto[]`

### GET `/pois/users/{userId}/{poiId}`

Response `200`: `PoiResponseDto`

### PUT `/pois/{poiId}`

Request (`PoiUpdateRequestDto`):

```json
{
  "id": "uuid",
  "name": "Updated POI",
  "description": "Updated desc",
  "users": [],
  "rating": 5,
  "actual": true,
  "url": "https://...",
  "poiPhoto": null
}
```

Response `200`: `PoiResponseDto`

### DELETE `/pois/{poiId}`

Response:
- `204 No Content`

## 9. Documents API

Primary aggregator for PDFs, tickets, and bookings.

### DocumentResponseDto (current shape)

```json
{
  "id": "uuid",
  "title": "Flight Ticket",
  "type": "TICKET",
  "fileId": "uuid",
  "tripId": "uuid",
  "fileUrl": "/api/v1/files/{fileId}",
  "metadata": "string",
  "createdAt": "2026-02-19T18:10:00",
  "modifiedAt": "2026-02-19T18:10:00"
}
```

### GET `/documents`

Returns all documents for the current user.

Response `200`: `DocumentResponseDto[]`

### GET `/documents/{id}`

Response `200`: `DocumentResponseDto`

### POST `/documents`

Request (`DocumentRequestDto`):

```json
{
  "title": "Hotel Booking",
  "type": "BOOKING",
  "fileId": "uuid",
  "tripId": "uuid",
  "metadata": "{}"
}
```

Response `200`: `DocumentResponseDto`

### DELETE `/documents/{id}`

Response:
- `204 No Content`

## 10. Routes API

Route creation flow is file-based:

1. Upload GPX file to `/files/upload` (multipart) or `/files/upload/raw` (bytes) and receive `gpxFileId`.
2. Create route with `gpxFileId`.
3. Server reads GPX from media storage, parses metrics once, and stores parsed fields in DB.

Server-side notes:

- GPX parsing is done on backend (library-based parser), not on client.
- Parsed metrics are persisted (`distanceKm`, `durationMinutes`, `pointCount`, `startedAt`, `previewPoints`) and reused for reads.
- File access is ownership-checked (owner or `ADMIN`).

### RouteResponseDto (current shape)

```json
{
  "id": "uuid",
  "userId": "uuid",
  "title": "Morning route",
  "description": "By river",
  "gpxFileId": "uuid",
  "gpxFileName": "morning.gpx",
  "gpxContentType": "application/gpx+xml",
  "gpxFileUrl": "/api/v1/files/{gpxFileId}",
  "distanceKm": 7.42,
  "durationMinutes": 48,
  "pointCount": 3150,
  "startedAt": "2026-02-19T18:10:00",
  "previewPoints": [{ "lat": 51.84, "lon": 16.57 }],
  "createdAt": "2026-02-19T18:10:00",
  "modifiedAt": "2026-02-19T18:10:00"
}
```

### POST `/routes/users/{userId}`

Request (`RouteCreateRequestDto`):

```json
{
  "title": "Morning route",
  "description": "By river",
  "gpxFileId": "00000000-0000-0000-0000-000000000123"
}
```

Validation:

- `gpxFileId` required
- referenced file must be accessible by current user (or admin)
- uploaded file must be valid GPX with at least 2 points

Response `200`: `RouteResponseDto`

### GET `/routes/users/{userId}`

Response `200`: `RouteResponseDto[]`

### GET `/routes/{id}`

Response `200`: `RouteResponseDto`

### PUT `/routes/{routeId}`

Request (`RouteUpdateRequestDto`):

```json
{
  "title": "Updated route title",
  "description": "Updated description",
  "gpxFileId": "00000000-0000-0000-0000-000000000124"
}
```

Behavior:

- if `gpxFileId` changed, server reparses GPX and refreshes stored metrics
- if `gpxFileId` unchanged/omitted, existing parsed metrics remain

Response `200`: `RouteResponseDto`

### DELETE `/routes/{routeId}`

Response:

- `204 No Content`

### DELETE `/routes/users/{userId}/{routeId}`

Response:

- `204 No Content`

## 10. Media Files API

Media storage backend is environment-driven:

- local development: local filesystem storage
- production: S3 storage (`allterra.media.storage-type=s3`)

### POST `/files/upload` (multipart)

Form-data:

- `file`: binary file

Response `200`:

```json
{
  "id": "uuid",
  "fileName": "track.gpx",
  "contentType": "application/gpx+xml",
  "size": 12345,
  "url": "/api/v1/files/{id}"
}
```

### POST `/files/upload/raw` (bytes)

Query params:

- `fileName` (optional)
- `contentType` (optional)

Body: raw bytes.

Response `200`: same as `/files/upload`.

### GET `/files/{id}`

Returns binary content of uploaded file (`Content-Type` from stored metadata).

## 11. Photo API

### 11.1 User photos

Base path: `/user-photos`

Request DTO:

```json
{
  "url": "https://cdn.example.com/avatar.jpg",
  "user": { "id": "uuid" }
}
```

Response DTO:

```json
{
  "id": "uuid",
  "url": "https://cdn.example.com/avatar.jpg",
  "user": { "id": "uuid" }
}
```

Endpoints:

- `POST /user-photos`
- `GET /user-photos/{id}`
- `GET /user-photos`
- `PUT /user-photos/{id}`
- `DELETE /user-photos/{id}` (`204`)

### 11.2 Post photos

Base path: `/post-photos`

Request DTO:

```json
{
  "url": "https://cdn.example.com/post.jpg",
  "post": { "id": "uuid" }
}
```

Response DTO:

```json
{
  "id": "uuid",
  "url": "https://cdn.example.com/post.jpg",
  "post": { "id": "uuid" }
}
```

Endpoints:

- `POST /post-photos`
- `GET /post-photos/{id}`
- `GET /post-photos`
- `PUT /post-photos/{id}`
- `DELETE /post-photos/{id}` (`204`)

### 11.3 POI photos

Base path: `/poi-photos`

Intended contract:

```json
{
  "url": "https://cdn.example.com/poi.jpg",
  "poi": { "id": "uuid" }
}
```

Note:

- `PoiPhotoController` is currently wired with `UserPhoto` DTO/service types, while dedicated `PoiPhoto*` DTOs and service also exist in codebase.
- For mobile client integration, treat this endpoint as unstable until this mismatch is normalized.

## 12. Test Endpoint

### GET `/test`

Response `200`:

- body: `Hello, World`
- content-type: plain text

## 13. Kotlin Compose Multiplatform Integration Notes

For shared `commonMain` API client models:

- Use string type for UUID fields and parse to platform UUID wrappers in domain layer if needed.
- Store `accessToken` + `refreshToken` securely:
  - Android: encrypted storage
  - iOS: Keychain
- Add an auth interceptor:
  - attach bearer token on every protected request
  - on 401/403 (or explicit token-expired handling), call `/auth/refresh` once and retry original request
  - if refresh fails, clear session and route user to login
- Keep defensive JSON parsing for error payloads because error shape is not fully unified yet.

## 14. Practical Gaps to Keep in Mind

Current API is functional but not yet ideal as a strict public contract:

- Several request/response DTOs include nested entities instead of pure ID-based references.
- Some not-found/business errors are thrown as generic runtime exceptions and may become `500`.
- `/poi-photos` currently has DTO/service mismatch.

Recommended next backend iteration before public mobile rollout:

- Introduce explicit stable API contracts (`*Request`, `*Response`) with IDs only for relations.
- Normalize error contract for all 4xx/5xx responses.
- Stabilize and test photo endpoints, especially `/poi-photos`.

## 15. Gear Inventory API

Base path: `/gear`

`GearStatus` enum:

- `NEW`
- `GOOD`
- `WORN`
- `BROKEN`

`GearRequestDto`:

```json
{
  "name": "Trekking Poles",
  "category": "Trekking",
  "weightKg": 0.62,
  "status": "GOOD"
}
```

`GearResponseDto`:

```json
{
  "id": "uuid",
  "name": "Trekking Poles",
  "category": "Trekking",
  "weightKg": 0.62,
  "status": "GOOD",
  "userId": "uuid",
  "createdAt": "2026-05-18T18:10:00",
  "modifiedAt": "2026-05-18T18:10:00"
}
```

Endpoints:

- `GET /gear` -> `GearResponseDto[]` for current user
- `GET /gear/{id}` -> `GearResponseDto` (owner only)
- `POST /gear` -> create and return `GearResponseDto`
- `PUT /gear/{id}` -> update and return `GearResponseDto` (owner only)
- `DELETE /gear/{id}` -> `204` (owner only)
