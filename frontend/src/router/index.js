import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../views/Layout.vue'
import { getAccessToken } from '../utils/token'
import i18n from '../locales'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: { titleKey: 'menu.login', public: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/customer/Dashboard.vue'), meta: { titleKey: 'menu.dashboard' } },
      { path: 'customers', name: 'CustomerList', component: () => import('../views/customer/CustomerList.vue'), meta: { titleKey: 'menu.customers' } },
      { path: 'customers/:id', name: 'CustomerDetail', component: () => import('../views/customer/CustomerDetail.vue'), meta: { titleKey: 'menu.customerDetail' } },
      { path: 'customer-tree', name: 'CustomerTree', component: () => import('../views/customerTree/CustomerTree.vue'), meta: { titleKey: 'menu.customerTree' } },
      { path: 'industry-tree', name: 'IndustryTree', component: () => import('../views/industryTree/IndustryTree.vue'), meta: { titleKey: 'menu.industryTree' } },
      { path: 'decision-makers', name: 'DecisionMakers', component: () => import('../views/decisionMaker/DecisionMakerList.vue'), meta: { titleKey: 'menu.decisionMakers' } },
      { path: 'decision-chains', name: 'DecisionChains', component: () => import('../views/decisionMaker/DecisionChainList.vue'), meta: { titleKey: 'menu.decisionChains' } },
      { path: 'profile/:id', name: 'CustomerProfile', component: () => import('../views/customerProfile/ProfileDashboard.vue'), meta: { titleKey: 'menu.customerProfile' } },
      { path: 'group-report', name: 'GroupReport', component: () => import('../views/groupReport/ReportDashboard.vue'), meta: { titleKey: 'menu.groupReport' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 页面标题随语言与路由联动
router.afterEach((to) => {
  const t = i18n.global.t
  const app = t('app.name')
  document.title = to.meta.titleKey ? `${t(to.meta.titleKey)} - ${app}` : app
})

// 全局守卫：未登录跳转登录页，已登录访问登录页跳转首页
router.beforeEach((to) => {
  const loggedIn = !!getAccessToken()
  if (to.meta.public) {
    if (loggedIn && to.path === '/login') {
      return '/'
    }
    return true
  }
  if (!loggedIn) {
    return { path: '/login', query: to.fullPath !== '/' ? { redirect: to.fullPath } : {} }
  }
  return true
})

export default router
