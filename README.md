# Crypto Trader (Starter) - Java + Spring Boot + Postgres + Flyway + Docker + React

This is a **starter scaffold** for a Binance trading web app.

## What's included
- **Backend**: Java 21 + Spring Boot + JPA/Hibernate + Flyway + Postgres
- **Agent loop**: scheduled tick (1s) + persisted TradeEvent
- **Environments**:
  - `APP_ENV=testnet` -> uses Binance testnet base URL
  - `APP_ENV=prod` -> uses real Binance base URL
- **Frontend**: React (Vite) served by Nginx in Docker, proxies `/api/*` to backend

> Safety: Real order placement is intentionally **disabled** in this starter (strategy only emits HOLD).

## Run with Docker
1) Copy env:
```bash
cp .env.example .env
```

2) Start:
```bash
docker compose --env-file .env up --build
```

- Frontend: http://localhost:3000
- Backend: http://localhost:8080

## Next steps
- Implement proper auth (JWT) and multi-user model
- Encrypt API keys at rest
- Add risk limits (max order size, max daily loss, cooldowns)
- Implement real order placement in `ExecutionService` using Binance connector
