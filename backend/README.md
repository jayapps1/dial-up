# Ghana Emergency Connect — Spring Boot Backend

A production-oriented Spring Boot 3.3 REST API backend service for **Ghana Emergency Connect**, built with Java 21, Maven, Spring Data JPA, Spring Security, and Flyway.

---

## Technical Stack & Architecture

- **Language:** Java 21
- **Framework:** Spring Boot 3.3.5
- **Build System:** Apache Maven (`pom.xml` & Maven Wrapper)
- **Persistence:** Spring Data JPA / Hibernate
- **Database Engine:** PostgreSQL with PostGIS extension readiness
- **Database Migrations:** Flyway (`classpath:db/migration`)
- **Package Architecture:** Feature-oriented (`agency`, `station`, `report`, `admin`, `common`)
- **Security:** Spring Security, JJWT, HttpOnly Refresh Token Cookies, BCrypt (strength 12)

---

## Required Software

- Java 21 JDK
- PostgreSQL 15+ (with PostGIS extension enabled)
- Apache Maven 3.9+ (managed via local Maven Wrapper `mvnw` / `mvnw.cmd`)

---

## Environment Variables & Configuration

The application reads server and datasource connection settings from environment variables:

| Environment Variable | Description | Development Default |
| :--- | :--- | :--- |
| `GEC_SERVER_PORT` | Server HTTP Port | `8099` |
| `GEC_DB_URL` | PostgreSQL JDBC Connection URL | `jdbc:postgresql://localhost:5432/gec_db` |
| `GEC_DB_USERNAME` | Database Username | `gec_user` |
| `GEC_DB_PASSWORD` | Database Password | `gec_password` |
| `GEC_BOOTSTRAP_ADMIN_EMAIL` | Bootstrap SYSTEM_ADMIN Email | `nanagyachie@gmail.com` |
| `GEC_BOOTSTRAP_ADMIN_PASSWORD` | Bootstrap SYSTEM_ADMIN Password | *(Provided at startup)* |
| `GEC_ADMIN_ALLOWED_ORIGINS` | Web Admin Portal CORS Allowed Origins | `http://localhost:5173,http://localhost:3000` |
| `SPRING_PROFILES_ACTIVE` | Active Spring Profile | `dev` |

### Setting Environment Variables in PowerShell (Local Development)
```powershell
$env:GEC_SERVER_PORT="8099"
$env:GEC_BOOTSTRAP_ADMIN_EMAIL="nanagyachie@gmail.com"
$env:GEC_BOOTSTRAP_ADMIN_PASSWORD="<YOUR_LOCAL_SECRET>"
```

---

## Flyway Database Schema Evolution

Flyway manages database schema evolution (`spring.flyway.enabled=true`).
`spring.jpa.hibernate.ddl-auto` is strictly set to `validate`. Schema modifications must be performed via new Flyway migration files:

- `V1__initial_schema.sql` — Base relational schema with UUID primary keys.
- `V2__seed_dev_data.sql` — Development agency and station seed data.
- `V3__incident_reporting.sql` — Incident reporting, category routing, assignment, and status history schema.
- `V4__seed_incident_categories.sql` — Development incident categories and multi-agency routing data.
- `V5__admin_security_and_rbac.sql` — Admin users, roles, permissions, refresh tokens, audit logs, and seed permissions.

---

## Build & Test Instructions (Maven)

Run unit & integration tests:
```powershell
.\mvnw.cmd clean test
```

Build executable Spring Boot JAR:
```powershell
.\mvnw.cmd clean package
```

Run Spring Boot application locally:
```powershell
.\mvnw.cmd spring-boot:run
```
