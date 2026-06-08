import { createContext, useCallback, useContext, useState, type ReactNode } from 'react'
import AlertToast from '../components/AlertToast'

export enum AlertTypes {
  ERROR = 'error',
  SUCCESS = 'success',
  WARNING = 'warning',
  INFO = 'info'
}

export interface Alert {
  id: number
  message: string
  type: AlertTypes
}

interface AlertContextValue {
  showAlert: (message: string, type?: AlertTypes) => void;
  info: (message: string) => void;
  success: (message: string) => void;
  warning: (message: string) => void;
  error: (message: string) => void;
}

const AlertContext = createContext<AlertContextValue | null>(null)

let nextId = 0

export function AlertProvider({ children }: { children: ReactNode }) {
  const [alerts, setAlerts] = useState<Alert[]>([])

  const dismiss = useCallback((id: number) => {
    setAlerts(prev => prev.filter(a => a.id !== id))
  }, [])

  const showAlert = useCallback((message: string, type: AlertTypes = AlertTypes.ERROR) => {
    const id = ++nextId
    setAlerts(prev => [...prev, { id, message, type }])
    setTimeout(() => dismiss(id), 4500)
  }, [dismiss])

  return (
    <AlertContext.Provider value={{ showAlert, info: (msg) => showAlert(msg, AlertTypes.INFO), success: (msg) => showAlert(msg, AlertTypes.SUCCESS), warning: (msg) => showAlert(msg, AlertTypes.WARNING), error: (msg) => showAlert(msg, AlertTypes.ERROR) }}>
      {children}
      <AlertToast alerts={alerts} onDismiss={dismiss} />
    </AlertContext.Provider>
  )
}

export function useAlert(): AlertContextValue {
  const ctx = useContext(AlertContext)
  if (!ctx) throw new Error('useAlert must be used inside AlertProvider')
  return ctx
}
