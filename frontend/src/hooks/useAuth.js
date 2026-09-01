import { useCallback, useState } from 'react'
import { getUserInfo, login as loginApi, logout as logoutApi } from '../api/auth'
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from '../utils/token'

let cachedUser = null
export function useAuth() {
  const [, refresh] = useState(0)
  const accessToken = getAccessToken()
  const fetchUserInfo = useCallback(async () => {
    const res = await getUserInfo()
    cachedUser = res.data
    refresh((value) => value + 1)
    return cachedUser
  }, [])
  const login = useCallback(async (username, password) => {
    const res = await loginApi({ username, password })
    setTokens(res.data.accessToken, res.data.refreshToken)
    cachedUser = null
    refresh((value) => value + 1)
    return res.data
  }, [])
  const logout = useCallback(async () => {
    try { await logoutApi(getRefreshToken()) } catch { /* session cleanup remains local */ }
    clearTokens(); cachedUser = null; refresh((value) => value + 1)
  }, [])
  return { isLoggedIn: Boolean(accessToken), userInfo: cachedUser, displayName: cachedUser?.realName || cachedUser?.username || '', fetchUserInfo, login, logout }
}
