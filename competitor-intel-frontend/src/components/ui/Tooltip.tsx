import React, { useState } from 'react'
import { cn } from '../../utils/cn'

interface TooltipProps { content: string; children: React.ReactNode; className?: string }

export function Tooltip({ content, children, className }: TooltipProps) {
  const [show, setShow] = useState(false)
  return (
    <div className={cn('relative inline-flex', className)} onMouseEnter={() => setShow(true)} onMouseLeave={() => setShow(false)}>
      {children}
      {show && (
        <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 z-50 pointer-events-none">
          <div className="bg-gray-900 dark:bg-gray-700 text-white text-xs rounded-lg px-2.5 py-1.5 whitespace-nowrap shadow-lg">
            {content}
            <div className="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-gray-900 dark:border-t-gray-700" />
          </div>
        </div>
      )}
    </div>
  )
}
