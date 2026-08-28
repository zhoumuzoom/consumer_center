import request from '../utils/request'

export const login = (data) => request.post('/auth/login', data, { skipErrorToast: true })

export const logout = (refreshToken) => request.post('/auth/logout', null, {
  headers: refreshToken ? { 'refresh-token': refreshToken } : {},
  skipErrorToast: true
})

export const getUserInfo = () => request.get('/auth/userinfo')
