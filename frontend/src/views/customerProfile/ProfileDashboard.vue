<template>
  <a-spin :spinning="loading">
    <PageHeader back eyebrow="PROFILE" :title="$t('profile.title')" />

    <!-- 基础信息 -->
    <a-card v-if="profile" style="margin-bottom: 20px">
      <template #title>{{ $t('profile.basicInfo') }} - {{ profile.basicInfo?.customerName }}</template>
      <a-descriptions :column="3">
        <a-descriptions-item :label="$t('customer.code')">{{ profile.basicInfo?.customerCode }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.customerName')">{{ profile.basicInfo?.customerName }}</a-descriptions-item>
        <a-descriptions-item :label="$t('customer.nameEn')">{{ profile.basicInfo?.customerNameEn }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.industry')">{{ profile.basicInfo?.industry }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.region')">{{ profile.basicInfo?.region }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.scale')">{{ profile.basicInfo?.scale }}</a-descriptions-item>
        <a-descriptions-item :label="$t('common.status')">{{ profile.managementInfo?.statusDesc }}</a-descriptions-item>
        <a-descriptions-item :label="$t('customer.managerName')">{{ profile.managementInfo?.managerName }}</a-descriptions-item>
        <a-descriptions-item :label="$t('profile.joinTime')">{{ profile.managementInfo?.joinTime }}</a-descriptions-item>
      </a-descriptions>
    </a-card>

    <!-- 业务数据 -->
    <a-card v-if="profile" style="margin-bottom: 20px">
      <template #title>{{ $t('profile.businessOverview') }}</template>
      <a-row :gutter="20">
        <a-col :span="4"><a-statistic :title="$t('profile.bidCount')" :value="profile?.businessInfo?.bidCount || 0" /></a-col>
        <a-col :span="4"><a-statistic :title="$t('profile.bidAmount')" :value="profile?.businessInfo?.bidAmount || 0" /></a-col>
        <a-col :span="4"><a-statistic :title="$t('profile.itBidCount')" :value="profile?.businessInfo?.itBidCount || 0" /></a-col>
        <a-col :span="4"><a-statistic :title="$t('profile.orderCount')" :value="profile?.businessInfo?.orderCount || 0" /></a-col>
        <a-col :span="4"><a-statistic :title="$t('profile.revenueContribution')" :value="profile?.businessInfo?.revenueContribution || 0" /></a-col>
        <a-col :span="4"><a-statistic :title="$t('profile.productCount')" :value="profile?.businessInfo?.productCount || 0" /></a-col>
      </a-row>
    </a-card>

    <!-- 多Tab内容区 -->
    <a-card v-if="profile">
      <a-tabs v-model:activeKey="activeTab">
        <!-- 分支组织架构 -->
        <a-tab-pane key="branches" :tab="$t('profile.tabBranches')">
          <a-tree
            v-if="branches.length"
            :tree-data="branches"
            :field-names="{ key: 'id', title: 'branchName', children: 'children' }"
            default-expand-all
          >
            <template #title="{ branchName, branchType, region }">
              <span>{{ branchName }}
                <a-tag v-if="branchType" color="blue">{{ branchType }}</a-tag>
                <a-tag v-if="region">{{ region }}</a-tag>
              </span>
            </template>
          </a-tree>
          <a-empty v-else :description="$t('profile.noBranches')" />
        </a-tab-pane>

        <!-- 政策动态 -->
        <a-tab-pane key="policies" :tab="$t('profile.tabPolicies')">
          <div style="margin-bottom: 12px; text-align: right">
            <a-button type="primary" size="small" @click="showPolicyDialog">{{ $t('profile.addPolicy') }}</a-button>
          </div>
          <a-table :columns="policyColumns" :data-source="profile.policies || []" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === '有效' ? 'success' : 'default'">{{ record.status }}</a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button size="small" danger @click="handleDeletePolicy(record)">{{ $t('common.delete') }}</a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- 历史订单/项目 -->
        <a-tab-pane key="orders" :tab="$t('profile.tabOrders')">
          <div style="margin-bottom: 12px; text-align: right">
            <a-button type="primary" size="small" @click="showOrderDialog">{{ $t('profile.addOrder') }}</a-button>
          </div>
          <a-table :columns="orderColumns" :data-source="profile.orders || []" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'orderStatus'">
                <a-tag :color="record.orderStatus === '已验收' ? 'success' : record.orderStatus === '执行中' ? 'processing' : 'default'">{{ record.orderStatus }}</a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button size="small" danger @click="handleDeleteOrder(record)">{{ $t('common.delete') }}</a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- 产品与方案 -->
        <a-tab-pane key="products" :tab="$t('profile.tabProducts')">
          <div style="margin-bottom: 12px; text-align: right">
            <a-button type="primary" size="small" @click="showProductDialog">{{ $t('profile.addProduct') }}</a-button>
          </div>
          <a-table :columns="productColumns" :data-source="profile.products || []" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'usageStatus'">
                <a-tag :color="record.usageStatus === '使用中' ? 'success' : record.usageStatus === '评估中' ? 'warning' : 'default'">{{ record.usageStatus }}</a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button size="small" danger @click="handleDeleteProduct(record)">{{ $t('common.delete') }}</a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- 决策链与关键人 -->
        <a-tab-pane key="decisions" :tab="$t('profile.tabDecisions')">
          <a-empty v-if="!profile?.decisionChains?.length" :description="$t('profile.noDecisionChains')" />
          <a-table v-else :columns="decisionColumns" :data-source="profile.decisionChains" :pagination="false" row-key="id" size="small" />
        </a-tab-pane>

        <!-- 交互记录 -->
        <a-tab-pane key="interactions" :tab="$t('profile.tabInteractions')">
          <a-timeline>
            <a-timeline-item v-for="record in profile?.interactionRecords" :key="record.id" color="blue">
              <a-card size="small">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px">
                  <a-tag>{{ record.type }}</a-tag>
                  <span style="color: #9ca3af; font-size: 12px; white-space: nowrap">{{ record.time }}</span>
                </div>
                <p style="margin: 4px 0">{{ record.content }}</p>
                <p style="margin: 4px 0; color: #6b7280">{{ $t('profile.participant', { value: record.participant }) }}</p>
                <p v-if="record.result" style="margin: 4px 0; color: #6b7280">{{ $t('profile.result', { value: record.result }) }}</p>
              </a-card>
            </a-timeline-item>
          </a-timeline>
          <a-empty v-if="!profile?.interactionRecords?.length" :description="$t('profile.noInteractions')" />
        </a-tab-pane>

        <!-- 任务调度 -->
        <a-tab-pane key="tasks" :tab="$t('profile.tabTasks')">
          <a-card size="small" style="margin-bottom: 16px">
            <template #title>{{ $t('profile.newTask') }}</template>
            <a-form :model="taskForm" layout="inline">
              <a-form-item :label="$t('profile.taskName')"><a-input v-model:value="taskForm.taskName" :placeholder="$t('profile.taskNamePlaceholder')" /></a-form-item>
              <a-form-item :label="$t('profile.taskType')">
                <a-select v-model:value="taskForm.taskType" style="width: 100px">
                  <a-select-option value="营销">{{ $t('profile.taskTypeMarketing') }}</a-select-option>
                  <a-select-option value="运营">{{ $t('profile.taskTypeOperation') }}</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item :label="$t('profile.assignee')"><a-input v-model:value="taskForm.assigneeName" :placeholder="$t('profile.assigneePlaceholder')" /></a-form-item>
              <a-form-item>
                <a-button type="primary" @click="handleCreateTask" :loading="taskCreating">{{ $t('profile.createTask') }}</a-button>
              </a-form-item>
            </a-form>
          </a-card>
        </a-tab-pane>

        <!-- AI 洞察 -->
        <a-tab-pane key="ai" :tab="$t('profile.tabAI')">
          <div style="margin-bottom: 12px; text-align: right">
            <a-button type="primary" @click="generateInsight" :loading="generating">{{ $t('profile.generateInsight') }}</a-button>
          </div>
          <div v-if="insight">
            <h4>{{ insight.title }}</h4>
            <a-card size="small" style="margin-bottom: 8px"><template #title>{{ $t('profile.industryOverview') }}</template><p>{{ insight.industryOverview }}</p></a-card>
            <a-card size="small" style="margin-bottom: 8px"><template #title>{{ $t('profile.customerAnalysis') }}</template><p>{{ insight.customerAnalysis }}</p></a-card>
            <a-card size="small" style="margin-bottom: 8px"><template #title>{{ $t('profile.competitiveLandscape') }}</template><p>{{ insight.competitiveLandscape }}</p></a-card>
            <a-card size="small" style="margin-bottom: 8px"><template #title>{{ $t('profile.opportunities') }}</template><p>{{ insight.opportunitiesAndSuggestions }}</p></a-card>
            <a-card size="small" style="margin-bottom: 8px"><template #title>{{ $t('profile.riskAlerts') }}</template><p>{{ insight.riskAlerts }}</p></a-card>
            <p style="color: #9ca3af; font-size: 12px">{{ $t('profile.generatedTime', { time: insight.generatedTime }) }}</p>
          </div>
          <a-empty v-else :description="$t('profile.aiTip')" />
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 新增政策弹窗 -->
    <a-modal v-model:open="policyDialogVisible" :title="$t('profile.policyDialogTitle')" @ok="handleSavePolicy" @cancel="policyDialogVisible = false" width="600px">
      <a-form :model="policyForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item :label="$t('profile.policyTitle')" required><a-input v-model:value="policyForm.policyTitle" /></a-form-item>
        <a-form-item :label="$t('profile.policyType')">
          <a-select v-model:value="policyForm.policyType" :placeholder="$t('common.selectPlease')">
            <a-select-option value="产业">{{ $t('profile.policyTypeIndustry') }}</a-select-option>
            <a-select-option value="财税">{{ $t('profile.policyTypeFinance') }}</a-select-option>
            <a-select-option value="贸易">{{ $t('profile.policyTypeTrade') }}</a-select-option>
            <a-select-option value="金融">{{ $t('profile.policyTypeFinancial') }}</a-select-option>
            <a-select-option value="其他">{{ $t('profile.policyTypeOther') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('profile.policyLevel')">
          <a-select v-model:value="policyForm.policyLevel" :placeholder="$t('common.selectPlease')">
            <a-select-option value="国家">{{ $t('profile.policyLevelNational') }}</a-select-option>
            <a-select-option value="省级">{{ $t('profile.policyLevelProvincial') }}</a-select-option>
            <a-select-option value="市级">{{ $t('profile.policyLevelMunicipal') }}</a-select-option>
            <a-select-option value="行业">{{ $t('profile.policyLevelIndustry') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('profile.issuingAuthority')"><a-input v-model:value="policyForm.issuingAuthority" /></a-form-item>
        <a-form-item :label="$t('profile.policySummary')"><a-textarea v-model:value="policyForm.policySummary" :rows="3" /></a-form-item>
        <a-form-item :label="$t('profile.impactAnalysis')"><a-textarea v-model:value="policyForm.impactAnalysis" :rows="2" /></a-form-item>
      </a-form>
    </a-modal>

    <!-- 新增订单弹窗 -->
    <a-modal v-model:open="orderDialogVisible" :title="$t('profile.orderDialogTitle')" @ok="handleSaveOrder" @cancel="orderDialogVisible = false" width="600px">
      <a-form :model="orderForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item :label="$t('common.projectName')" required><a-input v-model:value="orderForm.projectName" /></a-form-item>
        <a-form-item :label="$t('profile.projectType')">
          <a-select v-model:value="orderForm.projectType" :placeholder="$t('common.selectPlease')">
            <a-select-option value="信息化">{{ $t('profile.projectTypeIT') }}</a-select-option>
            <a-select-option value="基建">{{ $t('profile.projectTypeInfra') }}</a-select-option>
            <a-select-option value="咨询">{{ $t('profile.projectTypeConsulting') }}</a-select-option>
            <a-select-option value="运维">{{ $t('profile.projectTypeOM') }}</a-select-option>
            <a-select-option value="其他">{{ $t('profile.projectTypeOther') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('profile.orderAmount')"><a-input-number v-model:value="orderForm.orderAmount" :min="0" style="width: 100%" /></a-form-item>
        <a-form-item :label="$t('profile.orderStatus')">
          <a-select v-model:value="orderForm.orderStatus" :placeholder="$t('common.selectPlease')">
            <a-select-option value="洽谈中">{{ $t('profile.orderStatusNegotiating') }}</a-select-option>
            <a-select-option value="已签约">{{ $t('profile.orderStatusSigned') }}</a-select-option>
            <a-select-option value="执行中">{{ $t('profile.orderStatusExecuting') }}</a-select-option>
            <a-select-option value="已交付">{{ $t('profile.orderStatusDelivered') }}</a-select-option>
            <a-select-option value="已验收">{{ $t('profile.orderStatusAccepted') }}</a-select-option>
            <a-select-option value="已终止">{{ $t('profile.orderStatusTerminated') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('common.description')"><a-textarea v-model:value="orderForm.description" :rows="3" /></a-form-item>
      </a-form>
    </a-modal>

    <!-- 新增产品/方案弹窗 -->
    <a-modal v-model:open="productDialogVisible" :title="$t('profile.productDialogTitle')" @ok="handleSaveProduct" @cancel="productDialogVisible = false" width="600px">
      <a-form :model="productForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item :label="$t('common.name')" required><a-input v-model:value="productForm.productName" /></a-form-item>
        <a-form-item :label="$t('profile.productType')">
          <a-select v-model:value="productForm.productType" :placeholder="$t('common.selectPlease')">
            <a-select-option value="产品">{{ $t('profile.productTypeProduct') }}</a-select-option>
            <a-select-option value="解决方案">{{ $t('profile.productTypeSolution') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('profile.category')">
          <a-select v-model:value="productForm.category" :placeholder="$t('common.selectPlease')">
            <a-select-option value="云计算">{{ $t('profile.categoryCloud') }}</a-select-option>
            <a-select-option value="大数据">{{ $t('profile.categoryBigData') }}</a-select-option>
            <a-select-option value="AI">{{ $t('profile.categoryAI') }}</a-select-option>
            <a-select-option value="安全">{{ $t('profile.categorySecurity') }}</a-select-option>
            <a-select-option value="网络">{{ $t('profile.categoryNetwork') }}</a-select-option>
            <a-select-option value="办公">{{ $t('profile.categoryOffice') }}</a-select-option>
            <a-select-option value="其他">{{ $t('profile.categoryOther') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('profile.usageStatus')">
          <a-select v-model:value="productForm.usageStatus" :placeholder="$t('common.selectPlease')">
            <a-select-option value="使用中">{{ $t('profile.usageInUse') }}</a-select-option>
            <a-select-option value="已停用">{{ $t('profile.usageStopped') }}</a-select-option>
            <a-select-option value="评估中">{{ $t('profile.usageEvaluating') }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :label="$t('profile.annualFee')"><a-input-number v-model:value="productForm.annualFee" :min="0" style="width: 100%" /></a-form-item>
        <a-form-item :label="$t('common.description')"><a-textarea v-model:value="productForm.description" :rows="2" /></a-form-item>
      </a-form>
    </a-modal>
  </a-spin>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { useI18n } from 'vue-i18n'
import {
  getProfile, generateAIInsight, createTask,
  createPolicy, deletePolicy,
  createOrder, deleteOrder,
  createProduct, deleteProduct
} from '../../api/customerProfile'
import PageHeader from '../../components/PageHeader.vue'

const { t } = useI18n()
const route = useRoute()
const loading = ref(false)
const generating = ref(false)
const taskCreating = ref(false)
const profile = ref(null)
const branches = ref([])
const insight = ref(null)
const activeTab = ref('branches')

// ---- 表格列定义 ----
const policyColumns = computed(() => [
  { title: t('profile.policyTitle'), dataIndex: 'policyTitle', key: 'policyTitle', width: 200 },
  { title: t('common.type'), dataIndex: 'policyType', key: 'policyType', width: 80 },
  { title: t('profile.policyLevel'), dataIndex: 'policyLevel', key: 'policyLevel', width: 80 },
  { title: t('profile.issuingAuthority'), dataIndex: 'issuingAuthority', key: 'issuingAuthority', width: 150 },
  { title: t('common.status'), dataIndex: 'status', key: 'status', width: 80 },
  { title: t('common.action'), key: 'action', width: 80 },
])
const orderColumns = computed(() => [
  { title: t('common.projectName'), dataIndex: 'projectName', key: 'projectName', width: 200 },
  { title: t('common.type'), dataIndex: 'projectType', key: 'projectType', width: 80 },
  { title: t('profile.amount'), dataIndex: 'orderAmount', key: 'orderAmount', width: 100 },
  { title: t('common.status'), dataIndex: 'orderStatus', key: 'orderStatus', width: 100 },
  { title: t('common.action'), key: 'action', width: 80 },
])
const productColumns = computed(() => [
  { title: t('common.name'), dataIndex: 'productName', key: 'productName', width: 180 },
  { title: t('common.type'), dataIndex: 'productType', key: 'productType', width: 80 },
  { title: t('profile.category'), dataIndex: 'category', key: 'category', width: 80 },
  { title: t('common.status'), dataIndex: 'usageStatus', key: 'usageStatus', width: 80 },
  { title: t('profile.annualFee'), dataIndex: 'annualFee', key: 'annualFee', width: 100 },
  { title: t('common.action'), key: 'action', width: 80 },
])
const decisionColumns = computed(() => [
  { title: t('common.projectName'), dataIndex: 'projectName', key: 'projectName', width: 150 },
  { title: t('decisionChain.decisionMakerName'), dataIndex: 'decisionMakerName', key: 'decisionMakerName', width: 120 },
  { title: t('common.position'), dataIndex: 'decisionMakerPosition', key: 'decisionMakerPosition', width: 150 },
  { title: t('decisionChain.decisionStage'), dataIndex: 'decisionStage', key: 'decisionStage', width: 120 },
  { title: t('decisionChain.influenceLevel'), dataIndex: 'influenceLevel', key: 'influenceLevel', width: 100 },
])

// ---- 表单 ----
const policyDialogVisible = ref(false)
const policyForm = ref({})
const orderDialogVisible = ref(false)
const orderForm = ref({})
const productDialogVisible = ref(false)
const productForm = ref({})
const taskForm = ref({ taskName: '', taskType: '营销', assigneeName: '' })

// ---- 数据加载 ----
const loadData = async () => {
  loading.value = true
  const res = await getProfile(route.params.id)
  profile.value = res.data
  branches.value = res.data?.branches || []
  loading.value = false
}

// ---- AI 洞察 ----
const generateInsight = async () => {
  generating.value = true
  const res = await generateAIInsight(route.params.id)
  insight.value = res.data
  generating.value = false
  message.success(t('profile.insightGenerated'))
}

// ---- 政策动态 ----
const showPolicyDialog = () => { policyForm.value = {}; policyDialogVisible.value = true }
const handleSavePolicy = async () => {
  await createPolicy(route.params.id, policyForm.value)
  message.success(t('profile.addSuccess'))
  policyDialogVisible.value = false
  loadData()
}
const handleDeletePolicy = (record) => {
  Modal.confirm({ title: t('profile.deleteConfirm'), okText: t('common.confirm'), cancelText: t('common.cancel'), onOk: async () => { await deletePolicy(route.params.id, record.id); message.success(t('profile.deleteSuccess')); loadData() } })
}

// ---- 历史订单 ----
const showOrderDialog = () => { orderForm.value = {}; orderDialogVisible.value = true }
const handleSaveOrder = async () => {
  await createOrder(route.params.id, orderForm.value)
  message.success(t('profile.addSuccess'))
  orderDialogVisible.value = false
  loadData()
}
const handleDeleteOrder = (record) => {
  Modal.confirm({ title: t('profile.deleteConfirm'), okText: t('common.confirm'), cancelText: t('common.cancel'), onOk: async () => { await deleteOrder(route.params.id, record.id); message.success(t('profile.deleteSuccess')); loadData() } })
}

// ---- 产品与方案 ----
const showProductDialog = () => { productForm.value = {}; productDialogVisible.value = true }
const handleSaveProduct = async () => {
  await createProduct(route.params.id, productForm.value)
  message.success(t('profile.addSuccess'))
  productDialogVisible.value = false
  loadData()
}
const handleDeleteProduct = (record) => {
  Modal.confirm({ title: t('profile.deleteConfirm'), okText: t('common.confirm'), cancelText: t('common.cancel'), onOk: async () => { await deleteProduct(route.params.id, record.id); message.success(t('profile.deleteSuccess')); loadData() } })
}

// ---- 任务调度 ----
const handleCreateTask = async () => {
  if (!taskForm.value.taskName) { message.warning(t('profile.taskNameRequired')); return }
  taskCreating.value = true
  await createTask(route.params.id, taskForm.value)
  message.success(t('profile.taskCreated'))
  taskForm.value = { taskName: '', taskType: '营销', assigneeName: '' }
  taskCreating.value = false
}

onMounted(loadData)
</script>
