<template>
  <a-spin :spinning="loading">
    <a-page-header title="客户360度画像" @back="$router.back()" style="margin-bottom: 20px" />

    <!-- 基础信息 -->
    <a-card v-if="profile" style="margin-bottom: 20px">
      <template #title>基础信息 - {{ profile.basicInfo?.customerName }}</template>
      <a-descriptions :column="3">
        <a-descriptions-item label="客户编号">{{ profile.basicInfo?.customerCode }}</a-descriptions-item>
        <a-descriptions-item label="客户名称">{{ profile.basicInfo?.customerName }}</a-descriptions-item>
        <a-descriptions-item label="英文名称">{{ profile.basicInfo?.customerNameEn }}</a-descriptions-item>
        <a-descriptions-item label="行业">{{ profile.basicInfo?.industry }}</a-descriptions-item>
        <a-descriptions-item label="区域">{{ profile.basicInfo?.region }}</a-descriptions-item>
        <a-descriptions-item label="规模">{{ profile.basicInfo?.scale }}</a-descriptions-item>
        <a-descriptions-item label="看管状态">{{ profile.managementInfo?.statusDesc }}</a-descriptions-item>
        <a-descriptions-item label="客户经理">{{ profile.managementInfo?.managerName }}</a-descriptions-item>
        <a-descriptions-item label="入库时间">{{ profile.managementInfo?.joinTime }}</a-descriptions-item>
      </a-descriptions>
    </a-card>

    <!-- 业务数据 -->
    <a-card v-if="profile" style="margin-bottom: 20px">
      <template #title>业务数据概览</template>
      <a-row :gutter="20">
        <a-col :span="4"><a-statistic title="招标次数" :value="profile?.businessInfo?.bidCount || 0" /></a-col>
        <a-col :span="4"><a-statistic title="招标总金额(万)" :value="profile?.businessInfo?.bidAmount || 0" /></a-col>
        <a-col :span="4"><a-statistic title="信息化招标数" :value="profile?.businessInfo?.itBidCount || 0" /></a-col>
        <a-col :span="4"><a-statistic title="订单数量" :value="profile?.businessInfo?.orderCount || 0" /></a-col>
        <a-col :span="4"><a-statistic title="收入贡献(万)" :value="profile?.businessInfo?.revenueContribution || 0" /></a-col>
        <a-col :span="4"><a-statistic title="产品/方案数" :value="profile?.businessInfo?.productCount || 0" /></a-col>
      </a-row>
    </a-card>

    <!-- 多Tab内容区 -->
    <a-card v-if="profile">
      <a-tabs v-model:activeKey="activeTab">
        <!-- 分支组织架构 -->
        <a-tab-pane key="branches" tab="分支组织架构">
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
          <a-empty v-else description="暂无分支数据" />
        </a-tab-pane>

        <!-- 政策动态 -->
        <a-tab-pane key="policies" tab="政策动态">
          <div style="margin-bottom: 12px; text-align: right">
            <a-button type="primary" size="small" @click="showPolicyDialog">新增政策</a-button>
          </div>
          <a-table :columns="policyColumns" :data-source="profile.policies || []" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === '有效' ? 'success' : 'default'">{{ record.status }}</a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button size="small" danger @click="handleDeletePolicy(record)">删除</a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- 历史订单/项目 -->
        <a-tab-pane key="orders" tab="历史订单/项目">
          <div style="margin-bottom: 12px; text-align: right">
            <a-button type="primary" size="small" @click="showOrderDialog">新增订单</a-button>
          </div>
          <a-table :columns="orderColumns" :data-source="profile.orders || []" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'orderStatus'">
                <a-tag :color="record.orderStatus === '已验收' ? 'success' : record.orderStatus === '执行中' ? 'processing' : 'default'">{{ record.orderStatus }}</a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button size="small" danger @click="handleDeleteOrder(record)">删除</a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- 产品与方案 -->
        <a-tab-pane key="products" tab="产品与方案">
          <div style="margin-bottom: 12px; text-align: right">
            <a-button type="primary" size="small" @click="showProductDialog">新增产品/方案</a-button>
          </div>
          <a-table :columns="productColumns" :data-source="profile.products || []" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'usageStatus'">
                <a-tag :color="record.usageStatus === '使用中' ? 'success' : record.usageStatus === '评估中' ? 'warning' : 'default'">{{ record.usageStatus }}</a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-button size="small" danger @click="handleDeleteProduct(record)">删除</a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <!-- 决策链与关键人 -->
        <a-tab-pane key="decisions" tab="决策链与关键人">
          <a-empty v-if="!profile?.decisionChains?.length" description="暂无决策链数据" />
          <a-table v-else :columns="decisionColumns" :data-source="profile.decisionChains" :pagination="false" row-key="id" size="small" />
        </a-tab-pane>

        <!-- 交互记录 -->
        <a-tab-pane key="interactions" tab="交互记录">
          <a-timeline>
            <a-timeline-item v-for="record in profile?.interactionRecords" :key="record.id" color="blue">
              <a-card size="small">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px">
                  <a-tag>{{ record.type }}</a-tag>
                  <span style="color: #9ca3af; font-size: 12px; white-space: nowrap">{{ record.time }}</span>
                </div>
                <p style="margin: 4px 0">{{ record.content }}</p>
                <p style="margin: 4px 0; color: #6b7280">参与人：{{ record.participant }}</p>
                <p v-if="record.result" style="margin: 4px 0; color: #6b7280">结果：{{ record.result }}</p>
              </a-card>
            </a-timeline-item>
          </a-timeline>
          <a-empty v-if="!profile?.interactionRecords?.length" description="暂无交互记录" />
        </a-tab-pane>

        <!-- 任务调度 -->
        <a-tab-pane key="tasks" tab="任务调度">
          <a-card size="small" style="margin-bottom: 16px">
            <template #title>发起新任务</template>
            <a-form :model="taskForm" layout="inline">
              <a-form-item label="任务名称"><a-input v-model:value="taskForm.taskName" placeholder="请输入任务名称" /></a-form-item>
              <a-form-item label="类型">
                <a-select v-model:value="taskForm.taskType" style="width: 100px">
                  <a-select-option value="营销">营销</a-select-option>
                  <a-select-option value="运营">运营</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="执行人"><a-input v-model:value="taskForm.assigneeName" placeholder="执行人" /></a-form-item>
              <a-form-item>
                <a-button type="primary" @click="handleCreateTask" :loading="taskCreating">创建任务</a-button>
              </a-form-item>
            </a-form>
          </a-card>
        </a-tab-pane>

        <!-- AI 洞察 -->
        <a-tab-pane key="ai" tab="AI行业洞察">
          <div style="margin-bottom: 12px; text-align: right">
            <a-button type="primary" @click="generateInsight" :loading="generating">生成洞察报告</a-button>
          </div>
          <div v-if="insight">
            <h4>{{ insight.title }}</h4>
            <a-card size="small" style="margin-bottom: 8px"><template #title>行业概况</template><p>{{ insight.industryOverview }}</p></a-card>
            <a-card size="small" style="margin-bottom: 8px"><template #title>客户分析</template><p>{{ insight.customerAnalysis }}</p></a-card>
            <a-card size="small" style="margin-bottom: 8px"><template #title>竞争格局</template><p>{{ insight.competitiveLandscape }}</p></a-card>
            <a-card size="small" style="margin-bottom: 8px"><template #title>机会与建议</template><p>{{ insight.opportunitiesAndSuggestions }}</p></a-card>
            <a-card size="small" style="margin-bottom: 8px"><template #title>风险提示</template><p>{{ insight.riskAlerts }}</p></a-card>
            <p style="color: #9ca3af; font-size: 12px">生成时间：{{ insight.generatedTime }}</p>
          </div>
          <a-empty v-else description="点击生成按钮获取AI洞察" />
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 新增政策弹窗 -->
    <a-modal v-model:open="policyDialogVisible" title="新增政策动态" @ok="handleSavePolicy" @cancel="policyDialogVisible = false" width="600px">
      <a-form :model="policyForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="政策标题" required><a-input v-model:value="policyForm.policyTitle" /></a-form-item>
        <a-form-item label="政策类型">
          <a-select v-model:value="policyForm.policyType" placeholder="请选择">
            <a-select-option value="产业">产业</a-select-option>
            <a-select-option value="财税">财税</a-select-option>
            <a-select-option value="贸易">贸易</a-select-option>
            <a-select-option value="金融">金融</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="政策级别">
          <a-select v-model:value="policyForm.policyLevel" placeholder="请选择">
            <a-select-option value="国家">国家</a-select-option>
            <a-select-option value="省级">省级</a-select-option>
            <a-select-option value="市级">市级</a-select-option>
            <a-select-option value="行业">行业</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="发布机构"><a-input v-model:value="policyForm.issuingAuthority" /></a-form-item>
        <a-form-item label="政策摘要"><a-textarea v-model:value="policyForm.policySummary" :rows="3" /></a-form-item>
        <a-form-item label="影响分析"><a-textarea v-model:value="policyForm.impactAnalysis" :rows="2" /></a-form-item>
      </a-form>
    </a-modal>

    <!-- 新增订单弹窗 -->
    <a-modal v-model:open="orderDialogVisible" title="新增历史订单/项目" @ok="handleSaveOrder" @cancel="orderDialogVisible = false" width="600px">
      <a-form :model="orderForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="项目名称" required><a-input v-model:value="orderForm.projectName" /></a-form-item>
        <a-form-item label="项目类型">
          <a-select v-model:value="orderForm.projectType" placeholder="请选择">
            <a-select-option value="信息化">信息化</a-select-option>
            <a-select-option value="基建">基建</a-select-option>
            <a-select-option value="咨询">咨询</a-select-option>
            <a-select-option value="运维">运维</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="订单金额(万)"><a-input-number v-model:value="orderForm.orderAmount" :min="0" style="width: 100%" /></a-form-item>
        <a-form-item label="订单状态">
          <a-select v-model:value="orderForm.orderStatus" placeholder="请选择">
            <a-select-option value="洽谈中">洽谈中</a-select-option>
            <a-select-option value="已签约">已签约</a-select-option>
            <a-select-option value="执行中">执行中</a-select-option>
            <a-select-option value="已交付">已交付</a-select-option>
            <a-select-option value="已验收">已验收</a-select-option>
            <a-select-option value="已终止">已终止</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="项目描述"><a-textarea v-model:value="orderForm.description" :rows="3" /></a-form-item>
      </a-form>
    </a-modal>

    <!-- 新增产品/方案弹窗 -->
    <a-modal v-model:open="productDialogVisible" title="新增产品/方案" @ok="handleSaveProduct" @cancel="productDialogVisible = false" width="600px">
      <a-form :model="productForm" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="名称" required><a-input v-model:value="productForm.productName" /></a-form-item>
        <a-form-item label="类型">
          <a-select v-model:value="productForm.productType" placeholder="请选择">
            <a-select-option value="产品">产品</a-select-option>
            <a-select-option value="解决方案">解决方案</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="分类">
          <a-select v-model:value="productForm.category" placeholder="请选择">
            <a-select-option value="云计算">云计算</a-select-option>
            <a-select-option value="大数据">大数据</a-select-option>
            <a-select-option value="AI">AI</a-select-option>
            <a-select-option value="安全">安全</a-select-option>
            <a-select-option value="网络">网络</a-select-option>
            <a-select-option value="办公">办公</a-select-option>
            <a-select-option value="其他">其他</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="使用状态">
          <a-select v-model:value="productForm.usageStatus" placeholder="请选择">
            <a-select-option value="使用中">使用中</a-select-option>
            <a-select-option value="已停用">已停用</a-select-option>
            <a-select-option value="评估中">评估中</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="年费(万)"><a-input-number v-model:value="productForm.annualFee" :min="0" style="width: 100%" /></a-form-item>
        <a-form-item label="描述"><a-textarea v-model:value="productForm.description" :rows="2" /></a-form-item>
      </a-form>
    </a-modal>
  </a-spin>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  getProfile, generateAIInsight, createTask,
  createPolicy, deletePolicy,
  createOrder, deleteOrder,
  createProduct, deleteProduct
} from '../../api/customerProfile'

const route = useRoute()
const loading = ref(false)
const generating = ref(false)
const taskCreating = ref(false)
const profile = ref(null)
const branches = ref([])
const insight = ref(null)
const activeTab = ref('branches')

// ---- 表格列定义 ----
const policyColumns = [
  { title: '政策标题', dataIndex: 'policyTitle', key: 'policyTitle', width: 200 },
  { title: '类型', dataIndex: 'policyType', key: 'policyType', width: 80 },
  { title: '级别', dataIndex: 'policyLevel', key: 'policyLevel', width: 80 },
  { title: '发布机构', dataIndex: 'issuingAuthority', key: 'issuingAuthority', width: 150 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 80 },
  { title: '操作', key: 'action', width: 80 },
]
const orderColumns = [
  { title: '项目名称', dataIndex: 'projectName', key: 'projectName', width: 200 },
  { title: '类型', dataIndex: 'projectType', key: 'projectType', width: 80 },
  { title: '金额(万)', dataIndex: 'orderAmount', key: 'orderAmount', width: 100 },
  { title: '状态', dataIndex: 'orderStatus', key: 'orderStatus', width: 100 },
  { title: '操作', key: 'action', width: 80 },
]
const productColumns = [
  { title: '名称', dataIndex: 'productName', key: 'productName', width: 180 },
  { title: '类型', dataIndex: 'productType', key: 'productType', width: 80 },
  { title: '分类', dataIndex: 'category', key: 'category', width: 80 },
  { title: '状态', dataIndex: 'usageStatus', key: 'usageStatus', width: 80 },
  { title: '年费(万)', dataIndex: 'annualFee', key: 'annualFee', width: 100 },
  { title: '操作', key: 'action', width: 80 },
]
const decisionColumns = [
  { title: '项目名称', dataIndex: 'projectName', key: 'projectName', width: 150 },
  { title: '决策人', dataIndex: 'decisionMakerName', key: 'decisionMakerName', width: 120 },
  { title: '职位', dataIndex: 'decisionMakerPosition', key: 'decisionMakerPosition', width: 150 },
  { title: '决策阶段', dataIndex: 'decisionStage', key: 'decisionStage', width: 120 },
  { title: '影响力', dataIndex: 'influenceLevel', key: 'influenceLevel', width: 100 },
]

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
  message.success('AI洞察生成完成')
}

// ---- 政策动态 ----
const showPolicyDialog = () => { policyForm.value = {}; policyDialogVisible.value = true }
const handleSavePolicy = async () => {
  await createPolicy(route.params.id, policyForm.value)
  message.success('新增成功')
  policyDialogVisible.value = false
  loadData()
}
const handleDeletePolicy = (record) => {
  Modal.confirm({ title: '确认删除？', onOk: async () => { await deletePolicy(route.params.id, record.id); message.success('删除成功'); loadData() } })
}

// ---- 历史订单 ----
const showOrderDialog = () => { orderForm.value = {}; orderDialogVisible.value = true }
const handleSaveOrder = async () => {
  await createOrder(route.params.id, orderForm.value)
  message.success('新增成功')
  orderDialogVisible.value = false
  loadData()
}
const handleDeleteOrder = (record) => {
  Modal.confirm({ title: '确认删除？', onOk: async () => { await deleteOrder(route.params.id, record.id); message.success('删除成功'); loadData() } })
}

// ---- 产品与方案 ----
const showProductDialog = () => { productForm.value = {}; productDialogVisible.value = true }
const handleSaveProduct = async () => {
  await createProduct(route.params.id, productForm.value)
  message.success('新增成功')
  productDialogVisible.value = false
  loadData()
}
const handleDeleteProduct = (record) => {
  Modal.confirm({ title: '确认删除？', onOk: async () => { await deleteProduct(route.params.id, record.id); message.success('删除成功'); loadData() } })
}

// ---- 任务调度 ----
const handleCreateTask = async () => {
  if (!taskForm.value.taskName) { message.warning('请输入任务名称'); return }
  taskCreating.value = true
  await createTask(route.params.id, taskForm.value)
  message.success('任务创建成功')
  taskForm.value = { taskName: '', taskType: '营销', assigneeName: '' }
  taskCreating.value = false
}

onMounted(loadData)
</script>
