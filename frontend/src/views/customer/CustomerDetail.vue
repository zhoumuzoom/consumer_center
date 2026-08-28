<template>
  <div>
    <PageHeader eyebrow="CUSTOMER" title="客户详情">
      <template #actions>
        <a-space v-if="detail">
          <a-button type="primary" @click="$router.push(`/profile/${detail.id}`)">查看360画像</a-button>
          <a-button @click="$router.back()">返回</a-button>
        </a-space>
      </template>
    </PageHeader>

    <a-card v-if="detail" :loading="loading">
      <a-descriptions :column="3">
        <a-descriptions-item label="客户编号">{{ detail.customerCode }}</a-descriptions-item>
        <a-descriptions-item label="客户名称">{{ detail.customerName }}</a-descriptions-item>
        <a-descriptions-item label="英文名称">{{ detail.customerNameEn }}</a-descriptions-item>
        <a-descriptions-item label="行业">{{ detail.industry }}</a-descriptions-item>
        <a-descriptions-item label="区域">{{ detail.region }}</a-descriptions-item>
        <a-descriptions-item label="规模">{{ detail.scale }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="detail.status === 1 ? 'success' : 'default'">{{ detail.statusDesc }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="客户经理">{{ detail.managerName }}</a-descriptions-item>
        <a-descriptions-item label="联系人">{{ detail.contactPerson }}</a-descriptions-item>
        <a-descriptions-item label="联系电话">{{ detail.contactPhone }}</a-descriptions-item>
        <a-descriptions-item label="联系邮箱">{{ detail.contactEmail }}</a-descriptions-item>
        <a-descriptions-item label="地址" :span="2">{{ detail.address }}</a-descriptions-item>
        <a-descriptions-item label="备注" :span="3">{{ detail.remark }}</a-descriptions-item>
      </a-descriptions>

      <a-row :gutter="20" style="margin-top: 20px">
        <a-col :span="8"><a-statistic title="分支机构" :value="detail?.branchCount || 0" /></a-col>
        <a-col :span="8"><a-statistic title="关联决策人" :value="detail?.decisionMakerCount || 0" /></a-col>
        <a-col :span="8"><a-statistic title="交互记录" :value="detail?.interactionCount || 0" /></a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCustomerDetail } from '../../api/customer'
import PageHeader from '../../components/PageHeader.vue'

const route = useRoute()
const loading = ref(false)
const detail = ref(null)

onMounted(async () => {
  loading.value = true
  const res = await getCustomerDetail(route.params.id)
  detail.value = res.data
  loading.value = false
})
</script>
