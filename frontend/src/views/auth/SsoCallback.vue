<template>
  <div class="sso-callback">
    <a-spin v-if="loading" size="large" />
    <a-alert v-else type="error" show-icon :message="errorMessage" />
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '../../stores/auth'
import { ssoLogin } from '../../api/auth'
import { setTokens } from '../../utils/token'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()
const loading = ref(true)
const errorMessage = ref(t('login.ssoWaiting'))
let exchanged = false

const parentOrigin = import.meta.env.VITE_SSO_PARENT_ORIGIN || ''

function safeRedirect(target) {
  if (typeof target === 'string' && target.startsWith('/') && !target.startsWith('//')) {
    return target
  }
  return '/'
}

function isTrustedOrigin(origin) {
  if (parentOrigin) {
    return origin === parentOrigin
  }
  return origin === window.location.origin
}

function getTokenFromUrl() {
  const queryToken = route.query.access_token || route.query.token
  if (typeof queryToken === 'string' && queryToken) {
    return queryToken
  }
  const hash = window.location.hash.startsWith('#') ? window.location.hash.slice(1) : window.location.hash
  const hashToken = new URLSearchParams(hash).get('access_token') || new URLSearchParams(hash).get('token')
  if (hashToken) {
    window.history.replaceState({}, document.title, window.location.pathname + window.location.search)
  }
  return hashToken
}

async function exchangeToken(externalToken) {
  if (exchanged || !externalToken) {
    return
  }
  exchanged = true
  loading.value = true
  errorMessage.value = ''
  try {
    const res = await ssoLogin(externalToken)
    setTokens(res.data.accessToken, res.data.refreshToken)
    authStore.accessToken = res.data.accessToken
    await authStore.fetchUserInfo().catch(() => null)
    router.replace(safeRedirect(route.query.redirect))
  } catch (error) {
    exchanged = false
    loading.value = false
    errorMessage.value = error?.message || t('login.ssoFail')
  }
}

function handleMessage(event) {
  if (event.source !== window.parent || !isTrustedOrigin(event.origin)) {
    return
  }
  const payload = event.data || {}
  if (payload.type !== 'consumer-center-sso') {
    return
  }
  exchangeToken(payload.accessToken || payload.token || '')
}

onMounted(() => {
  window.addEventListener('message', handleMessage)
  const token = getTokenFromUrl()
  if (token) {
    exchangeToken(token)
  } else if (window.parent === window) {
    loading.value = false
    errorMessage.value = t('login.ssoNoToken')
  } else if (parentOrigin) {
    window.parent.postMessage({ type: 'consumer-center-sso-ready' }, parentOrigin)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('message', handleMessage)
})
</script>

<style scoped>
.sso-callback {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.sso-callback :deep(.ant-alert) {
  max-width: 520px;
}
</style>
