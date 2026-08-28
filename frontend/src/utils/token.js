const ACCESS_TOKEN_KEY = 'cmi_access_token'
const REFRESH_TOKEN_KEY = 'cmi_refresh_token'
const LAST_USERNAME_KEY = 'cmi_last_username'

export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY) || ''
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY) || ''
}

export function setTokens(accessToken, refreshToken) {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken || '')
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken || '')
}

export function clearTokens() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}

export function getLastUsername() {
  return localStorage.getItem(LAST_USERNAME_KEY) || ''
}

export function setLastUsername(username) {
  localStorage.setItem(LAST_USERNAME_KEY, username || '')
}
