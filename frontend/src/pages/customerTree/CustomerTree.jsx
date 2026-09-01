import { useCallback, useEffect, useRef, useState } from 'react'
import { Button, Card, Col, Descriptions, Empty, Form, Input, InputNumber, Modal, Row, Select, Space, Statistic, Tag, message } from 'antd'
import PageHeader from '../../components/PageHeader'
import G6TreeCanvas from '../../components/G6TreeCanvas'
import { useLocale } from '../../locales'
import { createTreeNode, deleteTreeNode, getCustomerTree, getNodeStatistics, updateTreeNode } from '../../api/customerTree'

function findNode(nodes, id) {
  for (const node of nodes) {
    if (String(node.id) === String(id)) return node
    if (node.children) {
      const child = findNode(node.children, id)
      if (child) return child
    }
  }
  return null
}

export default function CustomerTree() {
  const { t } = useLocale()
  const [tree, setTree] = useState([])
  const [selected, setSelected] = useState(null)
  const [statistics, setStatistics] = useState(null)
  const [loadingStatistics, setLoadingStatistics] = useState(false)
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form] = Form.useForm()
  const statisticsRequestRef = useRef(0)
  const getNodeLabel = useCallback((node) => node.nodeName, [])
  const getNodeCaption = useCallback((node) => `${t('customerTree.customerCount')} ${node.customerCount || 0}`, [t])

  const load = async () => {
    try {
      const res = await getCustomerTree()
      const nextTree = res.data || []
      setTree(nextTree)
      if (selected) {
        const nextSelected = findNode(nextTree, selected.id)
        setSelected(nextSelected)
        if (!nextSelected) setStatistics(null)
      }
    } catch {
      // 请求错误已由统一请求拦截器提示
    }
  }

  // biome-ignore lint/correctness/useExhaustiveDependencies: load is intentionally called once on mount
  useEffect(() => {
    void load()
  }, [])

  const selectNode = async (node) => {
    const requestId = ++statisticsRequestRef.current
    setSelected(node)
    setStatistics(null)
    setLoadingStatistics(true)
    try {
      const res = await getNodeStatistics(node.id)
      if (requestId === statisticsRequestRef.current) setStatistics(res.data || null)
    } catch {
      if (requestId === statisticsRequestRef.current) setStatistics(null)
    } finally {
      if (requestId === statisticsRequestRef.current) setLoadingStatistics(false)
    }
  }

  const save = async () => {
    const values = await form.validateFields()
    if (editing) await updateTreeNode(editing.id, values)
    else await createTreeNode(values)
    message.success(t('customerTree.saveSuccess'))
    setOpen(false)
    await load()
  }

  const edit = (node) => {
    setEditing(node)
    form.setFieldsValue(node)
    setOpen(true)
  }

  const remove = (node) => Modal.confirm({
    title: t('customerTree.deleteConfirm'),
    okText: t('common.confirm'),
    cancelText: t('common.cancel'),
    onOk: async () => {
      await deleteTreeNode(node.id)
      message.success(t('common.deleteSuccess'))
      if (String(selected?.id) === String(node.id)) {
        setSelected(null)
        setStatistics(null)
      }
      await load()
    }
  })

  const openCreate = () => {
    setEditing(null)
    form.resetFields()
    form.setFieldsValue({ parentId: 0 })
    setOpen(true)
  }

  return (
    <>
      <PageHeader
        eyebrow="CUSTOMER TREE"
        title={t('menu.customerTree')}
        description={t('customerTree.canvasHint')}
        actions={<Button type="primary" onClick={openCreate}>{t('customerTree.addNode')}</Button>}
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
                title={`${t('customerTree.nodeDetail')} · ${selected.nodeName}`}
                extra={(
                  <Space size={4}>
                    <Button type="link" size="small" onClick={() => edit(selected)}>{t('common.edit')}</Button>
                    <Button type="link" danger size="small" onClick={() => remove(selected)}>{t('common.delete')}</Button>
                  </Space>
                )}
              >
                <Descriptions column={1} size="small">
                  <Descriptions.Item label={t('customerTree.nodeName')}>{selected.nodeName}</Descriptions.Item>
                  <Descriptions.Item label={t('customerTree.nodeType')}><Tag color="blue">{selected.nodeType}</Tag></Descriptions.Item>
                  <Descriptions.Item label={t('customerTree.level')}>{selected.level}</Descriptions.Item>
                  <Descriptions.Item label={t('customerTree.managerName')}>{selected.managerName || '-'}</Descriptions.Item>
                  <Descriptions.Item label={t('customerTree.responsibility')}>{selected.responsibility || '-'}</Descriptions.Item>
                </Descriptions>
                <div className="tree-stat-grid">
                  <Statistic title={t('customerTree.customerCount')} value={statistics?.customerCount ?? selected.customerCount ?? 0} loading={loadingStatistics} />
                  <Statistic title={t('customerTree.childNodeCount')} value={statistics?.childNodeCount ?? selected.children?.length ?? 0} loading={loadingStatistics} />
                  <Statistic title={t('customerTree.managementRate')} value={statistics?.managementRate ?? 0} suffix="%" loading={loadingStatistics} />
                </div>
              </Card>
            ) : (
              <div className="tree-detail-empty"><Empty description={t('customerTree.selectNodeTip')} /></div>
            )}
          </Col>
        </Row>
      </Card>
      <Modal open={open} title={editing ? t('customerTree.editNode') : t('customerTree.addNode')} onCancel={() => setOpen(false)} onOk={save}>
        <Form form={form} layout="vertical">
          <Form.Item name="nodeName" label={t('customerTree.nodeName')} rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="parentId" label={t('customerTree.parentId')}><InputNumber min={0} style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="nodeType" label={t('customerTree.nodeType')}>
            <Select options={[['集团', 'typeGroup'], ['区域', 'typeRegion'], ['部门', 'typeDepartment']].map(([value, key]) => ({ value, label: t(`customerTree.${key}`) }))} />
          </Form.Item>
          <Form.Item name="managerName" label={t('customerTree.managerName')}><Input /></Form.Item>
          <Form.Item name="responsibility" label={t('customerTree.responsibility')}><Input.TextArea rows={3} /></Form.Item>
        </Form>
      </Modal>
    </>
  )
}
