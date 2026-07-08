import request from '../utils/request'

export const getIndustryTree = () => request.get('/industry-tree')
export const getNodeAggregation = (nodeId) => request.get(`/industry-tree/${nodeId}/aggregation`)
export const drillDown = (nodeId) => request.get(`/industry-tree/${nodeId}/drill-down`)
export const createIndustryNode = (data) => request.post('/industry-tree/nodes', data)
export const setCustomerTag = (customerId, data) => request.post(`/industry-tree/customers/${customerId}/tags`, data)
export const batchSetTags = (data) => request.post('/industry-tree/customers/tags/batch', data)
export const resourceAnalysis = (nodeId) => request.get(`/industry-tree/${nodeId}/resource-analysis`)
