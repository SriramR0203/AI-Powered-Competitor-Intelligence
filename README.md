# AI-Powered Competitor Intelligence Platform

> Enterprise-grade platform that automatically collects competitor intelligence from websites and news sources, enriches it with AI, and presents it through interactive dashboards, real-time alerts, and exportable reports.

---

## Screenshots

> _Run the application locally to see the full UI._

| Dashboard | Competitors | Intelligence Events |
|:---------:|:-----------:|:-------------------:|
| ![Dashboard](docs/screenshots/dashboard.png) | ![Competitors](docs/screenshots/competitors.png) | ![Events](docs/screenshots/events.png) |

| Alerts | Reports | Login |
|:------:|:-------:|:-----:|
| ![Alerts](docs/screenshots/alerts.png) | ![Reports](docs/screenshots/reports.png) | ![Login](docs/screenshots/login.png) |

---

## Live URLs

| Service | URL |
|---|---|
| **Frontend (Vercel)** | `https://competitor-intel-<your-id>.vercel.app` |
| **Backend API (Render)** | `https://competitor-intel-api.onrender.com` |
| **Swagger UI** | `https://competitor-intel-api.onrender.com/swagger-ui.html` |
| **Health Check** | `https://competitor-intel-api.onrender.com/actuator/health` |

> Replace the placeholders above with your actual URLs after completing the deployment steps below.

---

## Features

- **Automated Intelligence Gathering** — Web scraping via JSoup + Selenium; RSS/Atom feed parsing; configurable intervals
- **AI Enrichment Pipeline** — Dual-provider AI (OpenAI GPT-4 / Google Gemini 1.5) with automatic fallback to a safe mock; auto-classifies category, sentiment, relevance, and importance
- **Real-Time Dashboard** — KPI cards, category bar chart, sentiment donut chart, trend area chart, top-competitor activity chart
- **Competitor Management** — Full CRUD with search, industry/status filtering, priority scoring, paginated card grid
- **Intelligence Events** — Searchable, filterable event feed with AI summaries, key insights, tags, and manual enrich/flag actions
- **Alert Engine** — Configurable alert rules (by category, sentiment, keywords); in-app notifications + email-ready architecture
- **Reports & Export** — CSV and PDF export (streamed as blob downloads); report history with metadata
- **JWT Authentication** — Stateless Bearer-token auth; access + refresh tokens; protected routes; role-based access (ADMIN / ANALYST / VIEWER)
- **Dark / Light Mode** — Persisted theme preference
- **Responsive UI** — Mobile-first with collapsible sidebar drawer

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    React 18 Frontend (Vite)                     │
│     Pages · Components · React Query · Axios · Tailwind CSS     │
└─────────────────────────┬───────────────────────────────────────┘
                          │  REST / JSON over HTTPS
┌─────────────────────────▼───────────────────────────────────────┐
│              Spring Boot 3 Backend (Java 21)                    │
│  Controllers → Services → Repositories → JPA → Flyway           │
│       Security (JWT) · AI Module · Scraping Engine              │
└─────────────────────────┬───────────────────────────────────────┘
                          │  JDBC / PostgreSQL wire protocol
┌─────────────────────────▼───────────────────────────────────────┐
│              PostgreSQL 16 (Render Managed)                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## Tech Stack

### Backend (`competitor-intelligence/`)

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security 6 + JWT (jjwt 0.12) |
| Persistence | Spring Data JPA + Hibernate 6 |
| Database (prod) | PostgreSQL 16 (Render managed) |
| Database (dev/test) | H2 in-memory |
| Migrations | Flyway |
| Web Scraping | JSoup + Selenium 4 |
| RSS Parsing | Rome 2 |
| AI Integration | OpenAI GPT-4 / Google Gemini 1.5 |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| PDF Export | iText 7 |
| CSV Export | OpenCSV |
| Build | Maven 3 |
| Containerisation | Docker + Docker Compose |
| Hosting | Render (Web Service + Managed PostgreSQL) |

### Frontend (`competitor-intel-frontend/`)

| Concern | Technology |
|---|---|
| Framework | React 18 |
| Build Tool | Vite 5 |
| Language | TypeScript 5 |
| Styling | Tailwind CSS 3 |
| Routing | React Router DOM 6 |
| Server State | TanStack React Query 5 |
| HTTP Client | Axios 1.7 |
| Forms | React Hook Form 7 + Zod |
| Charts | Recharts 2 |
| Animations | Framer Motion 11 |
| Icons | Lucide React |
| Hosting | Vercel |

---

## Project Structure

```
AI-Powered-Competitor-Intelligence/
│
├── competitor-intelligence/          # Spring Boot backend
│   ├── src/main/java/...             # Application source
│   ├── src/main/resources/
│   │   ├── application.yml           # Base config (all profiles)
│   │   ├── application-dev.yml       # H2 in-memory (local dev)
│   │   ├── application-prod.yml      # PostgreSQL (Render)
│   │   └── db/
│   │       ├── migration/            # Flyway scripts — H2 (dev)
│   │       └── migration-postgresql/ # Flyway scripts — PostgreSQL (prod)
│   ├── start.sh                      # Render startup wrapper (URL conversion)
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── render.yaml                   # Render IaC — web service + database
│   ├── pom.xml
│   └── .env.example
│
├── competitor-intel-frontend/        # React frontend
│   ├── src/
│   │   ├── components/               # Reusable UI + layout + dashboard widgets
│   │   ├── context/                  # Auth + Theme contexts
│   │   ├── hooks/                    # React Query hooks per domain
│   │   ├── pages/                    # Route pages (lazy-loaded)
│   │   ├── routes/                   # AppRoutes + ProtectedRoute
│   │   ├── services/                 # Axios service modules
│   │   ├── types/                    # TypeScript interfaces
│   │   └── utils/                    # Helpers (cn, formatters)
│   ├── vercel.json                   # Vercel deployment config (SPA rewrites)
│   ├── .env.example
│   └── package.json
│
├── .gitignore
├── LICENSE
└── README.md
```

---

## Prerequisites

| Tool | Version |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| npm | 9+ |
| Docker | 24+ (optional, for local containerised dev) |

---

## Local Development

### 1 — Clone the repository

```bash
git clone https://github.com/your-username/AI-Powered-Competitor-Intelligence.git
cd AI-Powered-Competitor-Intelligence
```

### 2 — Backend (H2 in-memory — zero setup)

```bash
cd competitor-intelligence
mvn spring-boot:run
```

The `dev` profile activates automatically and uses H2. No environment variables or database installation needed.

- **API base:** `http://localhost:8080`
- **H2 console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:competitor_intel_db`)
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

### 3 — Frontend

```bash
cd competitor-intel-frontend
npm install
cp .env.example .env          # VITE_API_BASE_URL=http://localhost:8080 already set
npm run dev
```

- **Frontend:** `http://localhost:5173`

---

## Default Credentials (dev seed data)

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin123!` | ADMIN |
| `analyst` | `Analyst123!` | ANALYST |
| `viewer` | `Viewer123!` | VIEWER |

> **Change all passwords immediately after any production deployment.**

---

## Deployment

### Backend → Render

#### Step 1 — Push to GitHub

Ensure the repository (including `competitor-intelligence/`) is pushed to GitHub.

#### Step 2 — Create a new Render Web Service

1. Go to [render.com](https://render.com) → **New** → **Web Service**
2. Connect your GitHub repository
3. Set **Root Directory** to `competitor-intelligence`
4. Render will detect `render.yaml` automatically and pre-fill settings

#### Step 3 — Attach a PostgreSQL database

If using `render.yaml` (Infrastructure as Code):

```bash
# From the repository root, render.yaml defines both the web service
# and the PostgreSQL database. Render creates both on first deploy.
# The DATABASE_URL is injected automatically into the web service.
```

Or manually in the Render dashboard:
1. **New** → **PostgreSQL** → name it `competitor-intel-db`
2. In your web service → **Environment** → add `DATABASE_URL` → select the database

#### Step 4 — Set secret environment variables

In Render dashboard → **Service** → **Environment**, add:

| Variable | Value |
|---|---|
| `JWT_SECRET` | Output of `openssl rand -base64 64` |
| `ALLOWED_ORIGINS` | Your Vercel frontend URL (set after Step 6 below) |

Leave `AI_PROVIDER=mock` unless you have OpenAI/Gemini keys.

#### Step 5 — Deploy

Click **Deploy** (or push to the connected branch). Render will:
1. Run `mvn clean package -DskipTests -q`
2. Run `bash start.sh` — which converts `DATABASE_URL` from `postgres://` to `jdbc:postgresql://` and starts the JVM with the `prod` profile
3. Run Flyway migrations against PostgreSQL on first boot
4. Expose the service at `https://competitor-intel-api.onrender.com`

> **Note on Render free tier:** The service spins down after 15 minutes of inactivity. The first request after a cold start may take 30–60 seconds.

#### Step 6 — Verify backend health

```
GET https://competitor-intel-api.onrender.com/actuator/health
→ {"status":"UP"}
```

---

### Frontend → Vercel

#### Step 1 — Push to GitHub

Ensure the repository (including `competitor-intel-frontend/`) is pushed to GitHub.

#### Step 2 — Import project in Vercel

1. Go to [vercel.com](https://vercel.com) → **Add New Project**
2. Import your GitHub repository
3. Set **Root Directory** to `competitor-intel-frontend`
4. Framework preset: **Vite** (auto-detected via `vercel.json`)

#### Step 3 — Set environment variables

In Vercel → **Project Settings** → **Environment Variables**, add:

| Variable | Value |
|---|---|
| `VITE_API_BASE_URL` | `https://competitor-intel-api.onrender.com` |
| `VITE_API_TIMEOUT` | `15000` |

> `VITE_API_BASE_URL` must point to your Render backend URL — no trailing slash.

#### Step 4 — Deploy

Click **Deploy**. Vercel will run `npm install` then `npm run build` (TypeScript check + Vite build). The `vercel.json` SPA rewrite ensures all routes serve `index.html`.

Your frontend will be live at `https://competitor-intel-<your-id>.vercel.app`.

#### Step 5 — Update CORS on Render

Go back to Render → **Service** → **Environment** and update:

```
ALLOWED_ORIGINS = https://competitor-intel-<your-id>.vercel.app
```

Trigger a redeploy. The backend CORS configuration reads this value at startup.

---

## Environment Variables Reference

### Backend — `competitor-intelligence/.env.example`

| Variable | Required | Where Set | Description |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | No | Render / local | `dev` (default) or `prod` |
| `DATABASE_URL` | Prod | Render (auto) | Injected from attached PostgreSQL. `start.sh` converts `postgres://` → `jdbc:postgresql://` |
| `SPRING_DATASOURCE_URL` | Prod (non-Render) | Manual | Full JDBC URL if not using Render. E.g. `jdbc:postgresql://host:5432/db` |
| `SPRING_DATASOURCE_USERNAME` | Prod (non-Render) | Manual | DB username |
| `SPRING_DATASOURCE_PASSWORD` | Prod (non-Render) | Manual | DB password |
| `JWT_SECRET` | **Prod required** | Render dashboard | Min 64-char random string. Generate: `openssl rand -base64 64` |
| `JWT_EXPIRATION` | No | Render | Access token TTL ms (default `86400000` = 24 h) |
| `JWT_REFRESH_EXPIRATION` | No | Render | Refresh token TTL ms (default `604800000` = 7 d) |
| `ALLOWED_ORIGINS` | **Prod required** | Render dashboard | Vercel frontend URL(s). Comma-separate multiple origins |
| `AI_PROVIDER` | No | Render | `mock` \| `openai` \| `gemini` (default `mock`) |
| `OPENAI_API_KEY` | If using OpenAI | Render dashboard | OpenAI API key |
| `GEMINI_API_KEY` | If using Gemini | Render dashboard | Google Gemini API key |
| `MAIL_HOST` | No | Render | SMTP host for alert emails |
| `MAIL_PORT` | No | Render | SMTP port (default `587`) |
| `MAIL_USERNAME` | No | Render dashboard | SMTP username |
| `MAIL_PASSWORD` | No | Render dashboard | SMTP password |

### Frontend — `competitor-intel-frontend/.env.example`

| Variable | Required | Where Set | Description |
|---|---|---|---|
| `VITE_API_BASE_URL` | **Yes** | Vercel dashboard | Render backend URL — no trailing slash. E.g. `https://competitor-intel-api.onrender.com` |
| `VITE_API_TIMEOUT` | No | Vercel dashboard | Request timeout ms (default `15000`) |

---

## Build Verification

### Backend

```bash
cd competitor-intelligence
mvn clean package -DskipTests -q
# ✓ Produces: target/competitor-intelligence-platform-1.0.0.jar
```

### Frontend

```bash
cd competitor-intel-frontend
npm run build
# ✓ TypeScript check passes
# ✓ Vite build produces: dist/
```

---

## API Documentation

| Environment | URL |
|---|---|
| Local | `http://localhost:8080/swagger-ui.html` |
| Production | `https://competitor-intel-api.onrender.com/swagger-ui.html` |

OpenAPI JSON:

| Environment | URL |
|---|---|
| Local | `http://localhost:8080/api-docs` |
| Production | `https://competitor-intel-api.onrender.com/api-docs` |

---

## Running Tests

### Backend

```bash
cd competitor-intelligence
mvn test                                      # Run all tests (uses H2)
mvn test -Dtest=CompetitorServiceTest         # Run a specific test class
```

### Frontend

```bash
cd competitor-intel-frontend
npx tsc --noEmit                              # TypeScript type check
npm run build                                 # Full production build check
```

---

## Docker (Local Full Stack)

```bash
cd competitor-intelligence
cp .env.example .env    # Fill in JWT_SECRET at minimum

docker-compose up --build
```

Services started:
- **App** → `http://localhost:8080`
- **MailHog UI** → `http://localhost:8025` (catches outbound emails locally)

---

## Production Checklist

Before going live, confirm each item:

- [ ] `JWT_SECRET` set to a strong random value (`openssl rand -base64 64`)
- [ ] `ALLOWED_ORIGINS` set to the exact Vercel frontend URL (no trailing slash)
- [ ] `VITE_API_BASE_URL` set to the exact Render backend URL in Vercel
- [ ] PostgreSQL database attached to the Render web service
- [ ] `GET /actuator/health` returns `{"status":"UP"}`
- [ ] Login works end-to-end from the Vercel frontend
- [ ] Default seed passwords changed (`Admin123!`, `Analyst123!`, `Viewer123!`)
- [ ] `AI_PROVIDER` configured if real AI enrichment is needed

---

## Future Enhancements

- [ ] Real-time WebSocket notifications
- [ ] Advanced NLP keyword extraction
- [ ] Competitor benchmarking comparison view
- [ ] Scheduled report delivery via email
- [ ] Multi-tenant / team support
- [ ] Integration with LinkedIn, Twitter/X APIs
- [ ] Browser extension for manual event capture
- [ ] Prometheus + Grafana monitoring dashboard
- [ ] Kubernetes deployment manifests

---

## License

MIT License — see [LICENSE](LICENSE) for details.
