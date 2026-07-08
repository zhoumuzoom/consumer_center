import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../views/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/customer/Dashboard.vue'), meta: { title: '客户规模总览' } },
      { path: 'customers', name: 'CustomerList', component: () => import('../views/customer/CustomerList.vue'), meta: { title: '客户列表' } },
      { path: 'customers/:id', name: 'CustomerDetail', component: () => import('../views/customer/CustomerDetail.vue'), meta: { title: '客户详情' } },
      { path: 'customer-tree', name: 'CustomerTree', component: () => import('../views/customerTree/CustomerTree.vue'), meta: { title: '客户树管理' } },
      { path: 'industry-tree', name: 'IndustryTree', component: () => import('../views/industryTree/IndustryTree.vue'), meta: { title: '行业树管理' } },
      { path: 'decision-makers', name: 'DecisionMakers', component: () => import('../views/decisionMaker/DecisionMakerList.vue'), meta: { title: '决策关键人' } },
      { path: 'decision-chains', name: 'DecisionChains', component: () => import('../views/decisionMaker/DecisionChainList.vue'), meta: { title: '决策链管理' } },
      { path: 'profile/:id', name: 'CustomerProfile', component: () => import('../views/customerProfile/ProfileDashboard.vue'), meta: { title: '客户画像' } },
      { path: 'group-report', name: 'GroupReport', component: () => import('../views/groupReport/ReportDashboard.vue'), meta: { title: '集团数据上报' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
