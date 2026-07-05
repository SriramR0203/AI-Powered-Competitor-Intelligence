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

## Features

- **Automated Intelligence Gathering** — Web scraping via JSoup + Selenium; RSS/Atom feed parsing; configurable intervals
- **AI Enrichment Pipeline** — Dual-provider AI (OpenAI GPT-4 / Google Gemini) with automatic fallback to a safe mock; auto-classifies category, sentiment, relevance, and importance
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
                          │  REST / JSON over HTTP
┌─────────────────────────▼───────────────────────────────────────┐
│              Spring Boot 3 Backend (Java 21)                    │
│  Controllers → Services → Repositories → JPA → Flyway → MySQL  │
│       Security (JWT) · AI Module · Scraping Engine              │
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
| Database (prod) | MySQL 8 |
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
| CI/CD | GitHub Actions |
| Testing | JUnit 5 + Mockito |

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

---

## Project Structure

```
AI-Powered-Competitor-Intelligence/
│
├── competitor-intelligence/          # Spring Boot backend
│   ├── src/main/java/...             # Application source
│   ├── src/main/resources/           # Config + Flyway migrations
│   ├── src/test/                     # Unit + integration tests
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── pom.xml
│   ├── .env.example                  # Environment variable template
│   └── .gitignore
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
│   ├── .env.example
│   ├── .gitignore
│   └── README.md
│
├── .gitignore
├── LICENSE
└── README.md                         # ← You are here
```

---

## Prerequisites

| Tool | Version |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| npm | 9+ |
| Docker | 24+ (optional) |
| MySQL | 8.0+ (for production) |

---

## Installation & Running Locally

### 1 — Clone the repository

```bash
git clone https://github.com/your-username/AI-Powered-Competitor-Intelligence.git
cd AI-Powered-Competitor-Intelligence
```

---

### 2 — Backend Setup

#### Option A — H2 in-memory (quickest, no database needed)

```bash
cd competitor-intelligence
mvn spring-boot:run
```

The `dev` profile is active by default and uses H2. No environment variables needed for local development.

#### Option B — MySQL via Docker Compose

```bash
cd competitor-intelligence

# Start MySQL + MailHog
docker-compose up -d mysql mailhog

# Copy and fill in the env file
cp .env.example .env
# Edit .env — set DB_USERNAME, DB_PASSWORD, JWT_SECRET at minimum

# Run the backend
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Option C — Full Docker Compose stack

```bash
cd competitor-intelligence
cp .env.example .env   # Fill in all required values
docker-compose up --build
```

**Backend will be available at:** `http://localhost:8080`

---

### 3 — Frontend Setup

```bash
cd competitor-intel-frontend

# Install dependencies
npm install

# Copy environment template
cp .env.example .env
# VITE_API_BASE_URL=http://localhost:8080   (already set in .env.example)

# Start development server
npm run dev
```

**Frontend will be available at:** `http://localhost:5173`

The Vite dev server proxies `/api/*` requests to the backend automatically.

---

## Environment Variables

### Backend — `competitor-intelligence/.env`

| Variable | Required | Description |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | No | `dev` (default) or `prod` |
| `DB_HOST` | Prod only | MySQL host |
| `DB_PORT` | Prod only | MySQL port (default `3306`) |
| `DB_NAME` | Prod only | Database name |
| `DB_USERNAME` | Prod only | Database username |
| `DB_PASSWORD` | Prod only | Database password |
| `JWT_SECRET` | **Prod required** | Min 64-char random string. Generate: `openssl rand -base64 64` |
| `JWT_EXPIRATION` | No | Access token TTL ms (default `86400000` = 24h) |
| `AI_PROVIDER` | No | `mock` \| `openai` \| `gemini` (default `mock`) |
| `OPENAI_API_KEY` | If using OpenAI | OpenAI API key |
| `GEMINI_API_KEY` | If using Gemini | Google Gemini API key |
| `MAIL_HOST` | No | SMTP host for alert emails |

### Frontend — `competitor-intel-frontend/.env`

| Variable | Required | Description |
|---|---|---|
| `VITE_API_BASE_URL` | Yes | Backend base URL (e.g. `http://localhost:8080`) |
| `VITE_API_TIMEOUT` | No | Request timeout ms (default `15000`) |

---

## API Documentation

Once the backend is running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:

```
http://localhost:8080/api-docs
```

---

## Default Credentials (dev seed data)

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin123!` | ADMIN |
| `analyst` | `Analyst123!` | ANALYST |
| `viewer` | `Viewer123!` | VIEWER |

> **These are development-only seed credentials. Change all passwords immediately after any deployment.**

---

## Build for Production

### Backend

```bash
cd competitor-intelligence
mvn clean package -DskipTests
# Output: target/competitor-intelligence-platform-1.0.0.jar
java -jar target/competitor-intelligence-platform-1.0.0.jar
```

### Frontend

```bash
cd competitor-intel-frontend
npm run build
# Output: dist/ — serve with nginx or any static server
```

---

## Running Tests

### Backend

```bash
cd competitor-intelligence
mvn test                          # Run all tests
mvn test -Dtest=CompetitorServiceTest   # Run specific test
```

### Frontend

```bash
cd competitor-intel-frontend
npx tsc --noEmit                  # TypeScript check
npm run build                     # Full production build check
```

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
