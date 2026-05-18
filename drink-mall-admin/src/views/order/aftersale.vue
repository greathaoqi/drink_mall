<template>
  <el-card>
    <template #header>
      <el-row :gutter="12">
        <el-col :span="4">
          <el-select v-model="search.status" placeholder="状态" clearable>
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="线下已处理" value="offline_processed" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已关闭" value="closed" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-col>
        <el-col :span="4"><el-button type="primary" :loading="loading" @click="loadData">查询</el-button></el-col>
      </el-row>
    </template>

    <el-table :data="tableData" v-loading="loading" empty-text="暂无售后申请">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderId" label="订单ID" width="100" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="reason" label="申请原因" min-width="180" />
      <el-table-column prop="refundAmount" label="退款金额" width="110">
        <template #default="{ row }">￥{{ row.refundAmount || 0 }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="adminRemark" label="审核备注" min-width="180" />
      <el-table-column prop="offlineProcessResult" label="线下处理结果" min-width="180" />
      <el-table-column prop="createdAt" label="申请时间" width="180" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button link type="success" v-if="can('aftersale:write') && row.status === 'pending'" @click="handleApprove(row)">通过</el-button>
          <el-button link type="danger" v-if="can('aftersale:write') && row.status === 'pending'" @click="handleReject(row)">拒绝</el-button>
          <el-button link type="primary" v-if="can('aftersale:write') && row.status === 'approved'" @click="handleOffline(row)">记录处理</el-button>
          <el-button link type="success" v-if="can('aftersale:write') && row.status === 'offline_processed'" @click="handleComplete(row)">完成</el-button>
          <el-button link type="warning" v-if="can('aftersale:write') && ['pending', 'approved', 'offline_processed'].includes(row.status)" @click="handleClose(row)">关闭</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { can, confirmCritical, promptReason } from '@/utils/adminAction'

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref({ status: '' })

const statusText = (status: string) => ({
  pending: '待审核',
  approved: '已通过',
  offline_processed: '线下已处理',
  rejected: '已拒绝',
  closed: '已关闭',
  completed: '已完成'
}[status] || status || '-')

const statusType = (status: string) => ({
  approved: 'success',
  offline_processed: 'primary',
  completed: 'success',
  rejected: 'danger',
  closed: 'info',
  pending: 'warning'
}[status] || 'info')

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/order/aftersale/list', { params: { ...search.value, page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row: any) => {
  const result = await ElMessageBox.prompt('请输入审核通过原因和线下处理要求', '售后审核通过', {
    inputType: 'textarea',
    inputPattern: /\S+/,
    inputErrorMessage: '操作原因必填'
  })
  await confirmCritical('确认通过该售后申请？该操作会回滚相关奖励并记录日志。', '售后审核确认')
  await request.put(`/api/v1/admin/order/aftersale/${row.id}/approve`, null, { params: { remark: result.value.trim() } })
  ElMessage.success('售后申请已通过')
  loadData()
}

const handleReject = async (row: any) => {
  const reason = await promptReason('售后拒绝', '请输入拒绝原因')
  await request.put(`/api/v1/admin/order/aftersale/${row.id}/reject`, null, { params: { reason } })
  ElMessage.success('售后申请已拒绝')
  loadData()
}

const handleOffline = async (row: any) => {
  const result = await promptReason('记录线下处理', '请输入退款单号、换货说明或补偿说明')
  await request.put(`/api/v1/admin/order/aftersale/${row.id}/offline-result`, null, { params: { result } })
  ElMessage.success('线下处理结果已记录')
  loadData()
}

const handleComplete = async (row: any) => {
  const remark = await promptReason('完成售后', '请输入完成售后的原因或凭证')
  await confirmCritical('确认该售后已处理完成？', '售后完成确认')
  await request.put(`/api/v1/admin/order/aftersale/${row.id}/complete`, null, { params: { remark } })
  ElMessage.success('售后已完成')
  loadData()
}

const handleClose = async (row: any) => {
  const reason = await promptReason('关闭售后', '请输入关闭原因')
  await confirmCritical('确认关闭该售后申请？', '售后关闭确认')
  await request.put(`/api/v1/admin/order/aftersale/${row.id}/close`, null, { params: { reason } })
  ElMessage.success('售后已关闭')
  loadData()
}

onMounted(loadData)
</script>
