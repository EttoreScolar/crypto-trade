# Crypto Trader (Agentic) - Spring WebFlux + Redis + Spring AI + Postgres (JPA) + Flyway + React

This repo scaffolds a **multi-agent trading architecture**:

- **MarketDataPublisher** publishes ticks to Redis (`market:ticks`)
- **AnalystAgent** consumes ticks and uses **Spring AI ChatClient** to output a **TradeIntent**
- **RiskAgent** deterministically validates intents (kill switch, confidence threshold, amount caps) and publishes `trade:approved` / `trade:rejected`
- **ExecutorAgent** demonstrates **Spring AI tool calling** by forcing a tool call to `placeOrder(...)` using the approved intent
- **TradeEvent** rows are persisted in Postgres using **JPA** (off the hot path)

> By default, `placeOrder(...)` is a **NOOP**. Implement signed Binance order placement only after adding key management and more risk controls.

## Run
```bash
cp .env.example .env
docker compose --env-file .env up --build
```

Frontend: http://localhost:3000  
Backend: http://localhost:8080

## API
- GET `/api/events/latest` -> latest events from Postgres
- GET `/api/stream/events` -> SSE of executed trades
- GET `/api/killswitch` -> true/false
- POST `/api/killswitch/enable` / `/disable`

## Notes
- Spring AI requires `OPENAI_API_KEY`. Without it, intents will default to HOLD and no trades will be executed.
- WebFlux + JPA is used intentionally here: DB writes run on boundedElastic to avoid blocking reactive threads.
