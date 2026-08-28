<template>
  <a-layout style="height: 100vh">
    <a-layout-sider
      v-model:collapsed="collapsed"
      :width="224"
      :collapsed-width="64"
      :trigger="null"
      class="app-sider"
    >
      <div class="logo-area">
        <div class="logo-icon">C</div>
        <span v-if="!collapsed" class="logo-text">CMI 要客运营平台</span>
      </div>
      <a-menu
        :selected-keys="[route.path]"
        mode="inline"
        theme="dark"
        class="app-menu"
        @click="handleMenuClick"
      >
        <a-menu-item key="/dashboard">
          <template #icon><DashboardOutlined /></template>
          <span>客户规模总览</span>
        </a-menu-item>
        <a-menu-item key="/customers">
          <template #icon><UserOutlined /></template>
          <span>客户列表</span>
        </a-menu-item>
        <a-menu-item key="/customer-tree">
          <template #icon><ApartmentOutlined /></template>
          <span>客户树管理</span>
        </a-menu-item>
        <a-menu-item key="/industry-tree">
          <template #icon><AppstoreOutlined /></template>
          <span>行业树管理</span>
        </a-menu-item>
        <a-menu-item key="/decision-makers">
          <template #icon><TeamOutlined /></template>
          <span>决策关键人</span>
        </a-menu-item>
        <a-menu-item key="/decision-chains">
          <template #icon><DeploymentUnitOutlined /></template>
          <span>决策链管理</span>
        </a-menu-item>
        <a-menu-item key="/group-report">
          <template #icon><CloudUploadOutlined /></template>
          <span>集团数据上报</span>
        </a-menu-item>
      </a-menu>
      <div v-if="!collapsed" class="sider-foot">CMI · KEY ACCOUNT</div>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="app-header">
        <div class="header-left">
          <span class="collapse-trigger" @click="collapsed = !collapsed">
            <MenuUnfoldOutlined v-if="collapsed" />
            <MenuFoldOutlined v-else />
          </span>
          <a-breadcrumb separator="/">
            <a-breadcrumb-item><router-link to="/">首页</router-link></a-breadcrumb-item>
            <a-breadcrumb-item>{{ route.meta.title || '页面' }}</a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <a-dropdown placement="bottomRight">
            <div class="user-chip">
              <UserOutlined style="font-size: 14px; color: #6b7280" />
              <span class="user-name">{{ authStore.displayName || '当前用户' }}</span>
              <DownOutlined style="font-size: 10px; color: #9ca3af" />
            </div>
            <template #overlay>
              <a-menu @click="handleUserMenu">
                <a-menu-item key="logout">
                  <LogoutOutlined />
                  <span style="margin-left: 6px">退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content class="app-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const collapsed = ref(false)

onMounted(() => {
  if (authStore.isLoggedIn && !authStore.userInfo) {
    authStore.fetchUserInfo().catch(() => null)
  }
})

const handleMenuClick = ({ key }) => {
  router.push(key)
}

async function handleUserMenu({ key }) {
  if (key === 'logout') {
    await authStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
/* ==================== 侧边栏：呼应登录页品牌面板 ==================== */

.app-sider {
  position: relative;
  background: linear-gradient(180deg, #0c1430 0%, #111c42 62%, #16244f 100%);
}

.logo-area {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #6366f1, #818cf8);
  color: #fff;
  font-weight: 700;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
}

.logo-text {
  color: #e7ebff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.02em;
  white-space: nowrap;
  overflow: hidden;
}

.app-menu {
  background: transparent;
  border-inline-end: none !important;
  padding: 10px 8px 44px;
}

.app-menu :deep(.ant-menu-item) {
  border-radius: 8px;
  margin: 4px 0;
  width: calc(100% - 0px);
  color: rgba(231, 235, 255, 0.62);
}

.app-menu :deep(.ant-menu-item:hover) {
  color: #ffffff;
  background: rgba(99, 102, 241, 0.14);
}

.app-menu :deep(.ant-menu-item-selected) {
  background: rgba(99, 102, 241, 0.24) !important;
  color: #c7d2fe !important;
}

.app-menu :deep(.ant-menu-item-selected .anticon) {
  color: #a5b4fc;
}

.sider-foot {
  position: absolute;
  bottom: 14px;
  left: 0;
  right: 0;
  text-align: center;
  font-family: ui-monospace, 'SF Mono', 'Cascadia Mono', Consolas, monospace;
  font-size: 10px;
  letter-spacing: 0.3em;
  color: rgba(231, 235, 255, 0.24);
  pointer-events: none;
}

/* ==================== 顶栏 ==================== */

.app-header {
  background: #ffffff;
  padding: 0 24px;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  line-height: 56px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-trigger {
  cursor: pointer;
  font-size: 18px;
  color: #6b7280;
  display: flex;
  align-items: center;
}

.collapse-trigger:hover {
  color: #6366f1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.user-chip:hover {
  background: #f5f6ff;
}

.user-name {
  color: #4b5563;
  font-size: 13px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ==================== 内容区 ==================== */

.app-content {
  background: #f7f8fc;
  padding: 24px;
  overflow: auto;
}
</style>
