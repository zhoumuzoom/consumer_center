<template>
  <a-card>
    <template #title>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>决策链管理</span>
        <a-button type="primary" @click="showCreateDialog">建立决策链</a-button>
      </div>
    </template>

    <a-form layout="inline" style="margin-bottom: 20px">
      <a-form-item label="客户名称">
        <a-input v-model:value="query.customerName" allow-clear />
      </a-form-item>
      <a-form-item label="项目名称">
        <a-input v-model:value="query.projectName" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="loadData">查询</a-button>
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
            <a-button size="small" type="primary" @click="showEditDialog(record)">编辑</a-button>
            <a-button size="small" danger @click="handleDelete(record)">删除</a-button>
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
        :show-total="(total) => `共 ${total} 条`"
        @change="loadData"
      />
    </div>

    <a-modal
      v-model:open="dialogVisible"
      :title="isEdit ? '编辑决策链' : '建立决策链'"
      width="500px"
      @ok="handleSave"
      @cancel="dialogVisible = false"
    >
      <a-form :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
        <a-form-item label="客户ID" required>
          <a-input-number v-model:value="form.customerId" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item label="决策人ID" required>
          <a-input-number v-model:value="form.decisionMakerId" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item label="项目名称">
          <a-input v-model:value="form.projectName" />
        </a-form-item>
        <a-form-item label="决策阶段">
          <a-select v-model:value="form.decisionStage" placeholder="请选择">
            <a-select-option value="需求发起">需求发起</a-select-option>
            <a-select-option value="方案审批">方案审批</a-select-option>
            <a-select-option value="项目实施">项目实施</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="影响力">
          <a-select v-model:value="form.influenceLevel" placeholder="请选择">
            <a-select-option value="高">高</a-select-option>
            <a-select-option value="中">中</a-select-option>
            <a-select-option value="低">低</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </a-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { getDecisionChains, createDecisionChain, updateDecisionChain, deleteDecisionChain } from '../../api/decisionMaker'

const tableData = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, customerName: '', projectName: '' })
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const columns = [
  { title: '客户', dataIndex: 'customerName', key: 'customerName', width: 180 },
  { title: '决策人', dataIndex: 'decisionMakerName', key: 'decisionMakerName', width: 120 },
  { title: '职位', dataIndex: 'decisionMakerPosition', key: 'decisionMakerPosition', width: 150 },
  { title: '项目', dataIndex: 'projectName', key: 'projectName' },
  { title: '决策阶段', dataIndex: 'decisionStage', key: 'decisionStage', width: 120 },
  { title: '影响力', dataIndex: 'influenceLevel', key: 'influenceLevel', width: 100 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
]

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
  message.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row) => {
  Modal.confirm({
    title: '确认删除？',
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      await deleteDecisionChain(row.id)
      message.success('删除成功')
      loadData()
    },
  })
}

onMounted(loadData)
</script>
