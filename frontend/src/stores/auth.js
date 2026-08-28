import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, getUserInfo as getUserInfoApi } from '../api/auth'
import { getAccessToken, getRefreshToken, setTokens, clearTokens } from '../utils/token'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: getAccessToken(),
    userInfo: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.accessToken,
    displayName: (state) => state.userInfo?.realName || state.userInfo?.username || ''
  },
  actions: {
    async login(username, password) {
      const res = await loginApi({ username, password })
      setTokens(res.data.accessToken, res.data.refreshToken)
      this.accessToken = res.data.accessToken
      return res.data
    },
    async fetchUserInfo() {
      const res = await getUserInfoApi()
      this.userInfo = res.data
      return res.data
    },
    async logout() {
      try {
        await logoutApi(getRefreshToken())
      } catch {
        // 令牌失效时静默忽略，本地会话照常清除
      }
      this.clearSession()
    },
    clearSession() {
      clearTokens()
      this.accessToken = ''
      this.userInfo = null
    }
  }
})
