<template>
  <el-card>
    <template #header>
      <el-row :gutter="12">
        <el-col :span="4">
          <el-select v-model="search.status" placeholder="状态" clearable>
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
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
      <el-table-column prop="refundAmount" label="退款金额" width="110"><template #default="{ row }">¥{{ row.refundAmount || 0 }}</template></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'approved' || row.status === 'completed' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reviewRemark" label="审核备注/线下处理结果" min-width="200" />
      <el-table-column prop="createdAt" label="申请时间" width="180" />
      <el-table-column label="操作" width="210" fixed="right">
        <template #default="{ row }">
          <el-button link type="success" v-if="row.status === 'pending'" @click="handleApprove(row)">通过</el-button>
          <el-button link type="danger" v-if="row.status === 'pending'" @click="handleReject(row)">拒绝</el-button>
          <el-button link type="primary" v-if="row.status === 'approved'" @click="handleOffline(row)">记录处理</el-button>
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

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref({ status: '' })

const statusText = (status: string) => ({ pending: '待审核', approved: '已通过', rejected: '已拒绝', closed: '已关闭', completed: '已完成' }[status] || status || '-')

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
  const result = await ElMessageBox.prompt('请输入审核备注或线下处理要求', '售后审核通过', {
    inputPattern: /.+/,
    inputErrorMessage: '审核备注必填'
  })
  await ElMessageBox.confirm('确认通过该售后申请？', '售后操作确认', { type: 'warning' })
  await request.put(`/api/v1/admin/order/aftersale/${row.id}/approve`, null, { params: { remark: result.value } })
  ElMessage.success('售后申请已通过')
  loadData()
}

const handleReject = async (row: any) => {
  const result = await ElMessageBox.prompt('请输入拒绝原因', '售后拒绝', {
    inputPattern: /.+/,
    inputErrorMessage: '拒绝原因必填'
  })
  await request.put(`/api/v1/admin/order/aftersale/${row.id}/reject`, null, { params: { reason: result.value } })
  ElMessage.success('售后申请已拒绝')
  loadData()
}

const handleOffline = async (row: any) => {
  ElMessage.info(`后端尚未提供售后 ${row.id} 的关闭/完成/线下处理结果接口，已保留入口。`)
}

onMounted(loadData)
</script>
