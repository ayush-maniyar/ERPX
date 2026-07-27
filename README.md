# ERPX

Role-based Academic ERP: class panel tagging, quiz-driven automated attendance,
bulk group email, and scheduled video classes. Spring Boot backend + Jetpack
Compose Android client.

## Repo layout

```
erp-backend/        Spring Boot 3.x backend (this is the repo root)
android-client/      Android app (Kotlin, Jetpack Compose, MVVM)
```

## Backend — quick start

Requirements: JDK 21+.

No database install is required for local development — the backend runs
against an embedded, file-backed H2 database by default.

```bash
./mvnw spring-boot:run      # Linux/macOS
mvnw.cmd spring-boot:run    # Windows
```

The server starts on `http://localhost:8080`. On first boot it creates
`data/erp_db.mv.db` (gitignored) and all tables automatically.

That's it — register a user via `POST /api/auth/register` and you're up.

### Environment variables (all optional for local dev)

Copy `.env.example` to `.env` (or export the vars yourself) if you need
real email delivery or a non-default JWT secret. Every variable has a
dev-safe fallback baked into `application.properties`, so the backend runs
fine with none of them set.

| Variable | Purpose | Required for |
|---|---|---|
| `JWT_SECRET` | Base64 HMAC signing key for JWTs (`openssl rand -base64 32`) | Anything beyond throwaway local testing |
| `JWT_EXPIRATION_MS` | Token lifetime in ms (default 24h) | — |
| `GMAIL_USERNAME` / `GMAIL_APP_PASSWORD` | Gmail SMTP sender (use an [App Password](https://myaccount.google.com/apppasswords), not your real password) | Bulk email feature |
| `SPRING_PROFILES_ACTIVE` | `local` (default, H2) or `prod` (PostgreSQL) | Switching to Postgres |
| `POSTGRES_PASSWORD` | Postgres password | `prod` profile only |

### Switching to PostgreSQL

Set `SPRING_PROFILES_ACTIVE=prod`, have a Postgres instance running with an
`erp_db` database, and set `POSTGRES_PASSWORD`. See
`application-prod.properties` for the connection details.

### H2 console (local dev only)

With the `local` profile active, `http://localhost:8080/h2-console` is open
(JDBC URL `jdbc:h2:file:./data/erp_db`, user `sa`, blank password) for
poking at local data directly.

## Android client — quick start

Requirements: Android Studio (or the SDK + JDK 17 command-line tools),
an Android SDK with platform 35 installed.

1. `cd android-client`
2. Copy `local.properties.example` to `local.properties` and point `sdk.dir`
   at your Android SDK install (Android Studio does this for you
   automatically if you open the project there instead).
3. Build:
   ```bash
   ./gradlew assembleDebug
   ```
   The debug APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

### Pointing the app at your backend

The app's API base URL is baked in at build time via `apiBaseUrl`:

```bash
./gradlew assembleDebug -PapiBaseUrl="http://10.0.2.2:8080/"
```

- **Android emulator**: use the default, `http://10.0.2.2:8080/` — this is
  the emulator's alias for your host machine's `localhost`. No flag needed.
- **Physical device**: use your PC's LAN IP instead, e.g.
  `-PapiBaseUrl="http://192.168.1.50:8080/"`. The phone and PC must be on
  the same network, and:
  - Windows Firewall must allow inbound TCP on port 8080 for your network's
    Private profile (Public-profile networks block this by default —
    check via `Get-NetConnectionProfile` and switch with
    `Set-NetConnectionProfile -InterfaceAlias "Wi-Fi" -NetworkCategory Private`
    in an elevated PowerShell, then add a rule with `New-NetFirewallRule`).
  - If the base URL host isn't `10.0.2.2`/`localhost`, add it to
    `app/src/main/res/xml/network_security_config.xml` (cleartext HTTP is
    only permitted for hosts explicitly listed there).
  - A VPN active on the PC can also block LAN reachability from the phone —
    disconnect it if connections still fail after the firewall/network
    fixes above.

## Known backend gap

There's currently no "list quizzes by class tag" endpoint — only
`/api/quiz/create` (teacher) and `/api/quiz/submit` (student) exist. The
Android Quiz Taking screen works around this by having students enter the
quiz ID and question count shared by their teacher rather than fetching an
assigned-quiz list.
