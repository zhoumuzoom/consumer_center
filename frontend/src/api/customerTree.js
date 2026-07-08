import request from '../utils/request'

export const getCustomerTree = (params) => request.get('/customer-tree', { params })
export const filterCustomerTree = (params) => request.get('/customer-tree/filter', { params })
export const getNodeStatistics = (nodeId) => request.get(`/customer-tree/${nodeId}/statistics`)
export const createTreeNode = (data) => request.post('/customer-tree/nodes', data)
export const updateTreeNode = (id, data) => request.put(`/customer-tree/nodes/${id}`, data)
export const deleteTreeNode = (id) => request.delete(`/customer-tree/nodes/${id}`)
export const adjustHierarchy = (id, data) => request.put(`/customer-tree/nodes/${id}/hierarchy`, data)
