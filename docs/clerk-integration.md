# Clerk Integration

## Overview

The backend supports two authentication modes that coexist via a pluggable resolver pipeline:

1. **JWT mode** (production): Validates Clerk-issued JWTs from the `Authorization: Bearer <token>` header against the Clerk JWKS endpoint.
2. **Header mode** (dev/Docker): Trusts raw `X-Clerk-User-Id` / `X-Clerk-Email` headers. Guarded by `app.auth.clerk.trust-headers`.
3. **Mock mode** (local dev): Uses a fixed identity when `app.auth.mock-enabled=true` and no other resolver matches.

## Resolver Pipeline

Resolvers run in order; the first one that returns a principal wins:

| Order | Resolver | Condition |
|-------|----------|-----------|
| 5 | `ClerkJwtPrincipalResolver` | Active when `app.auth.clerk.jwks-uri` is configured |
| 10 | `ClerkHeaderPrincipalResolver` | Active when `app.auth.clerk.trust-headers=true` |
| 1000 | `MockAuthenticatedPrincipalResolver` | Active when `app.auth.mock-enabled=true` |

## Setup for Production (JWT Validation)

### 1. Clerk Dashboard

1. Go to [clerk.com](https://clerk.com) > your application > **Sessions > Customize session token**.
2. Add a custom claim to include the user's email in the JWT:
   ```json
   {
     "email": "{{user.primary_email_address}}"
   }
   ```
3. Note your **JWKS endpoint** and **Issuer URL** from **API Keys**:
   - JWKS: `https://<your-frontend-api>.clerk.accounts.dev/.well-known/jwks.json`
   - Issuer: `https://<your-frontend-api>.clerk.accounts.dev`
4. Note your **Publishable key** (`pk_test_...` or `pk_live_...`).

### 2. Backend Configuration

Set these environment variables (or in `application.properties`):

```properties
CLERK_JWKS_URI=https://<your-frontend-api>.clerk.accounts.dev/.well-known/jwks.json
CLERK_ISSUER=https://<your-frontend-api>.clerk.accounts.dev
CLERK_TRUST_HEADERS=false
APP_AUTH_MOCK_ENABLED=false
```

### 3. Frontend Configuration

```
EXPO_PUBLIC_CLERK_PUBLISHABLE_KEY=pk_test_xxxxx
```

### 4. User Provisioning

The seed data creates users with `@vacaciones.local` emails. When a Clerk user logs in:

- `CurrentUserServiceImpl` looks up the internal `User` by `clerkUserId` first, then by `email`.
- If matched by email and the internal `clerkUserId` is empty, it auto-links the Clerk ID.
- This means you can create Clerk users with matching emails and they'll be linked on first login.

## Configuration Reference

| Property | Env Var | Default | Description |
|----------|---------|---------|-------------|
| `app.auth.clerk.jwks-uri` | `CLERK_JWKS_URI` | _(empty)_ | JWKS endpoint URL. Empty = JWT validation disabled |
| `app.auth.clerk.issuer` | `CLERK_ISSUER` | _(empty)_ | Expected JWT issuer. Empty = issuer not validated |
| `app.auth.clerk.trust-headers` | `CLERK_TRUST_HEADERS` | `true` | Accept raw `X-Clerk-*` headers without JWT |
| `app.auth.clerk.user-id-header` | — | `X-Clerk-User-Id` | Header name for Clerk user ID |
| `app.auth.clerk.email-header` | — | `X-Clerk-Email` | Header name for user email |
| `app.auth.mock-enabled` | `APP_AUTH_MOCK_ENABLED` | `false` | Enable mock auth fallback |

## Docker Compose

For local development with mock auth (default):

```bash
docker compose up --build
```

For Clerk SSO via Docker:

```bash
EXPO_PUBLIC_CLERK_PUBLISHABLE_KEY=pk_test_xxx \
CLERK_JWKS_URI=https://your-api.clerk.accounts.dev/.well-known/jwks.json \
CLERK_ISSUER=https://your-api.clerk.accounts.dev \
CLERK_TRUST_HEADERS=false \
APP_AUTH_MOCK_ENABLED=false \
docker compose up --build
```
