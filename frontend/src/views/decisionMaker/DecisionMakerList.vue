<template>
  <div>
    <PageHeader eyebrow="DECISION" title="决策关键人管理">
      <template #actions>
        <a-button type="primary" @click="showCreateDialog">新增决策人</a-button>
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
            <a-button size="small" type="primary" @click="showEditDialog(record)">编辑</a-button>
            <a-button size="small" danger @click="handleDelete(record)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="dialogVisible"
      :title="isEdit ? '编辑决策人' : '新增决策人'"
      width="500px"
      @ok="handleSave"
      @cancel="dialogVisible = false"
    >
      <a-form :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
        <a-form-item label="姓名" required><a-input v-model:value="form.name" /></a-form-item>
        <a-form-item label="职位"><a-input v-model:value="form.position" /></a-form-item>
        <a-form-item label="部门"><a-input v-model:value="form.department" /></a-form-item>
        <a-form-item label="公司"><a-input v-model:value="form.company" /></a-form-item>
        <a-form-item label="角色">
          <a-select v-model:value="form.role" placeholder="请选择">
            <a-select-option value="需求发起">需求发起</a-select-option>
            <a-select-option value="方案审批">方案审批</a-select-option>
            <a-select-option value="项目实施">项目实施</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="联系电话"><a-input v-model:value="form.phone" /></a-form-item>
        <a-form-item label="邮箱"><a-input v-model:value="form.email" /></a-form-item>
      </a-form>
    </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { getDecisionChains, createDecisionMaker, updateDecisionMaker, deleteDecisionMaker } from '../../api/decisionMaker'
import PageHeader from '../../components/PageHeader.vue'

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const columns = [
  { title: '姓名', dataIndex: 'name', key: 'name', width: 120 },
  { title: '职位', dataIndex: 'position', key: 'position', width: 150 },
  { title: '部门', dataIndex: 'department', key: 'department', width: 150 },
  { title: '公司', dataIndex: 'company', key: 'company' },
  { title: '角色', dataIndex: 'role', key: 'role', width: 120 },
  { title: '联系电话', dataIndex: 'phone', key: 'phone', width: 150 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
]

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
      await deleteDecisionMaker(row.id)
      message.success('删除成功')
      loadData()
    },
  })
}

onMounted(loadData)
</script>
