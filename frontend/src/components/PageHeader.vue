<template>
  <div class="page-header">
    <div class="page-header-left">
      <a-button v-if="back" type="text" class="back-btn" :aria-label="$t('common.back')" @click="goBack">
        <ArrowLeftOutlined />
      </a-button>
      <div class="page-header-text">
        <p class="page-eyebrow">{{ eyebrow }}</p>
        <h2 class="page-title">{{ title }}</h2>
        <p v-if="description" class="page-desc">{{ description }}</p>
      </div>
    </div>
    <div v-if="$slots.actions" class="page-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

defineProps({
  eyebrow: { type: String, required: true },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  back: { type: Boolean, default: false }
})

const router = useRouter()

function goBack() {
  router.back()
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 20px;
}

.page-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.back-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  color: #6b7280;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  flex-shrink: 0;
}

.back-btn:hover {
  color: #6366f1;
  border-color: #c7d2fe;
  background: #f5f6ff;
}

.page-eyebrow {
  font-family: ui-monospace, 'SF Mono', 'Cascadia Mono', Consolas, monospace;
  font-size: 10.5px;
  letter-spacing: 0.28em;
  color: #6366f1;
  opacity: 0.82;
  margin: 0 0 5px;
}

.page-title {
  font-size: 22px;
  line-height: 1.3;
  font-weight: 700;
  letter-spacing: 0.01em;
  color: #111827;
  margin: 0;
}

.page-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 6px 0 0;
}

.page-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
