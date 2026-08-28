<template>
  <div>
    <PageHeader eyebrow="DECISION" :title="$t('decisionMakers.title')">
      <template #actions>
        <a-button type="primary" @click="showCreateDialog">{{ $t('decisionMakers.add') }}</a-button>
      </template>
    </PageHeader>

    <a-card>
      <a-table
        :columns="columns"
        :data-source="tableData"
        row-key="id"
        :pagination="false"
        :scroll="{ x: 1100 }"
      >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button size="small" type="primary" @click="showEditDialog(record)">{{ $t('common.edit') }}</a-button>
            <a-button size="small" danger @click="handleDelete(record)">{{ $t('common.delete') }}</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="dialogVisible"
      :title="isEdit ? $t('decisionMakers.editTitle') : $t('decisionMakers.addTitle')"
      width="500px"
      @ok="handleSave"
      @cancel="dialogVisible = false"
    >
      <a-form :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
        <a-form-item :label="$t('decisionMakers.name')" required><a-input v-model:value="form.name" /></a-form-item>
        <a-form-item :label="$t('common.position')"><a-input v-model:value="form.position" /></a-form-item>
        <a-form-item :label="$t('decisionMakers.department')"><a-input v-model:value="form.department" /></a-form-item>
        <a-form-item :label="$t('decisionMakers.company')"><a-input v-model:value="form.company" /></a-form-item>
        <a-form-item :label="$t('decisionMakers.role')">
          <a-select v-model:value="form.role" :placeholder="$t('common.selectPlease')">
            <a-select-option value="需求发起">{{ $t('decisionMakers.roleInitiate') }}</a-select-option>
            <a-select-option value="方案审批">{{ $t('decisionMakers.roleApprove') }}</a-select-option>
            <a-select-option value="项目实施">{{ $t('decisionMakers.roleImplement') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('common.phone')"><a-input v-model:value="form.phone" /></a-form-item>
        <a-form-item :label="$t('common.email')"><a-input v-model:value="form.email" /></a-form-item>
      </a-form>
    </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { useI18n } from 'vue-i18n'
import { getDecisionChains, createDecisionMaker, updateDecisionMaker, deleteDecisionMaker } from '../../api/decisionMaker'
import PageHeader from '../../components/PageHeader.vue'

const { t } = useI18n()

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const columns = computed(() => [
  { title: t('decisionMakers.name'), dataIndex: 'name', key: 'name', width: 120 },
  { title: t('common.position'), dataIndex: 'position', key: 'position', width: 150 },
  { title: t('decisionMakers.department'), dataIndex: 'department', key: 'department', width: 150 },
  { title: t('decisionMakers.company'), dataIndex: 'company', key: 'company' },
  { title: t('decisionMakers.role'), dataIndex: 'role', key: 'role', width: 120 },
  { title: t('common.phone'), dataIndex: 'phone', key: 'phone', width: 150 },
  { title: t('common.action'), key: 'action', width: 200, fixed: 'right' },
])

const loadData = async () => {
  const res = await getDecisionChains({ page: 1, size: 100 })
  const allRecords = res.data.records || []
  tableData.value = allRecords.map(r => ({
    id: r.decisionMakerId,
    name: r.decisionMakerName,
    position: r.decisionMakerPosition
  }))
  // 去重
  const seen = new Set()
  tableData.value = tableData.value.filter(item => {
    if (seen.has(item.id)) return false
    seen.add(item.id)
    return true
  })
}

const showCreateDialog = () => { isEdit.value = false; form.value = {}; dialogVisible.value = true }
const showEditDialog = (row) => { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

const handleSave = async () => {
  if (isEdit.value) {
    await updateDecisionMaker(form.value.id, form.value)
  } else {
    await createDecisionMaker(form.value)
  }
  message.success(t('common.saveSuccess'))
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  Modal.confirm({
    title: t('common.deleteConfirm'),
    okText: t('common.confirm'),
    cancelText: t('common.cancel'),
    onOk: async () => {
      await deleteDecisionMaker(row.id)
      message.success(t('common.deleteSuccess'))
      loadData()
    },
  })
}

onMounted(loadData)
</script>
