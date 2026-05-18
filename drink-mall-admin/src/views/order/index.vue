<template>
  <el-card>
    <template #header>
      <el-row :gutter="12">
        <el-col :span="6"><el-input v-model="search.orderNo" placeholder="订单号" clearable @keyup.enter="loadData" /></el-col>
        <el-col :span="4">
          <el-select v-model="search.status" placeholder="状态" clearable>
            <el-option label="待支付" value="pending" />
            <el-option label="已支付" value="paid" />
            <el-option label="已发货" value="shipped" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-col>
        <el-col :span="4"><el-button type="primary" :loading="loading" @click="loadData">查询</el-button></el-col>
      </el-row>
    </template>

    <el-table :data="tableData" v-loading="loading" empty-text="暂无订单">
      <el-table-column prop="orderNo" label="订单号" min-width="180" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="payAmount" label="实付金额" width="110"><template #default="{ row }">¥{{ row.payAmount }}</template></el-table-column>
      <el-table-column prop="paymentMethod" label="支付方式" width="110" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }"><el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="logisticsCompany" label="物流公司" width="130" />
      <el-table-column prop="logisticsNo" label="物流单号" min-width="150" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/order/${row.orderId || row.id}`)">详情</el-button>
          <el-button link type="success" v-if="can('order:write') && row.status === 'paid'" @click="handleShip(row)">发货</el-button>
          <el-button link type="primary" v-if="can('order:write') && row.status === 'pending' && row.paymentMethod === 'offline_corporate'" @click="handleOfflineConfirm(row)">线下确认</el-button>
          <el-button link type="warning" v-if="can('order:write') && ['pending', 'paid'].includes(row.status)" @click="handleCancel(row)">取消</el-button>
          <el-button link type="success" v-if="can('order:write') && row.status === 'shipped'" @click="handleComplete(row)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />

    <el-dialog v-model="shipDialogVisible" title="手动标记发货" width="440px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="物流公司" required><el-input v-model="shipForm.logisticsCompany" /></el-form-item>
        <el-form-item label="物流单号" required><el-input v-model="shipForm.logisticsNo" /></el-form-item>
        <el-form-item label="操作原因" required><el-input v-model="shipForm.reason" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { can, confirmCritical, promptReason, requireText } from '@/utils/adminAction'

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref({ orderNo: '', status: '' })
const shipDialogVisible = ref(false)
const currentOrder = ref<any>(null)
const shipForm = ref({ logisticsCompany: '', logisticsNo: '', reason: '' })

const statusText = (status: string) => ({ pending: '待支付', paid: '已支付', shipped: '已发货', completed: '已完成', cancelled: '已取消', closed: '已关闭' }[status] || status || '-')
const statusType = (status: string) => ({ pending: 'warning', paid: 'primary', shipped: 'primary', completed: 'success', cancelled: 'info', closed: 'info' }[status] || 'info')
const orderId = (row: any) => row.orderId || row.id

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/order/list', { params: { ...search.value, page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleShip = (row: any) => {
  currentOrder.value = row
  shipForm.value = { logisticsCompany: '', logisticsNo: '', reason: '' }
  shipDialogVisible.value = true
}

const confirmShip = async () => {
  if (!requireText(shipForm.value.logisticsCompany, '物流公司') || !requireText(shipForm.value.logisticsNo, '物流单号') || !requireText(shipForm.value.reason, '操作原因')) {
    return
  }
  await confirmCritical('确认手动标记该订单为已发货？该操作会记录操作日志。', '订单发货确认')
  await request.put(`/api/v1/admin/order/${orderId(currentOrder.value)}/ship`, null, { params: shipForm.value })
  ElMessage.success('订单已发货')
  shipDialogVisible.value = false
  loadData()
}

const handleCancel = async (row: any) => {
  const reason = await promptReason('取消订单', '请输入取消原因')
  await request.put(`/api/v1/admin/order/${orderId(row)}/cancel`, null, { params: { reason } })
  ElMessage.success('订单已取消')
  loadData()
}

const handleComplete = async (row: any) => {
  const reason = await promptReason('完成订单', '请输入提前完成订单的原因')
  await confirmCritical('确认提前完成订单？如涉及售后冻结期，请确保业务允许。', '订单完成确认')
  await request.put(`/api/v1/admin/order/${orderId(row)}/complete`, null, { params: { reason } })
  ElMessage.success('订单已完成')
  loadData()
}

const handleOfflineConfirm = async (row: any) => {
  const paymentNoResult = await ElMessageBox.prompt('请输入线下打款流水号', '线下转账确认', {
    inputPattern: /\S+/,
    inputErrorMessage: '打款流水号必填'
  })
  const reason = await promptReason('线下转账确认', '请输入确认线下到账的原因')
  await confirmCritical(`确认订单 ${row.orderNo} 已线下到账？`, '财务确认')
  await request.put(`/api/v1/admin/order/${orderId(row)}/offline-confirm`, null, {
    params: { paymentNo: paymentNoResult.value.trim(), reason }
  })
  ElMessage.success('线下付款已确认')
  loadData()
}

onMounted(loadData)
</script>
