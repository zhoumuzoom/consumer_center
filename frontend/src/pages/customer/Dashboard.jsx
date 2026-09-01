import React, { useEffect, useState } from 'react'
import { Card, Col, Row, Spin, Statistic, Tag } from 'antd'
import PageHeader from '../../components/PageHeader'
import { getStatistics } from '../../api/customer'
import { useLocale } from '../../locales'

export default function Dashboard() {
  const { t } = useLocale(); const [stats, setStats] = useState({}); const [loading, setLoading] = useState(true)
  useEffect(() => { getStatistics().then((res) => setStats(res.data || {})).finally(() => setLoading(false)) }, [])
  return <Spin spinning={loading}><PageHeader eyebrow="OVERVIEW" title={t('menu.dashboard')} description={t('dashboard.desc')} />
    <Row gutter={[20, 20]}>
      {[['totalCustomers', 'totalCustomersDesc', 'stat-primary'], ['managedCustomers', 'managedCustomersDesc', 'stat-success'], ['managementRate', 'managementRateDesc', 'stat-warning']].map(([key, desc, cls]) => <Col xs={24} md={8} key={key}><Card className="stat-card"><div>{t(`dashboard.${key}`)}</div><div className={`stat-value ${cls}`}>{stats[key] || 0}{key === 'managementRate' ? '%' : ''}</div><div className="stat-desc">{t(`dashboard.${desc}`)}</div></Card></Col>)}
      <Col xs={24} md={12}><Card title={t('dashboard.industryDistribution')}>{(stats.industryDistribution || []).map((item) => <div className="dist-row" key={item.name}><span>{item.name}</span><Tag color="blue">{t('dashboard.companyUnit', { n: item.count })}</Tag></div>)}</Card></Col>
      <Col xs={24} md={12}><Card title={t('dashboard.regionDistribution')}>{(stats.regionDistribution || []).map((item) => <div className="dist-row" key={item.name}><span>{item.name}</span><Tag color="green">{t('dashboard.companyUnit', { n: item.count })}</Tag></div>)}</Card></Col>
    </Row>
  </Spin>
}
