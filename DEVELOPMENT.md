# Local Development

## Configuration

Copy `.env.example` to `.env` and replace the placeholder values with your local
test credentials. `JWT_SECRET` must be at least 32 bytes. The backend requires
this value and does not use a shared default secret. Never commit `.env`.

Run `docker compose up --build` from the repository root. PostgreSQL must pass
its health check before the backend starts. The frontend is available at
http://localhost:5173 and the backend at http://localhost:8080.

The frontend container runs the Vite development server. It is intended for
local development, not public production hosting.

## Run Without Docker

Use Java 21, Maven, Node.js 22, and a PostgreSQL database. Supply `JWT_SECRET`,
`STRIPE_SECRET_KEY`, and `STRIPE_WEBHOOK_SECRET` as environment variables for
the backend. Use `DB_URL`, `DB_USER`, and `DB_PASS` for a non-default database.
Use `APP_URL` if the frontend origin differs from http://localhost:5173; this
also controls the allowed browser origin and payment redirect URLs.

In `backend`, run:

```sh
mvn spring-boot:run
```

In `frontend`, run:

```sh
npm ci
npm run dev
```

The frontend uses http://localhost:8080 by default. Set `VITE_API_BASE_URL` in
`frontend/.env.local` to use another backend.

## Verification

```sh
cd backend
mvn verify
```

```sh
cd frontend
npm ci
npm run build
```

The backend tests start Spring Security and the API against an in-memory H2
database. They check browser preflight requests, nested cart validation, and
inventory rollback when Stripe session creation fails. Stripe is mocked, so
these tests do not make payments or require Stripe credentials.

GitHub Actions runs both checks on pushes and pull requests. PostgreSQL
migrations and live Stripe webhooks still require separate integration checks.

## Remaining Payment Work

The payment implementation is still an MVP. Reservations must be associated
with a specific order before the webhook can safely consume only that order's
inventory. Reservation expiry also needs coordinated locking and alignment
with Stripe session expiry. Webhook replay handling, asynchronous payment
events, and retry-safe session creation need dedicated coverage before a
production deployment.
