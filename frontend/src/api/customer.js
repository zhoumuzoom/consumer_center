import request from '../utils/request'

export const getCustomerList = (params) => request.get('/customers', { params })
export const getCustomerDetail = (id) => request.get(`/customers/${id}`)
export const createCustomer = (data) => request.post('/customers', data)
export const updateCustomer = (id, data) => request.put(`/customers/${id}`, data)
export const deleteCustomer = (id) => request.delete(`/customers/${id}`)
export const updateCustomerStatus = (id, data) => request.put(`/customers/${id}/status`, data)
export const getStatistics = () => request.get('/customer/overview/statistics')
export const getDrillDown = (params) => request.get('/customer/overview/drill-down', { params })
