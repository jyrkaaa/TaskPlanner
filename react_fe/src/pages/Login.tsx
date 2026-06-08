import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { AuthRequest } from '../models/dto/AuthRequest'
import { useAlert } from '../context/AlertContext';
import ThemeToggleButton from '../components/ThemeToggleButton';

interface FormErrors {
  email?: string;
  password?: string;
}

function validate(email: string, password: string): FormErrors {
  const errors: FormErrors = {};
  if (!email) {
    errors.email = 'Email is required';
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    errors.email = 'Enter a valid email address';
  }
  if (!password) {
    errors.password = 'Password is required';
  }
  return errors;
}

function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { error } = useAlert();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<FormErrors>({});
  const [touched, setTouched] = useState({ email: false, password: false });

  function handleBlur(field: keyof typeof touched) {
    setTouched((prev) => ({ ...prev, [field]: true }));
    setErrors(validate(email, password));
  }

  function handleSubmit(e: { preventDefault(): void }) {
    e.preventDefault();
    const allTouched = { email: true, password: true };
    setTouched(allTouched);
    const errs = validate(email, password);
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;
    
    const request: AuthRequest = { email, password };
    login(request).catch(() => {
      error('Login failed. Please check your credentials and try again.');
    }).then(() => {
      navigate('/');
    });
  }

  const displayErrors: FormErrors = {
    email: touched.email ? errors.email : undefined,
    password: touched.password ? errors.password : undefined,
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2 style={styles.title}>Welcome back</h2>
          <ThemeToggleButton />
        </div>
        <p style={styles.subtitle}>Sign in to your account</p>

        <form onSubmit={handleSubmit} noValidate style={styles.form}>
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
              style={{
                ...styles.input,
                ...(displayErrors.email ? styles.inputError : {}),
              }}
              autoComplete="email"
            />
            {displayErrors.email && <span style={styles.errorText}>{displayErrors.email}</span>}
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
              style={{
                ...styles.input,
                ...(displayErrors.password ? styles.inputError : {}),
              }}
              autoComplete="current-password"
            />
            {displayErrors.password && <span style={styles.errorText}>{displayErrors.password}</span>}
          </div>

          <button type="submit" style={styles.button}>
            Sign in
          </button>
        </form>

        <p style={styles.footer}>
          Don't have an account?{' '}
          <Link to="/register" style={styles.link}>
            Create one
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

export default LoginPage
