<template>
  <a-dropdown :trigger="['click']" placement="bottomLeft">
    <div class="lang-switcher" :class="{ dark }" :title="t('lang.switch')">
      <GlobalOutlined class="lang-icon" />
      <span v-if="showLabel" class="lang-label">{{ isZh ? t('lang.zhCN') : t('lang.enUS') }}</span>
      <DownOutlined v-if="showLabel" class="lang-arrow" />
    </div>
    <template #overlay>
      <a-menu @click="handleSelect">
        <a-menu-item key="zh-CN">
          <div class="lang-menu-item">
            <span>{{ t('lang.zhCN') }}</span>
            <CheckOutlined v-if="isZh" class="lang-check" />
          </div>
        </a-menu-item>
        <a-menu-item key="en-US">
          <div class="lang-menu-item">
            <span>{{ t('lang.enUS') }}</span>
            <CheckOutlined v-if="!isZh" class="lang-check" />
          </div>
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { setLocale } from '../locales'

defineProps({
  dark: { type: Boolean, default: false },
  showLabel: { type: Boolean, default: false },
})

const { t, locale } = useI18n()
const isZh = computed(() => locale.value === 'zh-CN')

function handleSelect({ key }) {
  if (key !== locale.value) {
    setLocale(key)
  }
}
</script>

<style scoped>
.lang-switcher {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px;
  border-radius: 8px;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.lang-switcher:hover {
  color: #6366f1;
  background: #f5f6ff;
}

.lang-switcher.dark {
  color: rgba(231, 235, 255, 0.7);
}

.lang-switcher.dark:hover {
  color: #ffffff;
  background: rgba(99, 102, 241, 0.24);
}

.lang-icon {
  font-size: 15px;
}

.lang-label {
  font-size: 13px;
  white-space: nowrap;
}

.lang-arrow {
  font-size: 10px;
  opacity: 0.7;
}

.lang-menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  min-width: 120px;
}

.lang-check {
  color: #6366f1;
  font-size: 12px;
}
</style>
