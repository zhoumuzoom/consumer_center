import { createI18n } from 'vue-i18n'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import zhCN from './lang/zh-CN'
import enUS from './lang/en-US'

export const SUPPORTED_LOCALES = ['zh-CN', 'en-US']
const STORAGE_KEY = 'cmi-lang'

function detectLocale() {
  const saved = localStorage.getItem(STORAGE_KEY)
  if (SUPPORTED_LOCALES.includes(saved)) return saved
  const nav = (navigator.language || 'zh-CN').toLowerCase()
  return nav.startsWith('zh') ? 'zh-CN' : 'en-US'
}

function applySideEffects(locale) {
  localStorage.setItem(STORAGE_KEY, locale)
  document.documentElement.lang = locale === 'zh-CN' ? 'zh-CN' : 'en'
  dayjs.locale(locale === 'zh-CN' ? 'zh-cn' : 'en')
}

const initialLocale = detectLocale()
applySideEffects(initialLocale)

const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale: initialLocale,
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS,
  },
})

export function setLocale(locale) {
  if (!SUPPORTED_LOCALES.includes(locale)) return
  i18n.global.locale.value = locale
  applySideEffects(locale)
}

export default i18n
