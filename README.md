# shop-platform

Spring Boot **4.0.9** · Spring Cloud **2025.1.4** · Java **25** · Maven · PostgreSQL · Kafka · Redis · Docker · Kubernetes (GKE)

```
                      ┌──────────────┐
   client ──────────► │ api-gateway  │  Spring Cloud Gateway + circuit breaker + 503 fallback
                      └──┬──┬──┬──┬──┘
        ┌────────────────┘  │  │  └───────────────┐
        ▼                   ▼  ▼                  ▼
 product-service   article-service   inventory-service   order-service
   (productdb)       (articledb)        (inventorydb)       (orderdb)
   Redis cache       Redis cache             ▲                │  ▲
                          ▲                  │   Kafka        │  │
                          └── REST + CB ─────┼────────────────┘  │
                       order → article       │  order-created    │
                                             └──► inventory-reserved / inventory-rejected
```

| Service | Port | DB | Notes |
|---|---|---|---|
| api-gateway | 8080 | – | routes `/api/**`, circuit breaker per route, fallback → 503 ProblemDetail |
| product-service | 8081 | productdb | CRUD, Redis cache |
| article-service | 8082 | articledb | CRUD (SKU, price per product), Redis cache |
| inventory-service | 8083 | inventorydb | stock + saga participant (reserve / reject), idempotent |
| order-service | 8084 | orderdb | CQRS command + query side, saga, Resilience4j client to article-service |

## Patterns
* **Saga (choreography)** – `POST /api/orders` → order `PENDING` + `order-created` → inventory reserves → `inventory-reserved` (order `CONFIRMED`) or `inventory-rejected` (compensation: order `CANCELLED`).
* **CQRS** – order-service writes to `orders`; every state change goes to Kafka `order-events`; a projection builds `order_view`; queries read only `order_view` (+ Redis). Reads are eventually consistent, so POST returns `202 Accepted`.
* **Global exception handling** – `common/GlobalExceptionHandler` → RFC 9457 `ProblemDetail` for 400/404/409/503/500 and all standard MVC errors.
* **Circuit breaker** – gateway (per route) and order-service → article-service. If a service is down: fast `503` JSON instead of hanging. Redis down: `ResilientCacheConfig` falls back to the DB.
* **Autoscaling** – HPA: api-gateway, article-service · VPA: product-service (Auto), order/inventory (recommend only) · KEDA: order-service and inventory-service on Kafka lag.

## Run locally
```bash
docker compose up --build          # gateway on http://localhost:8080
bash scripts/test-flow.sh          # product → article → stock → order (confirmed) → order (cancelled)
```
Without Docker for the apps: `docker compose up postgres redis kafka`, then `mvn -pl order-service spring-boot:run` etc.

## Push to GitHub
```bash
git init && git add . && git commit -m "Initial commit: shop-platform"
gh repo create shop-platform --private --source=. --push      # or: git remote add origin ... && git push -u origin main
```

## Deploy to GCP (GKE)
1. Edit and run `scripts/gcp-setup.sh` (APIs, Artifact Registry, GKE with VPA, KEDA, Workload Identity Federation).
2. Add the printed GitHub variables/secrets. Push to `main` → `.github/workflows/cd.yml` tests, builds 5 images, pushes to Artifact Registry, applies `k8s/`.
3. `kubectl -n shop get svc api-gateway` for the external IP, then `bash scripts/test-flow.sh http://<IP>`.

## Before production
Postgres/Kafka/Redis in `k8s/` are single-node demo setups → use Cloud SQL, Managed Kafka, Memorystore. Replace `ddl-auto: update` with Flyway, secrets with Secret Manager, add an outbox for Kafka publishing and dead-letter topics, add auth at the gateway, add tests.
