<template>
  <div>
    <PageHeader eyebrow="CUSTOMER TREE" :title="$t('menu.customerTree')">
      <template #actions>
        <a-button type="primary" @click="showCreateDialog">{{ $t('customerTree.addNode') }}</a-button>
      </template>
    </PageHeader>

    <a-card>
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
                <a-button size="small" type="link" @click.stop="showEditDialog({ nodeName, nodeType, id })">{{ $t('common.edit') }}</a-button>
                <a-button size="small" type="link" danger @click.stop="handleDelete({ id })">{{ $t('common.delete') }}</a-button>
              </span>
            </span>
          </template>
        </a-tree>
      </a-col>
      <a-col :span="14">
        <a-card v-if="selectedNode">
          <template #title>{{ $t('customerTree.nodeDetail') }} - {{ selectedNode.nodeName }}</template>
          <a-descriptions :column="2">
            <a-descriptions-item :label="$t('customerTree.nodeName')">{{ selectedNode.nodeName }}</a-descriptions-item>
            <a-descriptions-item :label="$t('customerTree.nodeType')">{{ selectedNode.nodeType }}</a-descriptions-item>
            <a-descriptions-item :label="$t('customerTree.level')">{{ selectedNode.level }}</a-descriptions-item>
            <a-descriptions-item :label="$t('customerTree.managerName')">{{ selectedNode.managerName }}</a-descriptions-item>
            <a-descriptions-item :label="$t('customerTree.responsibility')" :span="2">{{ selectedNode.responsibility }}</a-descriptions-item>
            <a-descriptions-item :label="$t('customerTree.customerCount')">{{ selectedNode.customerCount }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
        <a-empty v-else :description="$t('customerTree.selectNodeTip')" />
      </a-col>
    </a-row>

    <a-modal
      v-model:open="dialogVisible"
      :title="isEdit ? $t('customerTree.editNode') : $t('customerTree.addNode')"
      width="500px"
      @ok="handleSave"
      @cancel="dialogVisible = false"
    >
      <a-form :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item :label="$t('customerTree.nodeName')" required>
          <a-input v-model:value="form.nodeName" />
        </a-form-item>
        <a-form-item :label="$t('customerTree.parentId')">
          <a-input-number v-model:value="form.parentId" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item :label="$t('customerTree.nodeType')">
          <a-select v-model:value="form.nodeType" :placeholder="$t('common.selectPlease')">
            <a-select-option value="集团">{{ $t('customerTree.typeGroup') }}</a-select-option>
            <a-select-option value="区域">{{ $t('customerTree.typeRegion') }}</a-select-option>
            <a-select-option value="部门">{{ $t('customerTree.typeDepartment') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('customerTree.managerName')">
          <a-input v-model:value="form.managerName" />
        </a-form-item>
        <a-form-item :label="$t('customerTree.responsibility')">
          <a-textarea v-model:value="form.responsibility" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { useI18n } from 'vue-i18n'
import { getCustomerTree, createTreeNode, updateTreeNode, deleteTreeNode } from '../../api/customerTree'
import PageHeader from '../../components/PageHeader.vue'

const { t } = useI18n()

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
  message.success(t('customerTree.saveSuccess'))
  dialogVisible.value = false
  loadData()
}

const handleDelete = (data) => {
  Modal.confirm({
    title: t('customerTree.deleteConfirm'),
    okText: t('common.confirm'),
    cancelText: t('common.cancel'),
    onOk: async () => {
      await deleteTreeNode(data.id)
      message.success(t('common.deleteSuccess'))
      loadData()
    },
  })
}

onMounted(loadData)
</script>
