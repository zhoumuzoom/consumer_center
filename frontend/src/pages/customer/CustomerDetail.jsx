import React, { useEffect, useState } from 'react'
import { Button, Card, Col, Descriptions, Row, Space, Tag } from 'antd'
import { useNavigate, useParams } from '@umijs/max'
import PageHeader from '../../components/PageHeader'
import { getCustomerDetail } from '../../api/customer'
import { useLocale } from '../../locales'

export default function CustomerDetail() {
  const { id } = useParams(); const { t } = useLocale(); const navigate = useNavigate(); const [detail, setDetail] = useState(null)
  useEffect(() => { getCustomerDetail(id).then((res) => setDetail(res.data)) }, [id])
  return <><PageHeader eyebrow="CUSTOMER" title={t('menu.customerDetail')} actions={detail && <Space><Button type="primary" onClick={() => navigate(`/profile/${detail.id}`)}>{t('customer.viewProfile')}</Button><Button onClick={() => navigate(-1)}>{t('common.back')}</Button></Space>} />
    <Card loading={!detail}>{detail && <><Descriptions column={{ xs: 1, sm: 2, md: 3 }}><Descriptions.Item label={t('customer.code')}>{detail.customerCode}</Descriptions.Item><Descriptions.Item label={t('common.customerName')}>{detail.customerName}</Descriptions.Item><Descriptions.Item label={t('customer.nameEn')}>{detail.customerNameEn}</Descriptions.Item><Descriptions.Item label={t('common.industry')}>{detail.industry}</Descriptions.Item><Descriptions.Item label={t('common.region')}>{detail.region}</Descriptions.Item><Descriptions.Item label={t('common.scale')}>{detail.scale}</Descriptions.Item><Descriptions.Item label={t('common.status')}><Tag color={detail.status === 1 ? 'success' : 'default'}>{detail.statusDesc}</Tag></Descriptions.Item><Descriptions.Item label={t('customer.managerName')}>{detail.managerName}</Descriptions.Item><Descriptions.Item label={t('customer.contactPerson')}>{detail.contactPerson}</Descriptions.Item><Descriptions.Item label={t('customer.contactPhone')}>{detail.contactPhone}</Descriptions.Item><Descriptions.Item label={t('customer.contactEmail')}>{detail.contactEmail}</Descriptions.Item><Descriptions.Item span={2} label={t('customer.address')}>{detail.address}</Descriptions.Item><Descriptions.Item span={3} label={t('common.remark')}>{detail.remark}</Descriptions.Item></Descriptions><Row gutter={20} style={{ marginTop: 20 }}>{[['branchCount', 'branchCount'], ['decisionMakerCount', 'decisionMakerCount'], ['interactionCount', 'interactionCount']].map(([key, label]) => <Col span={8} key={key}><Card size="small"><div className="stat-desc">{t(`customer.${label}`)}</div><div className="stat-value">{detail[key] || 0}</div></Card></Col>)}</Row></>}</Card>
  </>
}
