<template>
  <a-layout style="height: 100vh">
    <a-layout-sider
      v-model:collapsed="collapsed"
      :width="220"
      :collapsed-width="64"
      :trigger="null"
      style="background: #ffffff; border-right: 1px solid #e5e7eb"
    >
      <div class="logo-area">
        <div class="logo-icon">C</div>
        <span v-if="!collapsed" class="logo-text">CMI 要客运营平台</span>
      </div>
      <a-menu
        :selected-keys="[route.path]"
        mode="inline"
        theme="light"
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
          <UserOutlined style="font-size: 14px; color: #6b7280" />
          <span style="color: #6b7280; font-size: 13px">系统管理员</span>
        </div>
      </a-layout-header>
      <a-layout-content class="app-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const collapsed = ref(false)

const handleMenuClick = ({ key }) => {
  router.push(key)
}
</script>

<style scoped>
.logo-area {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  gap: 10px;
  border-bottom: 1px solid #e5e7eb;
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
}
.logo-text {
  color: #1f2937;
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
}
.app-header {
  background: #ffffff;
  padding: 0 24px;
  border-bottom: 1px solid #e5e7eb;
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
.app-content {
  background: #f5f7fa;
  padding: 24px;
  overflow: auto;
}
</style>
