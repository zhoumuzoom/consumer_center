import request from '../utils/request'

export const getReportOverview = () => request.get('/group-report/overview')
export const getAbnormalData = (params) => request.get('/group-report/abnormal', { params })
export const manualReport = (data) => request.post('/group-report/manual', data)
export const getScheduleConfig = () => request.get('/group-report/schedule/config')
export const updateScheduleConfig = (data) => request.put('/group-report/schedule/config', data)
export const getReportRecords = (params) => request.get('/group-report/records', { params })
export const getGroup139Status = () => request.get('/group-report/139-system/status')
