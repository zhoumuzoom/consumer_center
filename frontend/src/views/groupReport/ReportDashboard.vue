<template>
  <div>
    <a-row :gutter="20" style="margin-bottom: 20px">
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="总上报次数" :value="overview.totalReports || 0" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="成功上报" :value="overview.successReports || 0" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="失败上报" :value="overview.failReports || 0" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="异常数据" :value="overview.abnormalDataCount || 0" />
        </a-card>
      </a-col>
    </a-row>

    <a-card style="margin-bottom: 20px">
      <template #title>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>集团139系统对接状态</span>
          <a-tag :color="systemStatus.connected ? 'success' : 'error'">{{ systemStatus.connected ? '已连接' : '未连接' }}</a-tag>
        </div>
      </template>
      <a-descriptions :column="3">
        <a-descriptions-item label="系统名称">{{ systemStatus.systemName }}</a-descriptions-item>
        <a-descriptions-item label="同步状态">{{ systemStatus.syncStatus }}</a-descriptions-item>
        <a-descriptions-item label="最后同步时间">{{ systemStatus.lastSyncTime }}</a-descriptions-item>
      </a-descriptions>
      <div style="margin-top: 20px">
        <a-button type="primary" @click="handleManualReport" :loading="reporting">手动同步上报</a-button>
      </div>
    </a-card>

    <a-card>
      <template #title>上报记录</template>
      <a-table
        :columns="recordColumns"
        :data-source="records"
        row-key="id"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === '成功' ? 'success' : record.status === '失败' ? 'error' : 'warning'">
              {{ record.status }}
            </a-tag>
          </template>
        </template>
      </a-table>
      <div style="margin-top: 20px; display: flex; justify-content: flex-end">
        <a-pagination
          v-model:current="query.page"
          :total="recordTotal"
          :page-size="query.size"
          show-quick-jumper
          :show-total="(total) => `共 ${total} 条`"
          @change="loadRecords"
        />
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getReportOverview, manualReport, getReportRecords, getGroup139Status } from '../../api/groupReport'

const overview = ref({})
const systemStatus = ref({})
const records = ref([])
const recordTotal = ref(0)
const query = ref({ page: 1, size: 10 })
const reporting = ref(false)

const recordColumns = [
  { title: '批次号', dataIndex: 'reportBatch', key: 'reportBatch', width: 200 },
  { title: '类型', dataIndex: 'reportType', key: 'reportType', width: 100 },
  { title: '总量', dataIndex: 'totalCount', key: 'totalCount', width: 100 },
  { title: '成功', dataIndex: 'successCount', key: 'successCount', width: 100 },
  { title: '失败', dataIndex: 'failCount', key: 'failCount', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120 },
  { title: '上报时间', dataIndex: 'reportTime', key: 'reportTime' },
]

const loadData = async () => {
  const [overviewRes, statusRes] = await Promise.all([getReportOverview(), getGroup139Status()])
  overview.value = overviewRes.data
  systemStatus.value = statusRes.data
}

const loadRecords = async () => {
  const res = await getReportRecords(query.value)
  records.value = res.data.records || []
  recordTotal.value = res.data.total || 0
}

const handleManualReport = async () => {
  reporting.value = true
  const res = await manualReport({ includeAbnormal: false })
  message.success(`上报完成，批次号：${res.data.reportBatch}，成功：${res.data.successCount}，失败：${res.data.failCount}`)
  reporting.value = false
  loadData()
  loadRecords()
}

onMounted(() => { loadData(); loadRecords() })
</script>
