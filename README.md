# Property Rental Management Platform - Backend

Spring Boot REST API for a platform that connects property owners and tenants.

## Tech stack
- Java 17, Spring Boot 3
- Spring Data JPA / Hibernate
- MySQL
- REST API with JSON, signed login tokens, BCrypt password hashing

## Features
- Three roles: Property Owner, Tenant, Administrator
- Owners: add, edit and delete properties, mark them rented or available, approve or reject rental requests, see payments received
- Tenants: search by location, budget and type, send rental requests, track status and history, pay rent online (simulated) after approval
- Admin: verify owners and property listings, monitor transactions, resolve complaints, view reports
- Owners and tenants can raise complaints and read the admin's response

## Business rules
- New owners must be verified by the admin before they can list properties
- New listings stay hidden until the admin verifies them
- Approving a request marks the property as rented and rejects other waiting requests
- Only approved requests can be paid

## Run locally
1. Install JDK 17, Maven and MySQL 8
2. The database `rental_db` and all tables are created automatically
3. Set DB_USER and DB_PASSWORD if yours are not root / root
4. `mvn spring-boot:run`
5. The API runs at http://localhost:8080/api

## Environment variables
| Name | Purpose | Default |
|---|---|---|
| DB_URL | JDBC url, e.g. jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED&serverTimezone=UTC | local MySQL |
| DB_USER | database user | root |
| DB_PASSWORD | database password | root |
| CORS_ORIGIN | frontend address, no trailing slash | http://localhost:5173 |
| JWT_SECRET | secret used to sign login tokens | change it when deploying |
| PORT | server port | 8080 |

## Deploy (Render, Docker)
The included Dockerfile builds and runs the app. Create a Render web service from this repo and set the variables above.

## Demo logins (created on first start)
| Role | Email | Password |
|---|---|---|
| Admin | admin@rental.com | admin123 |
| Owner | owner@rental.com | owner123 |
| Tenant | tenant@rental.com | tenant123 |

## API overview
- `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/account/me`
- `GET /api/public/properties`, `GET /api/public/properties/{id}` (no login)
- `/api/owner/**` properties, requests, payments
- `/api/tenant/**` requests and payments
- `/api/complaints` raise and list own complaints
- `/api/admin/**` report, users, listings, payments, complaints
