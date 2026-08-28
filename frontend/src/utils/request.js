import axios from 'axios'
import { message } from 'ant-design-vue'
import { getAccessToken, getRefreshToken, setTokens, clearTokens } from './token'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截：携带accessToken
request.interceptors.request.use(config => {
  const token = getAccessToken()
  if (token) {
    config.headers.token = token
  }
  return config
})

// 单飞：并发的4003/4004响应只触发一次刷新
let refreshPromise = null

function redirectToLogin() {
  clearTokens()
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

// 用裸axios调用刷新接口，避免被本实例的拦截器递归处理
async function refreshTokens() {
  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    throw new Error('NO_REFRESH_TOKEN')
  }
  const response = await axios.post('/api/auth/refresh', null, {
    headers: { 'refresh-token': refreshToken },
    timeout: 30000
  })
  const body = response.data
  if (!body || body.code !== 200) {
    throw new Error(body?.message || '刷新令牌失败')
  }
  setTokens(body.data.accessToken, body.data.refreshToken)
  return body.data
}

function setRequestToken(config, token) {
  config.headers = config.headers || {}
  if (typeof config.headers.set === 'function') {
    config.headers.set('token', token)
  } else {
    config.headers.token = token
  }
}

// 响应拦截：业务码处理 + 令牌过期自动刷新重试
request.interceptors.response.use(
  async response => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    const config = response.config
    // accessToken过期(4003)或无效(4004)：尝试用refreshToken换新令牌并重试一次
    if ((res.code === 4003 || res.code === 4004) && !config._retried) {
      if (!getRefreshToken()) {
        redirectToLogin()
        return Promise.reject(new Error(res.message || '登录已失效'))
      }
      config._retried = true
      try {
        refreshPromise = refreshPromise || refreshTokens().finally(() => {
          refreshPromise = null
        })
        await refreshPromise
        setRequestToken(config, getAccessToken())
        return request(config)
      } catch {
        redirectToLogin()
        return Promise.reject(new Error(res.message || '登录已失效'))
      }
    }
    if (!config.skipErrorToast) {
      message.error(res.message || '请求失败')
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    if (!error.config || !error.config.skipErrorToast) {
      message.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
