import request from '../utils/request'

export const getDecisionChains = (params) => request.get('/decision-chains', { params })
export const getDecisionChainDetail = (id) => request.get(`/decision-chains/${id}`)
export const createDecisionMaker = (data) => request.post('/decision-makers', data)
export const updateDecisionMaker = (id, data) => request.put(`/decision-makers/${id}`, data)
export const deleteDecisionMaker = (id) => request.delete(`/decision-makers/${id}`)
export const createDecisionChain = (data) => request.post('/decision-chains', data)
export const updateDecisionChain = (id, data) => request.put(`/decision-chains/${id}`, data)
export const deleteDecisionChain = (id) => request.delete(`/decision-chains/${id}`)
export const archiveDecisionChain = (id) => request.post(`/decision-chains/${id}/archive`)
