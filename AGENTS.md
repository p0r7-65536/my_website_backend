# AGENTS.md

## Cursor Cloud specific instructions

This repo is a single Spring Boot 3.2 (Java 17 target, runs on the VM's Java 21) REST API
for a forum/blog — `com.example.blogdemo`. It is a headless JSON API (no frontend); test it
with `curl`/HTTP. Standard commands live in `pom.xml` and the `mvnw` wrapper.

### Services

- **Forum API** (Spring Boot) — port `8080`. Health: `GET /api/health`.
- **MySQL 8** — required at startup; the app opens the datasource and seeds data on boot.

### MySQL (must be started manually each boot)

MySQL is installed in the VM image but is NOT auto-started, and is NOT started by the update
script (service startup is intentionally excluded). Start it before running the app:

```bash
sudo service mysql start   # verify: sudo mysqladmin ping
```

The database and app user are already provisioned in the VM image and persist in the snapshot:

- database `blog`, user `blog_app`, password `blog_app_pw` (reachable on `127.0.0.1:3306`).

### Running the API in dev mode

`application.yml` hardcodes the datasource host to `mysql` and requires two env vars with no
defaults (`MYSQL_PASSWORD`, `JWT_SECRET`). For local dev, override the host to `127.0.0.1`
via `SPRING_DATASOURCE_URL` (do NOT edit `application.yml`). `JWT_SECRET` MUST be >= 32 chars
(HS256 key requirement in `JwtService`), or startup/login fails.

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://127.0.0.1:3306/blog"
export MYSQL_PASSWORD="blog_app_pw"
export JWT_SECRET="dev-jwt-secret-please-change-0123456789abcdef"
sh ./mvnw spring-boot:run      # dev mode; use `sh ./mvnw` — the committed mvnw lacks +x
```

On boot, `DataInitializer` seeds a `SUPER_ADMIN` account `admin` / `admin123` and 3 boards.
JPA `ddl-auto: update` auto-creates tables.

### Build / test / lint

- Build: `sh ./mvnw -B -DskipTests package` (produces `target/blog-demo-0.0.1-SNAPSHOT.jar`).
- Tests: none exist (`src/test` is empty).
- Lint: no linter/formatter is configured; compilation via the build is the effective check.
- Note: `target/` is committed to git in this repo — avoid committing rebuilt artifacts.
