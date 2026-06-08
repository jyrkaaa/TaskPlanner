import { PermissionsDto } from '../models/dto/PermissionsDto'

const PERM_KEY = 'enc_permissions'

async function buildKey(refreshToken: string): Promise<CryptoKey> {
  const raw = await crypto.subtle.digest(
    'SHA-256',
    new TextEncoder().encode(refreshToken)
  )
  return crypto.subtle.importKey('raw', raw, { name: 'AES-GCM' }, false, [
    'encrypt',
    'decrypt',
  ])
}

const toB64 = (arr: ArrayBuffer | Uint8Array): string =>
  btoa(String.fromCharCode(...new Uint8Array(arr)))

const fromB64 = (s: string): Uint8Array<ArrayBuffer> => {
  const binary = atob(s)
  const result = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i++) result[i] = binary.charCodeAt(i)
  return result
}

export async function savePermissions(permissions: PermissionsDto[]): Promise<void> {
  const refreshToken = localStorage.getItem('refreshToken')
  if (!refreshToken) return

  const key = await buildKey(refreshToken)
  const iv = crypto.getRandomValues(new Uint8Array(12))
  const cipher = await crypto.subtle.encrypt(
    { name: 'AES-GCM', iv },
    key,
    new TextEncoder().encode(JSON.stringify(permissions))
  )
  localStorage.setItem(
    PERM_KEY,
    JSON.stringify({ iv: toB64(iv), data: toB64(cipher) })
  )
}

export async function loadPermissions(): Promise<PermissionsDto[] | null> {
  const raw = localStorage.getItem(PERM_KEY)
  const refreshToken = localStorage.getItem('refreshToken')
  if (!raw || !refreshToken) return null

  try {
    const { iv, data } = JSON.parse(raw) as { iv: string; data: string }
    const key = await buildKey(refreshToken)
    const decrypted = await crypto.subtle.decrypt(
      { name: 'AES-GCM', iv: fromB64(iv) },
      key,
      fromB64(data)
    )
    return JSON.parse(new TextDecoder().decode(decrypted)) as PermissionsDto[]
  } catch {
    return null
  }
}

export function clearPermissions(): void {
  localStorage.removeItem(PERM_KEY)
}
