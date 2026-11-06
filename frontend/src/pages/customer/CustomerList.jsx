import React, { useRef, useState } from 'react'
import { DownloadOutlined } from '@ant-design/icons'
import { Button, Form, Input, Modal, Select, Space, Tag, message } from 'antd'
import { ProTable } from '@ant-design/pro-components'
import { useNavigate } from '@umijs/max'
import dayjs from 'dayjs'
import PageHeader from '../../components/PageHeader'
import { useLocale } from '../../locales'
import { createCustomer, deleteCustomer, getCustomerList, updateCustomer } from '../../api/customer'
import { downloadCsv } from '../../utils/exportCsv'

const EMPTY = { customerName: '', customerNameEn: '', industry: '', region: '', scale: '', contactPerson: '', contactPhone: '', remark: '' }
const EXPORT_PAGE_SIZE = 10
export default function CustomerList() {
  const { t } = useLocale(); const actionRef = useRef(); const navigate = useNavigate(); const [modal, setModal] = useState(false); const [editing, setEditing] = useState(null); const [exporting, setExporting] = useState(false); const [form] = Form.useForm(); const exportFiltersRef = useRef({})
  const columns = [
    { title: t('customer.keyword'), dataIndex: 'keyword', hideInTable: true, fieldProps: { placeholder: t('customer.keywordPlaceholder') } },
    { title: t('customer.code'), dataIndex: 'customerCode', width: 180, search: false },
    { title: t('common.customerName'), dataIndex: 'customerName', width: 200, render: (_, row) => <a onClick={() => navigate(`/customers/${row.id}`)}>{row.customerName}</a> },
    { title: t('common.industry'), dataIndex: 'industry' }, { title: t('common.region'), dataIndex: 'region' }, { title: t('common.scale'), dataIndex: 'scale' },
    { title: t('common.status'), dataIndex: 'status', valueType: 'select', valueEnum: { 0: { text: t('customer.statusUnmanaged') }, 1: { text: t('customer.statusManaged') }, 2: { text: t('customer.statusRemoved') } }, render: (_, row) => <Tag color={row.status === 1 ? 'success' : row.status === 2 ? 'error' : 'default'}>{row.statusDesc}</Tag> },
    { title: t('customer.managerName'), dataIndex: 'managerName', search: false },
    { title: t('common.action'), valueType: 'option', render: (_, row) => <Space><Button size="small" onClick={() => window.open(`/customers/${row.id}`, '_blank')}>{t('common.detail')}</Button><Button size="small" type="primary" onClick={() => openEdit(row)}>{t('common.edit')}</Button><Button size="small" danger onClick={() => confirmDelete(row)}>{t('common.delete')}</Button></Space> }
  ]
  const openEdit = (row) => { setEditing(row); form.setFieldsValue(row); setModal(true) }
  const openCreate = () => { setEditing(null); form.setFieldsValue(EMPTY); setModal(true) }
  const confirmDelete = (row) => Modal.confirm({ title: t('customer.deleteConfirm'), okText: t('common.confirm'), cancelText: t('common.cancel'), onOk: async () => { await deleteCustomer(row.id); message.success(t('common.deleteSuccess')); actionRef.current?.reload() } })
  const save = async () => { const values = await form.validateFields(); if (editing) { await updateCustomer(editing.id, values); message.success(t('common.updateSuccess')) } else { await createCustomer(values); message.success(t('common.addSuccess')) } setModal(false); actionRef.current?.reload() }
  // 导出当前筛选条件下的全部客户，文件在浏览器本地生成，不经过后端
  const handleExport = async () => {
    if (exporting) return
    setExporting(true)
    try {
      const filters = exportFiltersRef.current
      const first = await getCustomerList({ ...filters, page: 1, size: EXPORT_PAGE_SIZE })
      const total = first.data?.total || 0
      if (!total) { message.info(t('customer.exportEmpty')); return }
      const records = [...(first.data?.records || [])]
      const pages = Math.ceil(total / EXPORT_PAGE_SIZE)
      for (let page = 2; page <= pages; page++) {
        const res = await getCustomerList({ ...filters, page, size: EXPORT_PAGE_SIZE })
        records.push(...(res.data?.records || []))
      }
      downloadCsv(
        `${t('customer.exportFileName')}_${dayjs().format('YYYYMMDD_HHmmss')}.csv`,
        [t('customer.code'), t('common.customerName'), t('common.industry'), t('common.region'), t('common.scale'), t('common.status'), t('customer.managerName')],
        records.map((row) => [row.customerCode, row.customerName, row.industry, row.region, row.scale, row.statusDesc, row.managerName])
      )
      message.success(t('customer.exportSuccess', { count: records.length }))
    } catch {
      // 请求失败的提示由 request 响应拦截器统一处理
    } finally {
      setExporting(false)
    }
  }
  return <><PageHeader eyebrow="CUSTOMER" title={t('menu.customers')} actions={<><Button icon={<DownloadOutlined />} loading={exporting} onClick={handleExport}>{exporting ? t('customer.exporting') : t('customer.export')}</Button><Button type="primary" onClick={openCreate}>{t('customer.add')}</Button></>} />
    <ProTable rowKey="id" actionRef={actionRef} columns={columns} scroll={{ x: 1200 }} search={{ labelWidth: 'auto' }} request={async (params) => { const { current, pageSize, ...filters } = params; exportFiltersRef.current = filters; const res = await getCustomerList({ ...params, page: current, size: pageSize }); return { data: res.data?.records || [], total: res.data?.total || 0, success: true } }} pagination={{ showSizeChanger: true }} />
    <Modal open={modal} title={editing ? t('customer.editTitle') : t('customer.addTitle')} onCancel={() => setModal(false)} onOk={save}><Form form={form} layout="vertical"><Form.Item name="customerName" label={t('common.customerName')} rules={[{ required: true }]}><Input /></Form.Item><Form.Item name="customerNameEn" label={t('customer.nameEn')}><Input /></Form.Item><Form.Item name="industry" label={t('common.industry')}><Input /></Form.Item><Form.Item name="region" label={t('common.region')}><Input /></Form.Item><Form.Item name="scale" label={t('common.scale')}><Select options={[['大型', 'scaleLarge'], ['中型', 'scaleMedium'], ['小型', 'scaleSmall']].map(([value, key]) => ({ value, label: t(`customer.${key}`) }))} /></Form.Item><Form.Item name="contactPerson" label={t('customer.contactPerson')}><Input /></Form.Item><Form.Item name="contactPhone" label={t('customer.contactPhone')}><Input /></Form.Item><Form.Item name="remark" label={t('common.remark')}><Input.TextArea rows={3} /></Form.Item></Form></Modal>
  </>
}
