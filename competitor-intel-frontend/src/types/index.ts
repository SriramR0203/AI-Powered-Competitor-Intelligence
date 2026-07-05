// ── Auth ──────────────────────────────────────────────────────────────────
export interface User {
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
  fullName: string
  roles: string[]
  active: boolean
  emailVerified: boolean
  lastLoginAt: string | null
  createdAt: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: User
}

// ── Competitor ────────────────────────────────────────────────────────────
export type CompetitorStatus = 'ACTIVE' | 'INACTIVE' | 'ARCHIVED' | 'PENDING_REVIEW'

export interface Competitor {
  id: number
  name: string
  websiteUrl: string
  description: string | null
  industry: string | null
  headquarters: string | null
  employeeCountRange: string | null
  foundedYear: number | null
  linkedinUrl: string | null
  twitterHandle: string | null
  logoUrl: string | null
  status: CompetitorStatus
  priorityScore: number
  notes: string | null
  activeSourceCount: number
  recentEventCount: number
  createdAt: string
  updatedAt: string
  createdBy: string
}

export interface CompetitorSummary {
  id: number
  name: string
  websiteUrl: string
  industry: string | null
  status: CompetitorStatus
  priorityScore: number
  logoUrl: string | null
}

// ── Intelligence Source ───────────────────────────────────────────────────
export type SourceType = 'WEBSITE' | 'RSS_FEED' | 'NEWS_API' | 'SOCIAL_MEDIA' | 'PRESS_RELEASE' | 'BLOG' | 'JOB_BOARD'

export interface IntelligenceSource {
  id: number
  competitorId: number
  competitorName: string
  name: string
  url: string
  sourceType: SourceType
  scrapeIntervalHours: number
  cssSelector: string | null
  requiresJavascript: boolean
  active: boolean
  lastScrapedAt: string | null
  nextScrapeAt: string | null
  consecutiveFailures: number
  notes: string | null
  createdAt: string
}

// ── Intelligence Event ────────────────────────────────────────────────────
export type IntelligenceCategory =
  | 'PRODUCT_LAUNCH' | 'PRICING_CHANGE' | 'PARTNERSHIP' | 'ACQUISITION'
  | 'LEADERSHIP_CHANGE' | 'FUNDING' | 'LEGAL' | 'MARKETING_CAMPAIGN'
  | 'TECHNOLOGY' | 'HIRING' | 'CUSTOMER_WIN' | 'CUSTOMER_LOSS'
  | 'REGULATORY' | 'GENERAL_NEWS' | 'UNCLASSIFIED'

export type SentimentType = 'VERY_POSITIVE' | 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE' | 'VERY_NEGATIVE'
export type ProcessingStatus = 'RAW' | 'PROCESSING' | 'ENRICHED' | 'FAILED' | 'SKIPPED'

export interface IntelligenceEvent {
  id: number
  competitorId: number
  competitorName: string
  sourceId: number | null
  sourceName: string | null
  title: string
  url: string | null
  summary: string | null
  aiSummary: string | null
  keyInsights: string | null
  category: IntelligenceCategory
  sentiment: SentimentType
  sentimentScore: number | null
  relevanceScore: number | null
  importanceScore: number | null
  processingStatus: ProcessingStatus
  aiProvider: string | null
  aiModel: string | null
  publishedAt: string | null
  processedAt: string | null
  author: string | null
  imageUrl: string | null
  language: string
  flagged: boolean
  flagReason: string | null
  tags: string[]
  createdAt: string
}

// ── Alerts ────────────────────────────────────────────────────────────────
export type AlertSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
export type AlertStatus   = 'PENDING' | 'SENT' | 'ACKNOWLEDGED' | 'DISMISSED' | 'FAILED'

export interface AlertRule {
  id: number
  name: string
  description: string | null
  competitorId: number | null
  competitorName: string | null
  categoryFilter: IntelligenceCategory | null
  sentimentFilter: SentimentType | null
  keywordFilter: string | null
  severity: AlertSeverity
  active: boolean
  notifyEmail: boolean
  notifyInApp: boolean
  cooldownMinutes: number
  createdAt: string
}

export interface AlertNotification {
  id: number
  alertRuleId: number
  alertRuleName: string
  eventId: number
  eventTitle: string
  competitorName: string
  title: string
  message: string
  severity: AlertSeverity
  status: AlertStatus
  sentAt: string | null
  acknowledgedAt: string | null
  createdAt: string
}

// ── Reports ───────────────────────────────────────────────────────────────
export interface Report {
  id: number
  name: string
  description: string | null
  competitorId: number | null
  competitorName: string | null
  reportType: string
  format: string
  fileSizeBytes: number | null
  dateFrom: string | null
  dateTo: string | null
  rowCount: number | null
  generatedAt: string | null
  downloadCount: number
  createdAt: string
  createdBy: string
}

// ── Dashboard ─────────────────────────────────────────────────────────────
export interface DashboardStats {
  totalCompetitors: number
  activeCompetitors: number
  totalSources: number
  activeSources: number
  eventsLast30Days: number
  eventsLast7Days: number
  enrichedEvents: number
  pendingAlerts: number
  eventsByCategory: Record<string, number>
  eventsBySentiment: Record<string, number>
  eventsTrend: { date: string; count: number }[]
  topCompetitors: { id: number; name: string; eventCount: number }[]
  scrapeSuccessRate: { total: number; success: number; failed: number }
}

// ── Pagination ────────────────────────────────────────────────────────────
export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
  first: boolean
}

// ── Scraping ──────────────────────────────────────────────────────────────
export type ScrapingStatus = 'PENDING' | 'RUNNING' | 'SUCCESS' | 'PARTIAL_SUCCESS' | 'FAILED' | 'SKIPPED'
export interface ScrapingJob {
  id: number
  sourceId: number
  sourceName: string
  competitorId: number
  competitorName: string
  status: ScrapingStatus
  startedAt: string | null
  completedAt: string | null
  durationMs: number | null
  itemsScraped: number
  itemsNew: number
  errorMessage: string | null
  retryCount: number
  triggeredBy: string
  createdAt: string
}
