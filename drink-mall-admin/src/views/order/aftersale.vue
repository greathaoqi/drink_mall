<template>
  <div>
    <el-card>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'refund'">退款</el-tag>
            <el-tag v-else-if="row.type === 'return'" type="warning">退货</el-tag>
            <el-tag v-else type="info">换货</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" />
        <el-table-column prop="refundAmount" label="退款金额" width="100"><template #default="{ row }">¥{{ row.refundAmount }}</template></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待处理</el-tag>
            <el-tag v-else-if="row.status === 'approved'" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="success" v-if="row.status === 'pending'" @click="handleApprove(row.id)">通过</el-button>
            <el-button link type="danger" v-if="row.status === 'pending'" @click="handleReject(row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  const res = await request.get('/api/v1/admin/order/aftersale/list', { params: { page: page.value } })
  tableData.value = res.data?.records || []
  total.value = res.data?.total || 0
  loading.value = false
}

const handleApprove = async (id: number) => {
  await ElMessageBox.confirm('确定通过此售后申请?')
  await request.put(`/api/v1/admin/order/aftersale/${id}/approve`)
  ElMessage.success('操作成功')
  loadData()
}

const handleReject = async (id: number) => {
  const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝', { inputPattern: /.+/, inputErrorMessage: '请输入原因' })
  await request.put(`/api/v1/admin/order/aftersale/${id}/reject`, null, { params: { reason: value } })
  ElMessage.success('操作成功')
  loadData()
}

onMounted(loadData)
</script>