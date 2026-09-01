import React, { useEffect, useState } from 'react'
import { Alert, Button, Form, Input } from 'antd'
import { LockOutlined, UserOutlined } from '@ant-design/icons'
import { useLocation, useNavigate } from '@umijs/max'
import LangSwitcher from '../../components/LangSwitcher'
import { useAuth } from '../../hooks/useAuth'
import { useLocale } from '../../locales'
import { getLastUsername, setLastUsername } from '../../utils/token'

const safeRedirect = (target) => typeof target === 'string' && target.startsWith('/') && !target.startsWith('//') ? target : '/'
export default function Login() {
  const { t } = useLocale(); const auth = useAuth(); const navigate = useNavigate(); const location = useLocation(); const [loading, setLoading] = useState(false); const [error, setError] = useState(''); const [form] = Form.useForm()
  useEffect(() => { if (auth.isLoggedIn) navigate('/', { replace: true }); form.setFieldValue('username', getLastUsername()) }, [auth.isLoggedIn])
  const submit = async (values) => { setLoading(true); setError(''); try { const username = values.username.trim(); await auth.login(username, values.password); setLastUsername(username); await auth.fetchUserInfo().catch(() => null); navigate(safeRedirect(new URLSearchParams(location.search).get('redirect'))) } catch (e) { setError(e?.message || t('login.failDefault')) } finally { setLoading(false) } }
  return <div className="login-page"><section className="brand-panel"><div className="brand-top"><div className="brand-logo">C</div><span className="brand-name">{t('app.name')}</span><div className="brand-lang"><LangSwitcher dark /></div></div><div className="brand-center"><div className="brand-eyebrow">CMI · KEY ACCOUNT OPERATION</div><h1 className="brand-headline">{t('app.headline1')}<br />{t('app.headline2')}</h1><p className="brand-sub">{t('app.brandSub')}</p><div className="constellation" aria-hidden="true" /></div><div className="brand-foot">{t('app.footNote')}</div></section><section className="form-panel"><div className="form-inner"><h2 className="form-title">{t('login.title')}</h2><p className="form-desc">{t('login.desc')}</p>{error && <Alert showIcon type="error" message={error} style={{ marginBottom: 20 }} />}<Form form={form} layout="vertical" onFinish={submit}><Form.Item name="username" label={t('login.username')} rules={[{ required: true, message: t('login.usernameRequired') }]}><Input size="large" prefix={<UserOutlined />} placeholder={t('login.usernamePlaceholder')} autoComplete="username" /></Form.Item><Form.Item name="password" label={t('login.password')} rules={[{ required: true, message: t('login.passwordRequired') }]}><Input.Password size="large" prefix={<LockOutlined />} placeholder={t('login.passwordPlaceholder')} autoComplete="current-password" /></Form.Item><Button type="primary" htmlType="submit" block size="large" loading={loading}>{loading ? t('login.loggingIn') : t('login.submit')}</Button></Form><p className="form-hint">{t('login.forgotHint')}</p></div></section></div>
}
