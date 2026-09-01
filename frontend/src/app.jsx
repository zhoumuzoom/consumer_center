import React from 'react'
import { App as AntdApp, ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import enUS from 'antd/locale/en_US'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { LocaleProvider, useLocale } from './locales'
import './style.css'

const queryClient = new QueryClient()

function AppShell({ container }) {
  const { locale } = useLocale()
  return (
    <ConfigProvider
      locale={locale === 'zh-CN' ? zhCN : enUS}
      theme={{
        cssVar: true,
        token: {
          colorPrimary: '#6366f1',
          borderRadius: 8,
          fontFamily: "'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif"
        }
      }}
    >
      <AntdApp>{container}</AntdApp>
    </ConfigProvider>
  )
}

export function rootContainer(container) {
  return (
    <QueryClientProvider client={queryClient}>
      <LocaleProvider>
        <AppShell container={container} />
      </LocaleProvider>
    </QueryClientProvider>
  )
}

export const qiankun = { slave: {} }
