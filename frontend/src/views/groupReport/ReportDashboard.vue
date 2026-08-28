<template>
  <div>
    <PageHeader
      eyebrow="REPORT"
      :title="$t('menu.groupReport')"
      :description="$t('groupReport.desc')"
    />

    <a-row :gutter="20" style="margin-bottom: 20px">
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic :title="$t('groupReport.totalReports')" :value="overview.totalReports || 0" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic :title="$t('groupReport.successReports')" :value="overview.successReports || 0" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic :title="$t('groupReport.failReports')" :value="overview.failReports || 0" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic :title="$t('groupReport.abnormalData')" :value="overview.abnormalDataCount || 0" />
        </a-card>
      </a-col>
    </a-row>

    <a-card style="margin-bottom: 20px">
      <template #title>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>{{ $t('groupReport.systemStatus') }}</span>
          <a-tag :color="systemStatus.connected ? 'success' : 'error'">{{ systemStatus.connected ? $t('groupReport.connected') : $t('groupReport.disconnected') }}</a-tag>
        </div>
      </template>
      <a-descriptions :column="3">
        <a-descriptions-item :label="$t('groupReport.systemName')">{{ systemStatus.systemName }}</a-descriptions-item>
        <a-descriptions-item :label="$t('groupReport.syncStatus')">{{ systemStatus.syncStatus }}</a-descriptions-item>
        <a-descriptions-item :label="$t('groupReport.lastSyncTime')">{{ systemStatus.lastSyncTime }}</a-descriptions-item>
      </a-descriptions>
      <div style="margin-top: 20px">
        <a-button type="primary" @click="handleManualReport" :loading="reporting">{{ $t('groupReport.manualReport') }}</a-button>
      </div>
    </a-card>

    <a-card>
      <template #title>{{ $t('groupReport.records') }}</template>
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
          :show-total="(total) => $t('common.totalItems', { total })"
          @change="loadRecords"
        />
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { useI18n } from 'vue-i18n'
import { getReportOverview, manualReport, getReportRecords, getGroup139Status } from '../../api/groupReport'
import PageHeader from '../../components/PageHeader.vue'

const { t } = useI18n()
const overview = ref({})
const systemStatus = ref({})
const records = ref([])
const recordTotal = ref(0)
const query = ref({ page: 1, size: 10 })
const reporting = ref(false)

const recordColumns = computed(() => [
  { title: t('groupReport.reportBatch'), dataIndex: 'reportBatch', key: 'reportBatch', width: 200 },
  { title: t('common.type'), dataIndex: 'reportType', key: 'reportType', width: 100 },
  { title: t('common.total'), dataIndex: 'totalCount', key: 'totalCount', width: 100 },
  { title: t('common.success'), dataIndex: 'successCount', key: 'successCount', width: 100 },
  { title: t('common.fail'), dataIndex: 'failCount', key: 'failCount', width: 100 },
  { title: t('common.status'), dataIndex: 'status', key: 'status', width: 120 },
  { title: t('groupReport.reportTime'), dataIndex: 'reportTime', key: 'reportTime' },
])

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
  message.success(t('groupReport.reportDone', {
    batch: res.data.reportBatch,
    success: res.data.successCount,
    fail: res.data.failCount,
  }))
  reporting.value = false
  loadData()
  loadRecords()
}

onMounted(() => { loadData(); loadRecords() })
</script>
