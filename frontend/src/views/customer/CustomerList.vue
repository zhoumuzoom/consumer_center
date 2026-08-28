<template>
  <div>
    <PageHeader eyebrow="CUSTOMER" :title="$t('menu.customers')">
      <template #actions>
        <a-button type="primary" @click="showCreateDialog">{{ $t('customer.add') }}</a-button>
      </template>
    </PageHeader>

    <a-card>
      <a-form layout="inline" :model="queryForm" style="margin-bottom: 20px">
      <a-form-item :label="$t('customer.keyword')">
        <a-input v-model:value="queryForm.keyword" :placeholder="$t('customer.keywordPlaceholder')" allow-clear />
      </a-form-item>
      <a-form-item :label="$t('common.industry')">
        <a-input v-model:value="queryForm.industry" :placeholder="$t('common.industry')" allow-clear />
      </a-form-item>
      <a-form-item :label="$t('common.region')">
        <a-input v-model:value="queryForm.region" :placeholder="$t('common.region')" allow-clear />
      </a-form-item>
      <a-form-item :label="$t('common.status')">
        <a-select v-model:value="queryForm.status" :placeholder="$t('common.all')" allow-clear style="width: 120px">
          <a-select-option :value="0">{{ $t('customer.statusUnmanaged') }}</a-select-option>
          <a-select-option :value="1">{{ $t('customer.statusManaged') }}</a-select-option>
          <a-select-option :value="2">{{ $t('customer.statusRemoved') }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="loadData">{{ $t('common.search') }}</a-button>
        <a-button @click="resetQuery" style="margin-left: 8px">{{ $t('common.reset') }}</a-button>
      </a-form-item>
    </a-form>

    <a-table
      :columns="columns"
      :data-source="tableData"
      :loading="loading"
      :pagination="false"
      row-key="id"
      :scroll="{ x: 1200 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'customerName'">
          <router-link :to="`/customers/${record.id}`">{{ record.customerName }}</router-link>
        </template>
        <template v-if="column.key === 'statusDesc'">
          <a-tag :color="record.status === 1 ? 'success' : record.status === 2 ? 'error' : 'default'">{{ record.statusDesc }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button size="small" @click="showDetail(record)">{{ $t('common.detail') }}</a-button>
            <a-button size="small" type="primary" @click="showEditDialog(record)">{{ $t('common.edit') }}</a-button>
            <a-button size="small" danger @click="handleDelete(record)">{{ $t('common.delete') }}</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <div style="margin-top: 20px; display: flex; justify-content: flex-end">
      <a-pagination
        v-model:current="queryForm.page"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-size-options="['10', '20', '50']"
        show-size-changer
        show-quick-jumper
        :show-total="(total) => $t('common.totalItems', { total })"
        @change="loadData"
        @showSizeChange="loadData"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <a-modal
      v-model:open="dialogVisible"
      :title="isEdit ? $t('customer.editTitle') : $t('customer.addTitle')"
      width="600px"
      @ok="handleSave"
      @cancel="dialogVisible = false"
    >
      <a-form :model="form" label-align="left" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item :label="$t('common.customerName')" required>
          <a-input v-model:value="form.customerName" />
        </a-form-item>
        <a-form-item :label="$t('customer.nameEn')">
          <a-input v-model:value="form.customerNameEn" />
        </a-form-item>
        <a-form-item :label="$t('common.industry')">
          <a-input v-model:value="form.industry" />
        </a-form-item>
        <a-form-item :label="$t('common.region')">
          <a-input v-model:value="form.region" />
        </a-form-item>
        <a-form-item :label="$t('common.scale')">
          <a-select v-model:value="form.scale" :placeholder="$t('common.selectPlease')">
            <a-select-option value="大型">{{ $t('customer.scaleLarge') }}</a-select-option>
            <a-select-option value="中型">{{ $t('customer.scaleMedium') }}</a-select-option>
            <a-select-option value="小型">{{ $t('customer.scaleSmall') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('customer.contactPerson')">
          <a-input v-model:value="form.contactPerson" />
        </a-form-item>
        <a-form-item :label="$t('customer.contactPhone')">
          <a-input v-model:value="form.contactPhone" />
        </a-form-item>
        <a-form-item :label="$t('common.remark')">
          <a-textarea v-model:value="form.remark" :rows="3" />
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
import { getCustomerList, createCustomer, updateCustomer, deleteCustomer } from '../../api/customer'
import PageHeader from '../../components/PageHeader.vue'

const { t } = useI18n()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = ref({ page: 1, size: 10, keyword: '', industry: '', region: '', status: null })
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const columns = computed(() => [
  { title: t('customer.code'), dataIndex: 'customerCode', key: 'customerCode', width: 180 },
  { title: t('common.customerName'), dataIndex: 'customerName', key: 'customerName', width: 200 },
  { title: t('common.industry'), dataIndex: 'industry', key: 'industry', width: 120 },
  { title: t('common.region'), dataIndex: 'region', key: 'region', width: 100 },
  { title: t('common.scale'), dataIndex: 'scale', key: 'scale', width: 100 },
  { title: t('common.status'), dataIndex: 'statusDesc', key: 'statusDesc', width: 100 },
  { title: t('customer.managerName'), dataIndex: 'managerName', key: 'managerName', width: 120 },
  { title: t('common.action'), key: 'action', width: 250, fixed: 'right' },
])

const loadData = async () => {
  loading.value = true
  const res = await getCustomerList(queryForm.value)
  tableData.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

const resetQuery = () => {
  queryForm.value = { page: 1, size: 10, keyword: '', industry: '', region: '', status: null }
  loadData()
}

const showCreateDialog = () => {
  isEdit.value = false
  form.value = {}
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const showDetail = (row) => {
  window.open(`/customers/${row.id}`, '_blank')
}

const handleSave = async () => {
  if (isEdit.value) {
    await updateCustomer(form.value.id, form.value)
    message.success(t('common.updateSuccess'))
  } else {
    await createCustomer(form.value)
    message.success(t('common.addSuccess'))
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  Modal.confirm({
    title: t('customer.deleteConfirm'),
    okText: t('common.confirm'),
    cancelText: t('common.cancel'),
    onOk: async () => {
      await deleteCustomer(row.id)
      message.success(t('common.deleteSuccess'))
      loadData()
    },
  })
}

onMounted(loadData)
</script>
