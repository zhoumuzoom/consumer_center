import React from 'react'
import { CheckOutlined, DownOutlined, GlobalOutlined } from '@ant-design/icons'
import { Dropdown, Space } from 'antd'
import { useLocale } from '../locales'

export default function LangSwitcher({ dark = false, showLabel = false }) {
  const { locale, setLocale, t } = useLocale()
  const isZh = locale === 'zh-CN'
  const items = [
    { key: 'zh-CN', label: <Space>{t('lang.zhCN')}{isZh && <CheckOutlined />}</Space> },
    { key: 'en-US', label: <Space>{t('lang.enUS')}{!isZh && <CheckOutlined />}</Space> }
  ]
  return (
    <Dropdown menu={{ items, onClick: ({ key }) => setLocale(key) }} placement="bottomLeft">
      <span className={`lang-switcher ${dark ? 'dark' : ''}`} title={t('lang.switch')}>
        <GlobalOutlined />
        {showLabel && <><span>{isZh ? t('lang.zhCN') : t('lang.enUS')}</span><DownOutlined /></>}
      </span>
    </Dropdown>
  )
}
