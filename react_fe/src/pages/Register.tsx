import { useState } from 'react'
import { Link } from 'react-router-dom'

interface FormErrors {
  username?: string
  email?: string
  password?: string
  confirmPassword?: string
}

function validate(username: string, email: string, password: string, confirmPassword: string): FormErrors {
  const errors: FormErrors = {}
  if (!username) {
    errors.username = 'Username is required'
  } else if (username.length < 3) {
    errors.username = 'Username must be at least 3 characters'
  }
  if (!email) {
    errors.email = 'Email is required'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    errors.email = 'Enter a valid email address'
  }
  if (!password) {
    errors.password = 'Password is required'
  } else if (password.length < 8) {
    errors.password = 'Password must be at least 8 characters'
  }
  if (!confirmPassword) {
    errors.confirmPassword = 'Please confirm your password'
  } else if (confirmPassword !== password) {
    errors.confirmPassword = 'Passwords do not match'
  }
  return errors
}

type TouchedFields = Record<'username' | 'email' | 'password' | 'confirmPassword', boolean>

function RegisterPage() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [errors, setErrors] = useState<FormErrors>({})
  const [touched, setTouched] = useState<TouchedFields>({
    username: false,
    email: false,
    password: false,
    confirmPassword: false,
  })

  function handleBlur(field: keyof TouchedFields) {
    setTouched((prev) => ({ ...prev, [field]: true }))
    setErrors(validate(username, email, password, confirmPassword))
  }

  function handleSubmit(e: { preventDefault(): void }) {
    e.preventDefault()
    setTouched({ username: true, email: true, password: true, confirmPassword: true })
    const errs = validate(username, email, password, confirmPassword)
    setErrors(errs)
    if (Object.keys(errs).length > 0) return
    // register logic goes here
  }

  function err(field: keyof TouchedFields) {
    return touched[field] ? errors[field] : undefined
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h2 style={styles.title}>Create an account</h2>
        <p style={styles.subtitle}>Get started with Task Planner</p>

        <form onSubmit={handleSubmit} noValidate style={styles.form}>
          <div style={styles.field}>
            <label style={styles.label} htmlFor="username">
              Username
            </label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              onBlur={() => handleBlur('username')}
              placeholder="johndoe"
              style={{ ...styles.input, ...(err('username') ? styles.inputError : {}) }}
              autoComplete="username"
            />
            {err('username') && <span style={styles.errorText}>{err('username')}</span>}
          </div>

          <div style={styles.field}>
            <label style={styles.label} htmlFor="email">
              Email
            </label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              onBlur={() => handleBlur('email')}
              placeholder="you@example.com"
              style={{ ...styles.input, ...(err('email') ? styles.inputError : {}) }}
              autoComplete="email"
            />
            {err('email') && <span style={styles.errorText}>{err('email')}</span>}
          </div>

          <div style={styles.field}>
            <label style={styles.label} htmlFor="password">
              Password
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onBlur={() => handleBlur('password')}
              placeholder="••••••••"
              style={{ ...styles.input, ...(err('password') ? styles.inputError : {}) }}
              autoComplete="new-password"
            />
            {err('password') && <span style={styles.errorText}>{err('password')}</span>}
          </div>

          <div style={styles.field}>
            <label style={styles.label} htmlFor="confirmPassword">
              Confirm password
            </label>
            <input
              id="confirmPassword"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              onBlur={() => handleBlur('confirmPassword')}
              placeholder="••••••••"
              style={{ ...styles.input, ...(err('confirmPassword') ? styles.inputError : {}) }}
              autoComplete="new-password"
            />
            {err('confirmPassword') && <span style={styles.errorText}>{err('confirmPassword')}</span>}
          </div>

          <button type="submit" style={styles.button}>
            Create account
          </button>
        </form>

        <p style={styles.footer}>
          Already have an account?{' '}
          <Link to="/login" style={styles.link}>
            Sign in
          </Link>
        </p>
      </div>
    </div>
  )
}

const styles = {
  page: {
    display: 'flex',
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: '40px 16px',
  } as React.CSSProperties,
  card: {
    width: '100%',
    maxWidth: '400px',
    border: '1px solid var(--border)',
    borderRadius: '12px',
    padding: '40px',
    boxShadow: 'var(--shadow)',
    background: 'var(--bg)',
    textAlign: 'left',
  } as React.CSSProperties,
  title: {
    margin: '0 0 4px',
    fontSize: '24px',
    fontWeight: 500,
    color: 'var(--text-h)',
    letterSpacing: '-0.24px',
  } as React.CSSProperties,
  subtitle: {
    margin: '0 0 28px',
    color: 'var(--text)',
    fontSize: '15px',
  } as React.CSSProperties,
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '20px',
  } as React.CSSProperties,
  field: {
    display: 'flex',
    flexDirection: 'column',
    gap: '6px',
  } as React.CSSProperties,
  label: {
    fontSize: '14px',
    fontWeight: 500,
    color: 'var(--text-h)',
  } as React.CSSProperties,
  input: {
    padding: '10px 12px',
    fontSize: '15px',
    border: '1px solid var(--border)',
    borderRadius: '8px',
    background: 'var(--bg)',
    color: 'var(--text-h)',
    outline: 'none',
    transition: 'border-color 0.15s',
    fontFamily: 'var(--sans)',
  } as React.CSSProperties,
  inputError: {
    borderColor: '#ef4444',
  } as React.CSSProperties,
  errorText: {
    fontSize: '13px',
    color: '#ef4444',
  } as React.CSSProperties,
  button: {
    marginTop: '4px',
    padding: '11px',
    fontSize: '15px',
    fontWeight: 500,
    background: 'var(--accent)',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontFamily: 'var(--sans)',
  } as React.CSSProperties,
  footer: {
    marginTop: '24px',
    fontSize: '14px',
    color: 'var(--text)',
    textAlign: 'center',
  } as React.CSSProperties,
  link: {
    color: 'var(--accent)',
    textDecoration: 'none',
    fontWeight: 500,
  } as React.CSSProperties,
}

export default RegisterPage
