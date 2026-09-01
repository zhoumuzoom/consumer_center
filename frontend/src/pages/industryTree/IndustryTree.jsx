import { useCallback, useEffect, useRef, useState } from 'react'
import { Button, Card, Col, Divider, Empty, Row, Statistic, Tag, message } from 'antd'
import PageHeader from '../../components/PageHeader'
import G6TreeCanvas from '../../components/G6TreeCanvas'
import { useLocale } from '../../locales'
import { drillDown, getIndustryTree, getNodeAggregation } from '../../api/industryTree'

export default function IndustryTree() {
  const { t } = useLocale()
  const [tree, setTree] = useState([])
  const [fullTree, setFullTree] = useState([])
  const [selected, setSelected] = useState(null)
  const [aggregation, setAggregation] = useState(null)
  const [drilled, setDrilled] = useState(false)
  const aggregationRequestRef = useRef(0)
  const getNodeLabel = useCallback((node) => node.nodeName, [])
  const getNodeCaption = useCallback((node) => t('industryTree.companyUnit', { n: node.customerCount || 0 }), [t])

  useEffect(() => {
    const load = async () => {
      try {
        const res = await getIndustryTree()
        const nextTree = res.data || []
        setTree(nextTree)
        setFullTree(nextTree)
      } catch {
        // 请求错误已由统一请求拦截器提示
      }
    }
    void load()
  }, [])

  const selectNode = async (node) => {
    const requestId = ++aggregationRequestRef.current
    setSelected(node)
    setAggregation(null)
    try {
      const res = await getNodeAggregation(node.id)
      if (requestId === aggregationRequestRef.current) setAggregation(res.data || null)
    } catch {
      if (requestId === aggregationRequestRef.current) setAggregation(null)
    }
  }

  const handleDrillDown = async () => {
    if (!selected) return
    try {
      const res = await drillDown(selected.id)
      if (!res.data?.length) {
        message.info(t('industryTree.drillDownEmpty'))
        return
      }
      setTree(res.data)
      setSelected(null)
      setAggregation(null)
      setDrilled(true)
    } catch {
      // 请求错误已由统一请求拦截器提示
    }
  }

  const restoreTree = () => {
    setTree(fullTree)
    setSelected(null)
    setAggregation(null)
    setDrilled(false)
  }

  return (
    <>
      <PageHeader
        eyebrow="INDUSTRY TREE"
        title={t('menu.industryTree')}
        description={t('industryTree.canvasHint')}
        actions={drilled && <Button onClick={restoreTree}>{t('industryTree.backToTree')}</Button>}
      />
      <Card className="tree-workspace-card" styles={{ body: { padding: 0 } }}>
        <Row gutter={0}>
          <Col xs={24} lg={15} className="tree-canvas-column">
            <G6TreeCanvas
              treeData={tree}
              selectedId={selected?.id}
              onNodeClick={selectNode}
              getNodeLabel={getNodeLabel}
              getNodeCaption={getNodeCaption}
              emptyDescription={t('common.noData')}
            />
          </Col>
          <Col xs={24} lg={9} className="tree-detail-column">
            {selected ? (
              <Card
                variant="borderless"
                className="tree-detail-card"
                title={`${selected.nodeName} · ${t('industryTree.aggregation')}`}
                extra={<Button type="link" size="small" onClick={handleDrillDown}>{t('industryTree.drillDown')}</Button>}
              >
                <DescriptionsBlock node={selected} aggregation={aggregation} t={t} />
              </Card>
            ) : (
              <div className="tree-detail-empty"><Empty description={t('industryTree.selectNodeTip')} /></div>
            )}
          </Col>
        </Row>
      </Card>
    </>
  )
}

function DescriptionsBlock({ node, aggregation, t }) {
  return (
    <>
      <div className="tree-detail-meta">
        <span className="tree-detail-code">{node.nodeCode || '-'}</span>
        <Tag color="blue">{t('industryTree.companyUnit', { n: node.customerCount || 0 })}</Tag>
      </div>
      {node.description && <p className="tree-detail-description">{node.description}</p>}
      <Row gutter={[12, 20]}>
        {[
          ['customerCount', 'customerCount'],
          ['managementRate', 'managementRate'],
          ['groupCount', 'groupCount']
        ].map(([key, label]) => (
          <Col span={8} key={key}>
            <Statistic title={t(`industryTree.${label}`)} value={aggregation?.[key] ?? 0} suffix={key === 'managementRate' ? '%' : ''} />
          </Col>
        ))}
      </Row>
      <Divider />
      <Row gutter={[12, 20]}>
        {[
          ['strategicCount', 'strategicCount'],
          ['keyCount', 'keyCount']
        ].map(([key, label]) => (
          <Col span={12} key={key}>
            <Statistic title={t(`industryTree.${label}`)} value={aggregation?.[key] ?? 0} />
          </Col>
        ))}
      </Row>
    </>
  )
}
