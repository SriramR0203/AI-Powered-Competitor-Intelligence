import { useState } from 'react'
import { motion } from 'framer-motion'
import { Moon, Sun } from 'lucide-react'
import { Card, CardHeader } from '../components/ui/Card'
import { Button } from '../components/ui/Button'
import { useTheme } from '../context/ThemeContext'

interface ToggleProps {
  label: string
  description: string
  value: boolean
  onChange: (v: boolean) => void
}
function Toggle({ label, description, value, onChange }: ToggleProps) {
  return (
    <div className="flex items-center justify-between py-3">
      <div>
        <p className="text-sm font-medium text-gray-900 dark:text-white">{label}</p>
        <p className="text-xs text-gray-500">{description}</p>
      </div>
      <button
        type="button"
        onClick={() => onChange(!value)}
        className={`relative w-11 h-6 rounded-full transition-colors duration-200 ${
          value ? 'bg-brand-600' : 'bg-gray-200 dark:bg-gray-700'
        }`}
      >
        <span
          className={`absolute top-0.5 left-0.5 w-5 h-5 rounded-full bg-white shadow transition-transform duration-200 ${
            value ? 'translate-x-5' : 'translate-x-0'
          }`}
        />
      </button>
    </div>
  )
}

export function SettingsPage() {
  const { theme, setTheme } = useTheme()
  const [emailAlerts,       setEmailAlerts]       = useState(true)
  const [inAppAlerts,       setInAppAlerts]        = useState(true)
  const [autoEnrich,        setAutoEnrich]         = useState(true)
  const [twoFactor,         setTwoFactor]          = useState(false)
  const [scheduledScraping, setScheduledScraping]  = useState(true)

  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      className="max-w-2xl mx-auto space-y-5"
    >
      <div>
        <h2 className="text-lg font-bold text-gray-900 dark:text-white">Settings</h2>
        <p className="text-sm text-gray-500">Configure your intelligence platform preferences</p>
      </div>

      {/* Appearance */}
      <Card>
        <CardHeader title="Appearance" subtitle="Theme and display settings" />
        <p className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-3">Color Theme</p>
        <div className="flex gap-3">
          {(['light', 'dark'] as const).map((t) => (
            <button
              key={t}
              type="button"
              onClick={() => setTheme(t)}
              className={`flex items-center gap-2 px-4 py-2.5 rounded-xl border text-sm font-medium transition-all ${
                theme === t
                  ? 'border-brand-500 bg-brand-50 dark:bg-brand-950/30 text-brand-700 dark:text-brand-400'
                  : 'border-gray-200 dark:border-gray-700 text-gray-600 dark:text-gray-400 hover:border-gray-300'
              }`}
            >
              {t === 'light' ? <Sun size={15} /> : <Moon size={15} />}
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          ))}
        </div>
      </Card>

      {/* Notifications */}
      <Card>
        <CardHeader title="Notifications" subtitle="Control how you receive alerts" />
        <div className="divide-y divide-gray-100 dark:divide-gray-700">
          <Toggle label="Email Alerts"        description="Receive alert notifications via email"  value={emailAlerts}  onChange={setEmailAlerts} />
          <Toggle label="In-App Notifications" description="Show notifications in the platform"    value={inAppAlerts}  onChange={setInAppAlerts} />
        </div>
      </Card>

      {/* AI & Scraping */}
      <Card>
        <CardHeader title="AI & Scraping" subtitle="Intelligence collection settings" />
        <div className="divide-y divide-gray-100 dark:divide-gray-700">
          <Toggle label="Auto-Enrich Events"  description="Automatically run AI enrichment on new events" value={autoEnrich}        onChange={setAutoEnrich} />
          <Toggle label="Scheduled Scraping"  description="Run scraping jobs on the configured schedule"  value={scheduledScraping} onChange={setScheduledScraping} />
        </div>
        <div className="mt-4">
          <label className="label">AI Provider</label>
          <select className="input text-sm max-w-xs">
            <option value="mock">Mock (Development)</option>
            <option value="openai">OpenAI GPT-4</option>
            <option value="gemini">Google Gemini</option>
          </select>
          <p className="mt-1.5 text-xs text-gray-400">
            Configure your API key in the server-side <code className="font-mono">application.yml</code>.
          </p>
        </div>
      </Card>

      {/* Security */}
      <Card>
        <CardHeader title="Security" subtitle="Account security settings" />
        <div className="divide-y divide-gray-100 dark:divide-gray-700">
          <Toggle label="Two-Factor Authentication" description="Add an extra layer of security to your account" value={twoFactor} onChange={setTwoFactor} />
        </div>
      </Card>

      <div className="flex justify-end gap-3">
        <Button variant="secondary" type="button">Reset Defaults</Button>
        <Button type="button">Save Changes</Button>
      </div>
    </motion.div>
  )
}
