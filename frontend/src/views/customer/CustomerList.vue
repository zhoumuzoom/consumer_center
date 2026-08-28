<template>
  <div>
    <PageHeader eyebrow="CUSTOMER" title="客户列表">
      <template #actions>
        <a-button type="primary" @click="showCreateDialog">新增客户</a-button>
      </template>
    </PageHeader>

    <a-card>
      <a-form layout="inline" :model="queryForm" style="margin-bottom: 20px">
      <a-form-item label="关键词">
        <a-input v-model:value="queryForm.keyword" placeholder="客户名称/编号" allow-clear />
      </a-form-item>
      <a-form-item label="行业">
        <a-input v-model:value="queryForm.industry" placeholder="行业" allow-clear />
      </a-form-item>
      <a-form-item label="区域">
        <a-input v-model:value="queryForm.region" placeholder="区域" allow-clear />
      </a-form-item>
      <a-form-item label="状态">
        <a-select v-model:value="queryForm.status" placeholder="全部" allow-clear style="width: 120px">
          <a-select-option :value="0">未看管</a-select-option>
          <a-select-option :value="1">已看管</a-select-option>
          <a-select-option :value="2">已剔除</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="loadData">查询</a-button>
        <a-button @click="resetQuery" style="margin-left: 8px">重置</a-button>
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
            <a-button size="small" @click="showDetail(record)">详情</a-button>
            <a-button size="small" type="primary" @click="showEditDialog(record)">编辑</a-button>
            <a-button size="small" danger @click="handleDelete(record)">删除</a-button>
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
        :show-total="(total) => `共 ${total} 条`"
        @change="loadData"
        @showSizeChange="loadData"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <a-modal
      v-model:open="dialogVisible"
      :title="isEdit ? '编辑客户' : '新增客户'"
      width="600px"
      @ok="handleSave"
      @cancel="dialogVisible = false"
    >
      <a-form :model="form" label-align="left" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="客户名称" required>
          <a-input v-model:value="form.customerName" />
        </a-form-item>
        <a-form-item label="英文名称">
          <a-input v-model:value="form.customerNameEn" />
        </a-form-item>
        <a-form-item label="行业">
          <a-input v-model:value="form.industry" />
        </a-form-item>
        <a-form-item label="区域">
          <a-input v-model:value="form.region" />
        </a-form-item>
        <a-form-item label="规模">
          <a-select v-model:value="form.scale" placeholder="请选择">
            <a-select-option value="大型">大型</a-select-option>
            <a-select-option value="中型">中型</a-select-option>
            <a-select-option value="小型">小型</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="联系人">
          <a-input v-model:value="form.contactPerson" />
        </a-form-item>
        <a-form-item label="联系电话">
          <a-input v-model:value="form.contactPhone" />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="form.remark" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { getCustomerList, createCustomer, updateCustomer, deleteCustomer } from '../../api/customer'
import PageHeader from '../../components/PageHeader.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = ref({ page: 1, size: 10, keyword: '', industry: '', region: '', status: null })
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const columns = [
  { title: '客户编号', dataIndex: 'customerCode', key: 'customerCode', width: 180 },
  { title: '客户名称', dataIndex: 'customerName', key: 'customerName', width: 200 },
  { title: '行业', dataIndex: 'industry', key: 'industry', width: 120 },
  { title: '区域', dataIndex: 'region', key: 'region', width: 100 },
  { title: '规模', dataIndex: 'scale', key: 'scale', width: 100 },
  { title: '状态', dataIndex: 'statusDesc', key: 'statusDesc', width: 100 },
  { title: '客户经理', dataIndex: 'managerName', key: 'managerName', width: 120 },
  { title: '操作', key: 'action', width: 250, fixed: 'right' },
]

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
    message.success('修改成功')
  } else {
    await createCustomer(form.value)
    message.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  Modal.confirm({
    title: '确认删除该客户？',
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      await deleteCustomer(row.id)
      message.success('删除成功')
      loadData()
    },
  })
}

onMounted(loadData)
</script>
