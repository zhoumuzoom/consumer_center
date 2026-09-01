import React from 'react'
import { ArrowLeftOutlined } from '@ant-design/icons'
import { Button, Space } from 'antd'
import { useNavigate } from '@umijs/max'
import { useLocale } from '../locales'

export default function PageHeader({ eyebrow, title, description, back = false, actions }) {
  const navigate = useNavigate()
  const { t } = useLocale()
  return (
    <div className="page-header">
      <Space align="start" size={10}>
        {back && <Button aria-label={t('common.back')} icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)} />}
        <div>
          <div className="page-eyebrow">{eyebrow}</div>
          <h2 className="page-title">{title}</h2>
          {description && <div className="page-desc">{description}</div>}
        </div>
      </Space>
      {actions && <Space>{actions}</Space>}
    </div>
  )
}
