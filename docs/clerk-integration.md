# Clerk Integration (Incremental Backend Prep)

## What Is Implemented

- Request authentication context pipeline:
  - `AuthenticationContextFilter` runs once per request.
  - It resolves identity through pluggable `AuthenticatedPrincipalResolver` strategies.
  - Current implementations:
    - `ClerkHeaderPrincipalResolver` (reads Clerk identity from headers).
    - `MockAuthenticatedPrincipalResolver` (local fallback, configurable).
- Internal user resolution:
  - `CurrentUserServiceImpl` resolves `User` by:
    - `clerkUserId` first
    - `email` fallback
  - Validates that the internal user exists and is active.
  - If matched by email and internal `clerkUserId` is empty, it links/stores the incoming `clerkUserId`.
- Business services remain unchanged and still consume only `CurrentUserService`.

## Current Header-Based Contract (Temporary)

- `X-Clerk-User-Id`
- `X-Clerk-Email`

Header names are configurable via `application.properties`:

- `app.auth.clerk.user-id-header`
- `app.auth.clerk.email-header`

## Mock Mode (Local Development)

- `app.auth.mock-enabled=true|false`
- `app.auth.mock-current-user-email`
- `app.auth.mock-current-user-clerk-id`

When enabled and Clerk headers are absent, mock identity is used.

## Pending for Full Production Clerk Validation

- TODO: Validate Clerk JWT signature/claims on backend (or via trusted gateway/security layer).
- TODO: Replace header-based extraction with verified token claim extraction once security config is enabled.

If Spring Security is added/enabled later, keep business services unchanged and move identity extraction to a security-authenticated principal adapter.
