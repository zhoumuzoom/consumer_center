import React, { createContext, useContext, useMemo, useState } from 'react'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import zhCN from './lang/zh-CN'
import enUS from './lang/en-US'

export const SUPPORTED_LOCALES = ['zh-CN', 'en-US']
const STORAGE_KEY = 'cmi-lang'
const messages = { 'zh-CN': zhCN, 'en-US': enUS }

function detectLocale() {
  const saved = typeof window !== 'undefined' ? localStorage.getItem(STORAGE_KEY) : null
  if (SUPPORTED_LOCALES.includes(saved)) return saved
  const language = typeof navigator !== 'undefined' ? navigator.language : 'zh-CN'
  return language.toLowerCase().startsWith('zh') ? 'zh-CN' : 'en-US'
}

function getValue(source, path) {
  return path.split('.').reduce((value, key) => value?.[key], source) ?? path
}

function translate(locale, path, params = {}) {
  let value = getValue(messages[locale], path)
  if (typeof value !== 'string') return value
  return value.replace(/\{(\w+)\}/g, (_, key) => params[key] ?? `{${key}}`)
}

const LocaleContext = createContext(null)

export function LocaleProvider({ children }) {
  const [locale, setLocaleState] = useState(detectLocale)
  const setLocale = (next) => {
    if (!SUPPORTED_LOCALES.includes(next)) return
    if (typeof window !== 'undefined') localStorage.setItem(STORAGE_KEY, next)
    if (typeof document !== 'undefined') document.documentElement.lang = next === 'zh-CN' ? 'zh-CN' : 'en'
    dayjs.locale(next === 'zh-CN' ? 'zh-cn' : 'en')
    setLocaleState(next)
  }
  const value = useMemo(() => ({ locale, setLocale, t: (path, params) => translate(locale, path, params) }), [locale])
  return <LocaleContext.Provider value={value}>{children}</LocaleContext.Provider>
}

export function useLocale() {
  return useContext(LocaleContext)
}

export function setLocale(locale) {
  if (typeof window !== 'undefined') localStorage.setItem(STORAGE_KEY, locale)
  if (typeof document !== 'undefined') document.documentElement.lang = locale === 'zh-CN' ? 'zh-CN' : 'en'
  dayjs.locale(locale === 'zh-CN' ? 'zh-cn' : 'en')
}
