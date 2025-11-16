# Gold Broker Spring Boot

Java Spring Boot application (Maven) to store and process precious metals data and execute buy/sell transactions using prices from a (mocked) Gold Broker API.

## Tech stack

- Java 17
- Spring Boot 3 (Web, Data JPA, Validation)
- H2 in-memory DB (dev)
- Maven

## How to run

```bash
mvn spring-boot:run
```

The app starts on http://localhost:8080.

### Example requests

- Get mock latest price:

```bash
curl "http://localhost:8080/api/prices/latest?metal=GOLD"
```

- Buy 10 grams of GOLD:

```bash
curl -X POST "http://localhost:8080/api/portfolio/buy?metal=GOLD&quantity=10"
```

- Sell 5 grams of GOLD:

```bash
curl -X POST "http://localhost:8080/api/portfolio/sell?metal=GOLD&quantity=5"
```

- List holdings:

```bash
curl "http://localhost:8080/api/portfolio/holdings"
```

## Next steps

- Replace `PriceService.fetchLatestPrice` with a real HTTP call to Gold Broker API.
- Add authentication (JWT) and user-specific portfolios.
- Add audit logs and scheduled price snapshots.
