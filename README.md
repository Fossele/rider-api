# Rider API

Rider is a Spring Boot service for matching ride requests with nearby drivers. Driver locations are indexed in Redis using geospatial operations, while ride and location records are persisted in PostgreSQL/PostGIS.

## Features

- Find and assign the nearest available driver within 5 km.
- Track driver locations over STOMP/WebSocket.
- Persist rides and driver locations with Spring Data JPA.
- Complete or cancel rides and restore driver availability.
- Retrieve ride status and distance metrics.

## Technology

- Java 21
- Spring Boot 4
- PostgreSQL/PostGIS
- Redis 7
- Maven

## Running with Docker Compose

Docker and Docker Compose are required.

```bash
docker compose up --build
```

The API is available at `http://localhost:8080`. PostgreSQL and Redis are started automatically with the application.

To stop the services:

```bash
docker compose down
```

## Running locally

Start PostgreSQL and Redis first, then configure the connection properties through environment variables if needed:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/geodb
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password
export SPRING_REDIS_HOST=localhost
export SPRING_REDIS_PORT=6379
./mvnw spring-boot:run
```

Run the test suite with:

```bash
./mvnw test
```

## REST API

### Request a ride

The service assigns the closest available driver within 5 km.

```bash
curl -X POST \
  "http://localhost:8080/api/rides/request?riderId=rider-123&longitude=5.3344&latitude=3.4322"
```

### Get ride metrics

```bash
curl "http://localhost:8080/api/rides/{rideId}/metrics"
```

### Complete or cancel a ride

```bash
curl -X POST "http://localhost:8080/api/rides/{rideId}/complete"
curl -X POST "http://localhost:8080/api/rides/{rideId}/cancel"
```

## Driver location updates

Connect a STOMP client to the SockJS endpoint:

```text
http://localhost:8080/ws-ride
```

Publish driver location updates to `/app/driver.ping` with a payload such as:

```json
{
  "vehicleName": "Taxi west",
  "longitude": 5.3344,
  "latitude": 3.4322
}
```

## Configuration

Application defaults are defined in `src/main/resources/application.yaml`. The Docker Compose setup uses:

- PostgreSQL: `localhost:5432`, database `geodb`
- Redis: `localhost:6379`
- API: `localhost:8080`

