<template>
  <div>
    <PageHeader eyebrow="INDUSTRY TREE" title="行业树管理" />

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
          <template #title="{ nodeName, customerCount }">
            <span>{{ nodeName }} <a-tag>{{ customerCount || 0 }}家</a-tag></span>
          </template>
        </a-tree>
      </a-col>
      <a-col :span="14">
        <a-card v-if="selectedNode">
          <template #title>{{ selectedNode.nodeName }} - 聚合数据</template>
          <a-row :gutter="20">
            <a-col :span="8"><a-statistic title="客户数量" :value="aggregation?.customerCount || 0" /></a-col>
            <a-col :span="8"><a-statistic title="看管率" :value="aggregation?.managementRate || 0" suffix="%" /></a-col>
            <a-col :span="8"><a-statistic title="集团客户" :value="aggregation?.groupCount || 0" /></a-col>
          </a-row>
          <a-divider />
          <a-row :gutter="20">
            <a-col :span="12"><a-statistic title="战略要客" :value="aggregation?.strategicCount || 0" /></a-col>
            <a-col :span="12"><a-statistic title="重点客户" :value="aggregation?.keyCount || 0" /></a-col>
          </a-row>
        </a-card>
        <a-empty v-else description="请选择一个行业节点" />
      </a-col>
    </a-row>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getIndustryTree, getNodeAggregation } from '../../api/industryTree'
import PageHeader from '../../components/PageHeader.vue'

const treeData = ref([])
const selectedNode = ref(null)
const aggregation = ref(null)

const loadData = async () => {
  const res = await getIndustryTree()
  treeData.value = res.data || []
}

const handleNodeSelect = async (keys, { node }) => {
  selectedNode.value = node
  const res = await getNodeAggregation(node.id)
  aggregation.value = res.data
}

onMounted(loadData)
</script>
