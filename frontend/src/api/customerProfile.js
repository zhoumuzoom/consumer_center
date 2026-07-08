import request from '../utils/request'

// 画像
export const getProfile = (customerId) => request.get(`/customer-profiles/${customerId}`)

// 分支
export const getBranchTree = (customerId) => request.get(`/customer-profiles/${customerId}/branches`)
export const createBranch = (customerId, data) => request.post(`/customer-profiles/${customerId}/branches`, data)
export const updateBranch = (customerId, branchId, data) => request.put(`/customer-profiles/${customerId}/branches/${branchId}`, data)
export const deleteBranch = (customerId, branchId) => request.delete(`/customer-profiles/${customerId}/branches/${branchId}`)
export const branchAnalysis = (customerId, branchId) => request.get(`/customer-profiles/${customerId}/branches/${branchId}/analysis`)

// 政策动态
export const listPolicies = (customerId) => request.get(`/customer-profiles/${customerId}/policies`)
export const createPolicy = (customerId, data) => request.post(`/customer-profiles/${customerId}/policies`, data)
export const deletePolicy = (customerId, policyId) => request.delete(`/customer-profiles/${customerId}/policies/${policyId}`)

// 历史订单/项目
export const listOrders = (customerId) => request.get(`/customer-profiles/${customerId}/orders`)
export const createOrder = (customerId, data) => request.post(`/customer-profiles/${customerId}/orders`, data)
export const deleteOrder = (customerId, orderId) => request.delete(`/customer-profiles/${customerId}/orders/${orderId}`)

// 产品与方案
export const listProducts = (customerId) => request.get(`/customer-profiles/${customerId}/products`)
export const createProduct = (customerId, data) => request.post(`/customer-profiles/${customerId}/products`, data)
export const deleteProduct = (customerId, productId) => request.delete(`/customer-profiles/${customerId}/products/${productId}`)

// 任务
export const createTask = (customerId, data) => request.post(`/customer-profiles/${customerId}/tasks`, data)

// AI 洞察
export const generateAIInsight = (customerId) => request.post(`/customer-profiles/${customerId}/ai-insight`)
