import React, { useEffect, useState } from 'react'
import { Card, Col, Divider, Empty, Row, Statistic, Tag, Tree } from 'antd'
import PageHeader from '../../components/PageHeader'
import { useLocale } from '../../locales'
import { getIndustryTree, getNodeAggregation } from '../../api/industryTree'

export default function IndustryTree() {
  const { t } = useLocale(); const [tree, setTree] = useState([]); const [selected, setSelected] = useState(null); const [aggregation, setAggregation] = useState(null)
  useEffect(() => { getIndustryTree().then((res) => setTree(res.data || [])) }, [])
  const select = (_, info) => { setSelected(info.node); getNodeAggregation(info.node.id).then((res) => setAggregation(res.data)) }
  return <><PageHeader eyebrow="INDUSTRY TREE" title={t('menu.industryTree')} /><Card><Row gutter={20}><Col span={10}><Tree treeData={tree} fieldNames={{ key: 'id', title: 'nodeName', children: 'children' }} titleRender={(node) => <span>{node.nodeName} <Tag>{t('industryTree.companyUnit', { n: node.customerCount || 0 })}</Tag></span>} defaultExpandAll onSelect={select} /></Col><Col span={14}>{selected ? <Card title={`${selected.nodeName} - ${t('industryTree.aggregation')}`}><Row gutter={20}>{[['customerCount', 'customerCount'], ['managementRate', 'managementRate'], ['groupCount', 'groupCount']].map(([key, label]) => <Col span={8} key={key}><Statistic title={t(`industryTree.${label}`)} value={aggregation?.[key] || 0} suffix={key === 'managementRate' ? '%' : ''} /></Col>)}</Row><Divider /><Row gutter={20}>{[['strategicCount', 'strategicCount'], ['keyCount', 'keyCount']].map(([key, label]) => <Col span={12} key={key}><Statistic title={t(`industryTree.${label}`)} value={aggregation?.[key] || 0} /></Col>)}</Row></Card> : <Empty description={t('industryTree.selectNodeTip')} />}</Col></Row></Card></>
}
