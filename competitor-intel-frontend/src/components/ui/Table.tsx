import React from 'react'
import { cn } from '../../utils/cn'

interface TableProps { children: React.ReactNode; className?: string }

export function Table({ children, className }: TableProps) {
  return (
    <div className="overflow-x-auto">
      <table className={cn('w-full text-sm', className)}>
        {children}
      </table>
    </div>
  )
}

export function Thead({ children }: { children: React.ReactNode }) {
  return <thead className="border-b border-gray-100 dark:border-gray-700">{children}</thead>
}

export function Th({ children, className }: { children: React.ReactNode; className?: string }) {
  return <th className={cn('px-4 py-3 text-left text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide whitespace-nowrap', className)}>{children}</th>
}

export function Tbody({ children }: { children: React.ReactNode }) {
  return <tbody className="divide-y divide-gray-50 dark:divide-gray-700/50">{children}</tbody>
}

export function Tr({ children, onClick, className }: { children: React.ReactNode; onClick?: () => void; className?: string }) {
  return (
    <tr
      onClick={onClick}
      className={cn('transition-colors', onClick && 'cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/30', className)}
    >
      {children}
    </tr>
  )
}

export function Td({ children, className }: { children: React.ReactNode; className?: string }) {
  return <td className={cn('px-4 py-3 text-gray-700 dark:text-gray-300', className)}>{children}</td>
}
