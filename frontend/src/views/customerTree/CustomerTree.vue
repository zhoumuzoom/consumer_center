<template>
  <a-card>
    <template #title>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>客户树管理</span>
        <a-button type="primary" @click="showCreateDialog">新增节点</a-button>
      </div>
    </template>

    <a-row :gutter="20">
      <a-col :span="10">
        <a-tree
          v-if="treeData.length"
          :tree-data="treeData"
          :field-names="{ key: 'id', title: 'nodeName', children: 'children' }"
          default-expand-all
          @select="handleNodeSelect"
        >
          <template #title="{ nodeName, nodeType, id }">
            <span style="display: flex; justify-content: space-between; width: 100%; align-items: center">
              <span>{{ nodeName }} <a-tag>{{ nodeType }}</a-tag></span>
              <span>
                <a-button size="small" type="link" @click.stop="showEditDialog({ nodeName, nodeType, id })">编辑</a-button>
                <a-button size="small" type="link" danger @click.stop="handleDelete({ id })">删除</a-button>
              </span>
            </span>
          </template>
        </a-tree>
      </a-col>
      <a-col :span="14">
        <a-card v-if="selectedNode">
          <template #title>节点详情 - {{ selectedNode.nodeName }}</template>
          <a-descriptions :column="2">
            <a-descriptions-item label="节点名称">{{ selectedNode.nodeName }}</a-descriptions-item>
            <a-descriptions-item label="节点类型">{{ selectedNode.nodeType }}</a-descriptions-item>
            <a-descriptions-item label="层级">{{ selectedNode.level }}</a-descriptions-item>
            <a-descriptions-item label="责任人">{{ selectedNode.managerName }}</a-descriptions-item>
            <a-descriptions-item label="职责描述" :span="2">{{ selectedNode.responsibility }}</a-descriptions-item>
            <a-descriptions-item label="客户数量">{{ selectedNode.customerCount }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
        <a-empty v-else description="请选择一个节点" />
      </a-col>
    </a-row>

    <a-modal
      v-model:open="dialogVisible"
      :title="isEdit ? '编辑节点' : '新增节点'"
      width="500px"
      @ok="handleSave"
      @cancel="dialogVisible = false"
    >
      <a-form :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="节点名称" required>
          <a-input v-model:value="form.nodeName" />
        </a-form-item>
        <a-form-item label="父节点ID">
          <a-input-number v-model:value="form.parentId" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="节点类型">
          <a-select v-model:value="form.nodeType" placeholder="请选择">
            <a-select-option value="集团">集团</a-select-option>
            <a-select-option value="区域">区域</a-select-option>
            <a-select-option value="部门">部门</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="责任人">
          <a-input v-model:value="form.managerName" />
        </a-form-item>
        <a-form-item label="职责描述">
          <a-textarea v-model:value="form.responsibility" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { getCustomerTree, createTreeNode, updateTreeNode, deleteTreeNode } from '../../api/customerTree'

const treeData = ref([])
const selectedNode = ref(null)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const loadData = async () => {
  const res = await getCustomerTree()
  treeData.value = res.data || []
}

const handleNodeSelect = (keys, { node }) => {
  selectedNode.value = node
}

const showCreateDialog = () => {
  isEdit.value = false
  form.value = { parentId: 0 }
  dialogVisible.value = true
}

const showEditDialog = (data) => {
  isEdit.value = true
  form.value = { ...data }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (isEdit.value) {
    await updateTreeNode(form.value.id, form.value)
  } else {
    await createTreeNode(form.value)
  }
  message.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (data) => {
  Modal.confirm({
    title: '确认删除该节点？子节点将一并删除',
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      await deleteTreeNode(data.id)
      message.success('删除成功')
      loadData()
    },
  })
}

onMounted(loadData)
</script>
