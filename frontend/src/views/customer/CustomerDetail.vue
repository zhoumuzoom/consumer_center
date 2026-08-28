<template>
  <div>
    <PageHeader eyebrow="CUSTOMER" :title="$t('menu.customerDetail')">
      <template #actions>
        <a-space v-if="detail">
          <a-button type="primary" @click="$router.push(`/profile/${detail.id}`)">{{ $t('customer.viewProfile') }}</a-button>
          <a-button @click="$router.back()">{{ $t('common.back') }}</a-button>
        </a-space>
      </template>
    </PageHeader>

    <a-card v-if="detail" :loading="loading">
      <a-descriptions :column="3">
        <a-descriptions-item :label="$t('customer.code')">{{ detail.customerCode }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.customerName')">{{ detail.customerName }}</a-descriptions-item>
        <a-descriptions-item :label="$t('customer.nameEn')">{{ detail.customerNameEn }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.industry')">{{ detail.industry }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.region')">{{ detail.region }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.scale')">{{ detail.scale }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.status')">
          <a-tag :color="detail.status === 1 ? 'success' : 'default'">{{ detail.statusDesc }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item :label="$t('customer.managerName')">{{ detail.managerName }}</a-descriptions-item>
        <a-descriptions-item :label="$t('customer.contactPerson')">{{ detail.contactPerson }}</a-descriptions-item>
        <a-descriptions-item :label="$t('customer.contactPhone')">{{ detail.contactPhone }}</a-descriptions-item>
        <a-descriptions-item :label="$t('customer.contactEmail')">{{ detail.contactEmail }}</a-descriptions-item>
        <a-descriptions-item :label="$t('customer.address')" :span="2">{{ detail.address }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.remark')" :span="3">{{ detail.remark }}</a-descriptions-item>
      </a-descriptions>

      <a-row :gutter="20" style="margin-top: 20px">
        <a-col :span="8"><a-statistic :title="$t('customer.branchCount')" :value="detail?.branchCount || 0" /></a-col>
        <a-col :span="8"><a-statistic :title="$t('customer.decisionMakerCount')" :value="detail?.decisionMakerCount || 0" /></a-col>
        <a-col :span="8"><a-statistic :title="$t('customer.interactionCount')" :value="detail?.interactionCount || 0" /></a-col>
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
