import { supabase } from '../supaBaseClient'

const API_BASE = (import.meta.env.VITE_API_BASE || '').replace(/\/$/, '')

export async function ensureBackendUser({ email, fullName = '' }) {
  if (!email) {
    throw new Error('Missing user email')
  }

  const usersResponse = await fetch(`${API_BASE}/users`)
  if (!usersResponse.ok) {
    throw new Error('Failed to fetch backend users')
  }

  const users = await usersResponse.json()
  const existing = users.find(
    (u) => u.email?.toLowerCase() === email.toLowerCase()
  )

  if (existing?.id) {
    if (fullName && existing.fullName !== fullName) {
      await fetch(`${API_BASE}/users/${existing.id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fullName }),
      })
    }

    return existing
  }

  const createResponse = await fetch(`${API_BASE}/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, fullName }),
  })

  if (!createResponse.ok) {
    const errorText = await createResponse.text()
    throw new Error(`Failed to create backend user: ${errorText}`)
  }

  return createResponse.json()
}

export async function ensureBackendUserFromSupabaseUser(supabaseUser) {
  if (!supabaseUser?.email) {
    throw new Error('Missing authenticated user email')
  }

  const fullName =
    supabaseUser.user_metadata?.full_name ||
    supabaseUser.user_metadata?.name ||
    ''

  return ensureBackendUser({
    email: supabaseUser.email,
    fullName,
  })
}

export async function getAuthenticatedBackendUser() {
  const { data, error } = await supabase.auth.getUser()

  if (error) {
    throw error
  }

  if (!data.user) {
    throw new Error('No authenticated user found')
  }

  return ensureBackendUserFromSupabaseUser(data.user)
}

export function getUserRequestHeaders(userId, includeJsonContentType = true) {
  const headers = {
    'X-User-Id': String(userId),
  }

  if (includeJsonContentType) {
    headers['Content-Type'] = 'application/json'
  }

  return headers
}
