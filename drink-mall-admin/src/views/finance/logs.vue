<template>
  <el-card>
    <template #header>
      <el-row :gutter="12" align="middle">
        <el-col :span="4"><el-input v-model.number="search.userId" placeholder="用户ID" clearable /></el-col>
        <el-col :span="4">
          <el-select v-model="activeTab" @change="handleTabChange">
            <el-option label="余额明细" value="balance" />
            <el-option label="积分明细" value="points" />
            <el-option label="DF 明细" value="df" />
            <el-option label="酒豆明细" value="wine_bean" />
            <el-option label="期权明细" value="option" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" :loading="loading" @click="loadData">查询</el-button>
          <el-button type="warning" @click="openAdjust">手动资产调整</el-button>
        </el-col>
      </el-row>
    </template>

    <el-alert class="mb12" type="warning" show-icon :closable="false" title="财务和资产操作必须二次确认，手动调整必须填写原因并由后端记录流水与操作日志。" />

    <el-table :data="tableData" v-loading="loading" empty-text="暂无流水">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="assetType" label="资产类型" width="110">
        <template #default="{ row }">{{ assetText(row.assetType || activeTab) }}</template>
      </el-table-column>
      <el-table-column prop="changeType" label="变动类型" width="130" />
      <el-table-column prop="amount" label="变动数量" width="120">
        <template #default="{ row }"><span :class="Number(row.amount || row.points) >= 0 ? 'plus' : 'minus'">{{ signed(row.amount ?? row.points) }}</span></template>
      </el-table-column>
      <el-table-column prop="beforeBalance" label="变动前" width="120" />
      <el-table-column prop="afterBalance" label="变动后" width="120" />
      <el-table-column prop="remark" label="备注/原因" min-width="180" />
      <el-table-column prop="createdAt" label="时间" width="180" />
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />

    <el-dialog v-model="adjustDialogVisible" title="手动资产调整" width="520px">
      <el-alert title="请输入正数增加、负数减少；原因会进入审计记录。" type="warning" show-icon :closable="false" />
      <el-form :model="adjustForm" label-width="100px" class="dialog-form">
        <el-form-item label="用户ID" required><el-input-number v-model="adjustForm.userId" :min="1" /></el-form-item>
        <el-form-item label="资产类型" required>
          <el-select v-model="adjustForm.assetType">
            <el-option label="余额" value="balance" />
            <el-option label="DF" value="df" />
            <el-option label="酒豆" value="wine_bean" />
            <el-option label="积分" value="points" />
            <el-option label="期权" value="option" />
          </el-select>
        </el-form-item>
        <el-form-item label="调整数量" required><el-input-number v-model="adjustForm.amount" :precision="2" :step="1" /></el-form-item>
        <el-form-item label="调整原因" required><el-input v-model="adjustForm.reason" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitAdjust">二次确认并提交</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const activeTab = ref('balance')
const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref<{ userId?: number }>({ userId: undefined })
const adjustDialogVisible = ref(false)
const adjustForm = ref({ userId: 1, assetType: 'balance', amount: 0, reason: '' })

const assetText = (type: string) => ({ balance: '余额', points: '积分', df: 'DF', wine_bean: '酒豆', option: '期权' }[type] || type)
const signed = (value: unknown) => {
  const num = Number(value || 0)
  return `${num > 0 ? '+' : ''}${num}`
}

const loadData = async () => {
  if (['df', 'wine_bean', 'option'].includes(activeTab.value) && !search.value.userId) {
    tableData.value = []
    total.value = 0
    ElMessage.warning('DF、酒豆、期权明细需要先输入用户ID')
    return
  }
  loading.value = true
  try {
    const params = { userId: search.value.userId, page: page.value, size: 20 }
    const url = activeTab.value === 'points'
      ? '/api/v1/admin/finance/points-logs'
      : activeTab.value === 'balance'
        ? '/api/v1/admin/finance/balance-logs'
        : '/api/v1/admin/finance/asset-logs'
    const res = await request.get(url, {
      params: activeTab.value === 'df' || activeTab.value === 'wine_bean' || activeTab.value === 'option'
        ? { ...params, assetType: activeTab.value }
        : params
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  page.value = 1
  loadData()
}

const openAdjust = () => {
  adjustForm.value = { userId: search.value.userId || 1, assetType: activeTab.value, amount: 0, reason: '' }
  adjustDialogVisible.value = true
}

const submitAdjust = async () => {
  if (!adjustForm.value.reason.trim()) {
    ElMessage.warning('资产调整原因必填')
    return
  }
  if (!adjustForm.value.amount) {
    ElMessage.warning('调整数量不能为 0')
    return
  }
  await ElMessageBox.confirm(
    `确认对用户 ${adjustForm.value.userId} ${adjustForm.value.amount > 0 ? '增加' : '减少'} ${assetText(adjustForm.value.assetType)} ${Math.abs(adjustForm.value.amount)}？`,
    '财务二次确认',
    { type: 'warning' }
  )
  submitting.value = true
  try {
    await request.post('/api/v1/admin/finance/assets/adjust', adjustForm.value)
    ElMessage.success('资产调整已提交')
    adjustDialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.mb12 { margin-bottom: 12px; }
.dialog-form { margin-top: 16px; }
.plus { color: #16a34a; }
.minus { color: #dc2626; }
</style>
