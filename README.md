# ERPX

Role-based Academic ERP: class panel tagging, quiz-driven automated attendance,
bulk group email, and scheduled video classes. Spring Boot 4 backend + Jetpack
Compose Android client.

Designed to run on a local network: the backend runs on one machine and phones
on the same Wi-Fi connect to it directly. No hosting or database setup required.

## Repo layout

```
src/, pom.xml       Spring Boot backend (the repo root is the backend project)
android-client/     Android app (Kotlin, Jetpack Compose, MVVM)
data/               Local H2 database files (gitignored, created on first run)
```

## Backend — quick start

Requirements: JDK 21+.

No database install is required for local development — the backend runs
against an embedded, file-backed H2 database by default.

```bash
./mvnw spring-boot:run      # Linux/macOS
mvnw.cmd spring-boot:run    # Windows
```

The server binds `0.0.0.0:8080`, so it is reachable both at
`http://localhost:8080` on the host and at `http://<your-LAN-IP>:8080` from
other devices on the same network. On first boot it creates
`data/erp_db.mv.db` (gitignored) and all tables automatically.

That's it — register a user via `POST /api/auth/register` and you're up.

The H2 database file persists across restarts. If the backend is killed
ungracefully it can leave a stale `data/erp_db.lock.db` behind, which blocks
the next startup; delete that file and start again. Deleting
`data/erp_db.mv.db` wipes all data and recreates an empty schema.

### Environment variables (all optional for local dev)

Copy `.env.example` to `.env` (or export the vars yourself) if you need
real email delivery or a non-default JWT secret. Every variable has a
dev-safe fallback baked into `application.properties`, so the backend runs
fine with none of them set. `.env` is gitignored — never commit it.

> **Keep `JWT_SECRET` stable once people are signed in.** Tokens are signed
> with it, so changing it invalidates every existing session and logs all
> devices out. Set it once and leave it alone. Generate one with
> `openssl rand -base64 32`.

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

**The server address is set inside the app at runtime — it is not baked into
the APK.** On first launch the app asks for it, and you can change it any time
from the "Can't connect? Change server address" link on the login screen. A
new address takes effect on the very next request; no rebuild, no reinstall.

Enter the address of the machine running the backend, e.g. `192.168.1.50:8080`.
The scheme and port are optional — `192.168.1.50` is expanded to
`http://192.168.1.50:8080/`. **Test and save** checks the server is actually
reachable before storing it.

| Where the app runs | Address to enter |
|---|---|
| Android emulator | `10.0.2.2:8080` (the emulator's alias for your host's `localhost`) |
| Physical device | Your PC's LAN IP, e.g. `192.168.1.50:8080` |

To find your PC's LAN IP: `ipconfig` on Windows (look for the Wi-Fi adapter's
IPv4 address), or `ifconfig` / `ip addr` on macOS/Linux.

`-PapiBaseUrl` still works and sets the *first-run default* only. Once a user
saves an address on the device, that value wins.

> **Your LAN IP will change.** Routers hand out addresses via DHCP, so the
> PC's IP can change after a reboot or reconnect — this is the single most
> common cause of "it worked yesterday". Just re-enter the new address in the
> app. To stop it happening, reserve a static IP for the PC in your router's
> DHCP settings.

### Sharing the app with someone else on your network

1. Start the backend on your PC (see above). Leave it running.
2. Find your PC's LAN IP (`ipconfig`).
3. Allow inbound port 8080 through the firewall — see below.
4. Send them `app/build/outputs/apk/debug/app-debug.apk` and have them install it.
5. On first launch they enter your PC's IP and tap **Test and save**.

Both devices must be on the same Wi-Fi network.

#### Firewall (Windows)

Inbound TCP 8080 must be allowed **for the profile your Wi-Fi is currently
using**. A rule scoped to `Private` does nothing on a network Windows has
classified as `Public`, which is a common and easily-missed failure.

Check the classification and the rule:

```powershell
Get-NetConnectionProfile | Select-Object InterfaceAlias, NetworkCategory
Get-NetFirewallRule -DisplayName "ERPX Backend (8080)" | Select-Object Profile, Enabled
```

In an **elevated** PowerShell, create the rule (first time) or widen an
existing one to cover every profile:

```powershell
New-NetFirewallRule -DisplayName "ERPX Backend (8080)" -Direction Inbound `
  -Protocol TCP -LocalPort 8080 -Action Allow -Profile Any

# ...or, if the rule already exists but is scoped too narrowly:
Set-NetFirewallRule -DisplayName "ERPX Backend (8080)" -Profile Any
```

#### If it still won't connect

- **VPN on the PC** — an active VPN commonly blocks LAN reachability from the
  phone. Disconnect it and retry.
- **Verify from the phone's browser**: open `http://<PC-IP>:8080/api/auth/login`.
  A `405 Method Not Allowed` page is the expected result and is *good news* —
  it proves the phone reached the server, so the problem is in the app, not the
  network. A timeout or "can't connect" means it's a network or firewall issue.
- **Confirm the backend is listening on all interfaces**, not just loopback:
  ```powershell
  Get-NetTCPConnection -LocalPort 8080 -State Listen
  ```
- **Client isolation / guest Wi-Fi** — some routers stop devices talking to
  each other. Try a phone hotspot with both devices joined to it.

### Reliability behaviour

Worth knowing when diagnosing a report of "the app is broken":

- Requests **retry 3× with backoff** on connection failure and on 5xx, so a
  brief Wi-Fi drop, a device waking from sleep, or a backend restart recovers
  on its own.
- The connect timeout is **5 seconds** — a wrong address fails fast rather
  than freezing the screen.
- A **401 clears the saved session** and returns to the login screen. Without
  this, a device holding a token the server no longer accepts would sit on a
  dashboard where every screen silently failed while still appearing signed in.

## Troubleshooting

**"It worked once, then no new users appeared in the database."**
Almost always the phone pointing at a stale IP rather than a database fault.
The backend only writes when requests reach it, so check the backend console:
if there are no `Hibernate: insert ...` lines, nothing arrived. Re-enter the
current LAN IP in the app (see *Pointing the app at your backend*).

**The app looks signed in but every screen is empty or slow.**
The session is stored on the device and survives restarts, so the app can look
logged in while no request succeeds. Open the login screen's *Change server
address* link and use **Test and save** to confirm the backend is reachable.

**Everyone got logged out after a backend restart.**
`JWT_SECRET` changed (commonly: it was unset, so the fallback was used, and
then a real one was added). Set it once in `.env` and keep it stable.

**Diagnosing a 4xx/5xx.**
The backend logs a warning for handled request failures and a full stack trace
for unhandled ones, so the backend console is the place to look — the client
only ever shows a generic message. Reading the status:

| Status | Meaning |
|---|---|
| `401` | Missing/invalid/expired token. Also what an unknown path returns when unauthenticated, since security runs before routing. |
| `403` | Authenticated, but the wrong role for that endpoint (e.g. a student calling a teacher-only route). |
| `404` | Authenticated, but the URL isn't mapped. |
| `405` | Right URL, wrong HTTP verb. |
| `400` | Request rejected by validation or business rules — the log line gives the reason. |
| `500` | A genuine server-side fault, with a full stack trace in the log. |

## Known backend gap

There's currently no "list quizzes by class tag" endpoint — only
`/api/quiz/create` (teacher) and `/api/quiz/submit` (student) exist. The
Android Quiz Taking screen works around this by having students enter the
quiz ID and question count shared by their teacher rather than fetching an
assigned-quiz list.
