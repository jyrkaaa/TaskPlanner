import axios from 'axios';
import { LocalStorageNames } from '../constants/LocalStorageNames';

let accessToken: string | null = null
let refreshPromise: Promise<void> | null = null

export function setTokens({ access, refresh }: { access: string; refresh?: string }) {
  accessToken = access
  if (refresh) localStorage.setItem(LocalStorageNames.REFRESH_TOKEN, refresh)
}

export function clearTokens() {
  accessToken = null
  localStorage.removeItem(LocalStorageNames.REFRESH_TOKEN)
}

const client = axios.create({ baseURL: '/api' })

client.interceptors.request.use((config) => {
  if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`
  return config
})

client.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config
    if (
      error.response?.status === 401 &&
      !original._retry &&
      localStorage.getItem(LocalStorageNames.REFRESH_TOKEN)
    ) {
      original._retry = true
      if (!refreshPromise) {
        refreshPromise = refreshAccessToken().finally(() => {
          refreshPromise = null
        })
      }
      await refreshPromise
      original.headers.Authorization = `Bearer ${accessToken}`
      return client(original)
    }
    return Promise.reject(error)
  }
)

async function refreshAccessToken(): Promise<void> {
  const refreshToken = localStorage.getItem(LocalStorageNames.REFRESH_TOKEN)
  if (!refreshToken) throw new Error('No refresh token')

  const { data } = await axios.post('/api/auth/refresh', { refreshToken })
  accessToken = data.token
  if (data.refreshToken) localStorage.setItem(LocalStorageNames.REFRESH_TOKEN, data.refreshToken)
}

export const api = {
  get: <T>(path: string) => client.get<T>(path).then((r) => r.data),
  post: <T>(path: string, body?: unknown) => client.post<T>(path, body).then((r) => r.data),
  put: <T>(path: string, body?: unknown) => client.put<T>(path, body).then((r) => r.data),
  delete: <T>(path: string) => client.delete<T>(path).then((r) => r.data),
}
