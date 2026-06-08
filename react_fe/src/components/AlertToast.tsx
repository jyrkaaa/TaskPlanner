import type { Alert, AlertType } from '../context/AlertContext'

interface Props {
  alerts: Alert[]
  onDismiss: (id: number) => void
}

const palette: Record<AlertType, { bg: string; border: string; color: string; icon: string }> = {
  error:   { bg: '#fff0f0', border: '#fca5a5', color: '#b91c1c', icon: '✕' },
  success: { bg: '#f0fdf4', border: '#86efac', color: '#15803d', icon: '✓' },
  warning: { bg: '#fffbeb', border: '#fcd34d', color: '#b45309', icon: '!' },
  info:    { bg: '#eff6ff', border: '#93c5fd', color: '#1d4ed8', icon: 'i' },
}

const darkPalette: Record<AlertType, { bg: string; border: string; color: string }> = {
  error:   { bg: '#2d1515', border: '#7f1d1d', color: '#fca5a5' },
  success: { bg: '#0f2d1a', border: '#166534', color: '#86efac' },
  warning: { bg: '#2d2210', border: '#78350f', color: '#fcd34d' },
  info:    { bg: '#0f1e2d', border: '#1e3a5f', color: '#93c5fd' },
}

function AlertItem({ alert, onDismiss }: { alert: Alert; onDismiss: (id: number) => void }) {
  const p = palette[alert.type]
  const d = darkPalette[alert.type]

  return (
    <div
      role="alert"
      style={{
        display: 'flex',
        alignItems: 'flex-start',
        gap: '10px',
        padding: '12px 16px',
        borderRadius: '10px',
        border: `1px solid ${p.border}`,
        background: p.bg,
        color: p.color,
        boxShadow: '0 4px 12px rgba(0,0,0,0.12)',
        fontSize: '14px',
        fontWeight: 500,
        minWidth: '280px',
        maxWidth: '420px',
        animation: 'alert-slide-in 0.22s ease',
      }}
    >
      <span style={{
        flexShrink: 0,
        width: '20px',
        height: '20px',
        borderRadius: '50%',
        border: `1.5px solid ${p.color}`,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontSize: '11px',
        fontWeight: 700,
        marginTop: '1px',
      }}>
        {p.icon}
      </span>

      <span style={{ flex: 1, lineHeight: '1.45' }}>{alert.message}</span>

      <button
        onClick={() => onDismiss(alert.id)}
        aria-label="Dismiss"
        style={{
          flexShrink: 0,
          background: 'none',
          border: 'none',
          cursor: 'pointer',
          color: p.color,
          opacity: 0.6,
          fontSize: '16px',
          lineHeight: 1,
          padding: '0 0 0 4px',
          marginTop: '1px',
        }}
      >
        ×
      </button>

      {/* dark-mode overrides via CSS custom properties aren't available inline,
          so we inject a tiny style tag once per mount instead */}
      <style>{`
        @media (prefers-color-scheme: dark) {
          [data-alert-type="${alert.type}"] {
            background: ${d.bg} !important;
            border-color: ${d.border} !important;
            color: ${d.color} !important;
          }
        }
        @keyframes alert-slide-in {
          from { opacity: 0; transform: translateY(-12px) scale(0.97); }
          to   { opacity: 1; transform: translateY(0)     scale(1);    }
        }
      `}</style>
    </div>
  )
}

export default function AlertToast({ alerts, onDismiss }: Props) {
  if (alerts.length === 0) return null

  return (
    <div style={{
      position: 'fixed',
      top: '20px',
      left: '50%',
      transform: 'translateX(-50%)',
      zIndex: 9999,
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      gap: '8px',
      pointerEvents: 'none',
    }}>
      {alerts.map(alert => (
        <div key={alert.id} style={{ pointerEvents: 'auto' }} data-alert-type={alert.type}>
          <AlertItem alert={alert} onDismiss={onDismiss} />
        </div>
      ))}
    </div>
  )
}
