import { AlertTriangle } from 'lucide-react'
import { Modal } from './Modal'
import { Button } from './Button'

interface ConfirmDialogProps {
  open:        boolean
  title:       string
  message:     string
  confirmLabel?: string
  cancelLabel?:  string
  danger?:     boolean
  loading?:    boolean
  onConfirm:   () => void
  onCancel:    () => void
}

/**
 * Accessible, non-blocking confirmation dialog.
 * Replaces native window.confirm() throughout the application.
 */
export function ConfirmDialog({
  open,
  title,
  message,
  confirmLabel = 'Delete',
  cancelLabel  = 'Cancel',
  danger       = true,
  loading      = false,
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  return (
    <Modal open={open} onClose={onCancel} size="sm">
      <div className="flex flex-col items-center text-center gap-4">
        <div className={`w-12 h-12 rounded-2xl flex items-center justify-center ${
          danger ? 'bg-red-100 dark:bg-red-900/30' : 'bg-amber-100 dark:bg-amber-900/30'
        }`}>
          <AlertTriangle
            size={22}
            className={danger ? 'text-red-600' : 'text-amber-600'}
          />
        </div>

        <div>
          <h3 className="text-sm font-semibold text-gray-900 dark:text-white mb-1">{title}</h3>
          <p className="text-sm text-gray-500 dark:text-gray-400">{message}</p>
        </div>

        <div className="flex gap-3 w-full">
          <Button
            variant="secondary"
            className="flex-1"
            onClick={onCancel}
            disabled={loading}
          >
            {cancelLabel}
          </Button>
          <Button
            variant={danger ? 'danger' : 'primary'}
            className="flex-1"
            loading={loading}
            onClick={onConfirm}
          >
            {confirmLabel}
          </Button>
        </div>
      </div>
    </Modal>
  )
}
