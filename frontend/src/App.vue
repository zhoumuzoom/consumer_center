<template>
  <a-config-provider :theme="themeConfig">
    <router-view />
  </a-config-provider>
</template>

<script setup>
import { theme } from 'ant-design-vue'

const themeConfig = {
  algorithm: theme.defaultAlgorithm,
  token: {
    colorPrimary: '#6366f1',
    colorBgBase: '#ffffff',
    colorBgContainer: '#ffffff',
    colorBgElevated: '#ffffff',
    colorBorder: '#e5e7eb',
    colorBorderSecondary: '#f0f0f0',
    colorText: '#1f2937',
    colorTextSecondary: '#6b7280',
    colorTextTertiary: '#9ca3af',
    colorTextQuaternary: '#d1d5db',
    borderRadius: 6,
    colorLink: '#6366f1',
    colorLinkHover: '#818cf8',
    colorSuccess: '#10b981',
    colorWarning: '#f59e0b',
    colorError: '#ef4444',
    colorInfo: '#6366f1',
    fontFamily: "'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif",
  },
  components: {
    Table: {
      colorBgContainer: '#ffffff',
      headerBg: '#f9fafb',
      borderColor: '#e5e7eb',
      headerColor: '#6b7280',
      rowHoverBg: '#f9fafb',
    },
    Card: {
      colorBgContainer: '#ffffff',
      colorBorder: '#e5e7eb',
    },
    Menu: {
      itemBg: '#ffffff',
      subMenuItemBg: '#ffffff',
      itemSelectedBg: '#f0f5ff',
      itemSelectedColor: '#6366f1',
      itemHoverBg: '#f9fafb',
      itemSelectedActiveBarBg: '#6366f1',
    },
    Modal: {
      colorBgContainer: '#ffffff',
    },
    Tabs: {
      inkBarColor: '#6366f1',
      itemActiveColor: '#6366f1',
      itemHoverColor: '#818cf8',
      itemSelectedColor: '#6366f1',
    },
    Tree: {
      colorBgContainer: '#ffffff',
      nodeHoverBg: '#f9fafb',
      nodeSelectedBg: '#f0f5ff',
    },
  },
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body, #app {
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  background: #f5f7fa;
  color: #1f2937;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.15);
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: rgba(0,0,0,0.25);
}

/* 全局过渡 */
a, .ant-btn, .ant-card, .ant-table-row, .ant-menu-item {
  transition: all 0.2s ease;
}

/* 去掉 table bordered 的外边框 */
.ant-table-bordered .ant-table-container {
  border: none;
}
.ant-table-bordered .ant-table-cell {
  border-inline-end: none;
}

/* Ant Design Tag 微调 */
.ant-tag {
  border: none;
}
</style>
