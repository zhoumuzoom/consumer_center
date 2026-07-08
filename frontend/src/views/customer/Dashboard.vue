<template>
  <div>
    <a-row :gutter="20" style="margin-bottom: 20px">
      <a-col :span="8">
        <a-card class="stat-card">
          <div class="stat-label">客户总量</div>
          <div class="stat-value stat-primary">{{ stats.totalCustomers || 0 }}</div>
          <div class="stat-desc">要客总数</div>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card class="stat-card">
          <div class="stat-label">已看管客户</div>
          <div class="stat-value stat-success">{{ stats.managedCustomers || 0 }}</div>
          <div class="stat-desc">已分配客户经理</div>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card class="stat-card">
          <div class="stat-label">客户看管率</div>
          <div class="stat-value stat-warning">{{ stats.managementRate || 0 }}%</div>
          <div class="stat-desc">已看管/总量</div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="20">
      <a-col :span="12">
        <a-card>
          <template #title>行业分布</template>
          <div v-for="item in stats.industryDistribution" :key="item.name" class="dist-row">
            <span>{{ item.name }}</span>
            <a-tag color="rgba(99,102,241,0.15)" style="color: #818cf8">{{ item.count }} 家</a-tag>
          </div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card>
          <template #title>区域分布</template>
          <div v-for="item in stats.regionDistribution" :key="item.name" class="dist-row">
            <span>{{ item.name }}</span>
            <a-tag color="rgba(34,197,94,0.15)" style="color: #22c55e">{{ item.count }} 家</a-tag>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '../../api/customer'

const stats = ref({})

onMounted(async () => {
  const res = await getStatistics()
  stats.value = res.data
})
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-label {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
  line-height: 1.2;
}
.stat-primary {
  color: #6366f1;
}
.stat-success {
  color: #10b981;
}
.stat-warning {
  color: #f59e0b;
}
.stat-desc {
  color: #9ca3af;
  font-size: 13px;
}
.dist-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}
.dist-row:last-child {
  border-bottom: none;
}
</style>
