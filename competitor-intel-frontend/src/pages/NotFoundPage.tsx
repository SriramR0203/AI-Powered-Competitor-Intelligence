import { motion } from 'framer-motion'
import { useNavigate } from 'react-router-dom'
import { TrendingUp } from 'lucide-react'
import { Button } from '../components/ui/Button'

export function NotFoundPage() {
  const navigate = useNavigate()
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 px-4">
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="text-center max-w-md">
        <div className="w-20 h-20 rounded-3xl bg-brand-100 dark:bg-brand-950/50 flex items-center justify-center mx-auto mb-6">
          <TrendingUp size={36} className="text-brand-600" />
        </div>
        <h1 className="text-7xl font-black text-gray-200 dark:text-gray-800">404</h1>
        <h2 className="text-xl font-bold text-gray-900 dark:text-white mt-2">Page not found</h2>
        <p className="text-gray-500 mt-2 text-sm">The intelligence you're looking for doesn't exist here.</p>
        <div className="mt-6 flex justify-center gap-3">
          <Button onClick={() => navigate(-1)} variant="secondary">Go Back</Button>
          <Button onClick={() => navigate('/dashboard')}>Dashboard</Button>
        </div>
      </motion.div>
    </div>
  )
}
