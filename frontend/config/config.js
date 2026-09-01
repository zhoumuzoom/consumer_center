import { defineConfig } from '@umijs/max'

export default defineConfig({
  antd: {},
  dva: false,
  hash: true,
  npmClient: 'npm',
  locale: { antd: true, default: 'zh-CN', baseNavigator: true },
  routes: [
    { path: '/login', component: '@/pages/auth/Login', layout: false },
    { path: '/sso/callback', component: '@/pages/auth/SsoCallback', layout: false },
    {
      path: '/',
      component: '@/layouts/BasicLayout',
      routes: [
        { path: '/', redirect: '/dashboard' },
        { path: 'dashboard', component: '@/pages/customer/Dashboard' },
        { path: 'customers', component: '@/pages/customer/CustomerList' },
        { path: 'customers/:id', component: '@/pages/customer/CustomerDetail' },
        { path: 'customer-tree', component: '@/pages/customerTree/CustomerTree' },
        { path: 'industry-tree', component: '@/pages/industryTree/IndustryTree' },
        { path: 'decision-makers', component: '@/pages/decisionMaker/DecisionMakerList' },
        { path: 'decision-chains', component: '@/pages/decisionMaker/DecisionChainList' },
        { path: 'profile/:id', component: '@/pages/customerProfile/ProfileDashboard' },
        { path: 'group-report', component: '@/pages/groupReport/ReportDashboard' },
        { path: '*', redirect: '/dashboard' }
      ]
    }
  ],
  proxy: {
    '/api': { target: 'http://localhost:8080', changeOrigin: true }
  },
  tailwindcss: {}
})
