<template>
  <div>
    <PageHeader eyebrow="DECISION CHAIN" :title="$t('menu.decisionChains')">
      <template #actions>
        <a-button type="primary" @click="showCreateDialog">{{ $t('decisionChain.add') }}</a-button>
      </template>
    </PageHeader>

    <a-card>
      <a-form layout="inline" style="margin-bottom: 20px">
      <a-form-item :label="$t('common.customerName')">
        <a-input v-model:value="query.customerName" allow-clear />
      </a-form-item>
      <a-form-item :label="$t('common.projectName')">
        <a-input v-model:value="query.projectName" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="loadData">{{ $t('common.search') }}</a-button>
      </a-form-item>
    </a-form>

    <a-table
      :columns="columns"
      :data-source="tableData"
      row-key="id"
      :pagination="false"
      :scroll="{ x: 1200 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'influenceLevel'">
          <a-tag :color="record.influenceLevel === '高' ? 'error' : record.influenceLevel === '中' ? 'warning' : 'default'">
            {{ record.influenceLevel }}
          </a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button size="small" type="primary" @click="showEditDialog(record)">{{ $t('common.edit') }}</a-button>
            <a-button size="small" danger @click="handleDelete(record)">{{ $t('common.delete') }}</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <div style="margin-top: 20px; display: flex; justify-content: flex-end">
      <a-pagination
        v-model:current="query.page"
        :total="total"
        :page-size="query.size"
        show-quick-jumper
        :show-total="(total) => $t('common.totalItems', { total })"
        @change="loadData"
      />
    </div>

    <a-modal
      v-model:open="dialogVisible"
      :title="isEdit ? $t('decisionChain.editTitle') : $t('decisionChain.addTitle')"
      width="500px"
      @ok="handleSave"
      @cancel="dialogVisible = false"
    >
      <a-form :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
        <a-form-item :label="$t('decisionChain.customerId')" required>
          <a-input-number v-model:value="form.customerId" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('decisionChain.decisionMakerId')" required>
          <a-input-number v-model:value="form.decisionMakerId" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('common.projectName')">
          <a-input v-model:value="form.projectName" />
        </a-form-item>
        <a-form-item :label="$t('decisionChain.decisionStage')">
          <a-select v-model:value="form.decisionStage" :placeholder="$t('common.selectPlease')">
            <a-select-option value="需求发起">{{ $t('decisionMakers.roleInitiate') }}</a-select-option>
            <a-select-option value="方案审批">{{ $t('decisionMakers.roleApprove') }}</a-select-option>
            <a-select-option value="项目实施">{{ $t('decisionMakers.roleImplement') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('decisionChain.influenceLevel')">
          <a-select v-model:value="form.influenceLevel" :placeholder="$t('common.selectPlease')">
            <a-select-option value="高">{{ $t('decisionChain.influenceHigh') }}</a-select-option>
            <a-select-option value="中">{{ $t('decisionChain.influenceMedium') }}</a-select-option>
            <a-select-option value="低">{{ $t('decisionChain.influenceLow') }}</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { useI18n } from 'vue-i18n'
import { getDecisionChains, createDecisionChain, updateDecisionChain, deleteDecisionChain } from '../../api/decisionMaker'
import PageHeader from '../../components/PageHeader.vue'

const { t } = useI18n()

const tableData = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, customerName: '', projectName: '' })
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const columns = computed(() => [
  { title: t('decisionChain.customer'), dataIndex: 'customerName', key: 'customerName', width: 180 },
  { title: t('decisionChain.decisionMakerName'), dataIndex: 'decisionMakerName', key: 'decisionMakerName', width: 120 },
  { title: t('common.position'), dataIndex: 'decisionMakerPosition', key: 'decisionMakerPosition', width: 150 },
  { title: t('common.project'), dataIndex: 'projectName', key: 'projectName' },
  { title: t('decisionChain.decisionStage'), dataIndex: 'decisionStage', key: 'decisionStage', width: 120 },
  { title: t('decisionChain.influenceLevel'), dataIndex: 'influenceLevel', key: 'influenceLevel', width: 100 },
  { title: t('common.action'), key: 'action', width: 200, fixed: 'right' },
])

const loadData = async () => {
  const res = await getDecisionChains(query.value)
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const showCreateDialog = () => { isEdit.value = false; form.value = {}; dialogVisible.value = true }
const showEditDialog = (row) => { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

const handleSave = async () => {
  if (isEdit.value) {
    await updateDecisionChain(form.value.id, form.value)
  } else {
    await createDecisionChain(form.value)
  }
  message.success(t('decisionChain.saveSuccess'))
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  Modal.confirm({
    title: t('decisionChain.deleteConfirm'),
    okText: t('common.confirm'),
    cancelText: t('common.cancel'),
    onOk: async () => {
      await deleteDecisionChain(row.id)
      message.success(t('common.deleteSuccess'))
      loadData()
    },
  })
}

onMounted(loadData)
</script>
