import React, { useEffect, useState } from 'react'
import { Alert, Spin } from 'antd'
import { useLocation, useNavigate } from '@umijs/max'
import { ssoLogin } from '../../api/auth'
import { useAuth } from '../../hooks/useAuth'
import { useLocale } from '../../locales'
import { setTokens } from '../../utils/token'

const safeRedirect = (target) => typeof target === 'string' && target.startsWith('/') && !target.startsWith('//') ? target : '/'
export default function SsoCallback() {
  const { t } = useLocale(); const auth = useAuth(); const navigate = useNavigate(); const location = useLocation(); const [error, setError] = useState('');
  useEffect(() => { let active = true; let exchanged = false; const parentOrigin = typeof process !== 'undefined' ? process.env?.VITE_SSO_PARENT_ORIGIN || '' : ''; const params = new URLSearchParams(location.search); const exchange = (token) => { if (!token || exchanged) return; exchanged = true; ssoLogin(token).then(async (res) => { if (!active) return; setTokens(res.data.accessToken, res.data.refreshToken); await auth.fetchUserInfo().catch(() => null); navigate(safeRedirect(params.get('redirect')), { replace: true }) }).catch((e) => { exchanged = false; if (active) setError(e?.message || t('login.ssoFail')) }) }; const handleMessage = (event) => { if (event.source !== window.parent || (parentOrigin ? event.origin !== parentOrigin : event.origin !== window.location.origin)) return; const payload = event.data || {}; if (payload.type === 'consumer-center-sso') exchange(payload.accessToken || payload.token) }; window.addEventListener('message', handleMessage); const hash = new URLSearchParams(window.location.hash.replace(/^#/, '')); const token = params.get('access_token') || params.get('token') || hash.get('access_token') || hash.get('token'); if (token) exchange(token); else if (window.parent === window) setError(t('login.ssoNoToken')); else if (parentOrigin) window.parent.postMessage({ type: 'consumer-center-sso-ready' }, parentOrigin); return () => { active = false; window.removeEventListener('message', handleMessage) } }, [location.search])
  return <div className="sso-callback" style={{ minHeight: '100vh', display: 'grid', placeItems: 'center' }}>{error ? <Alert type="error" showIcon message={error} /> : <Spin size="large" tip={t('login.ssoWaiting')} />}</div>
}
