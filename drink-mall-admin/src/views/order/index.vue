<template>
  <div>
    <el-card>
      <template #header>
        <el-row :gutter="20">
          <el-col :span="6"><el-input v-model="search.orderNo" placeholder="订单号" clearable /></el-col>
          <el-col :span="4"><el-select v-model="search.status" placeholder="状态" clearable>
            <el-option label="待付款" value="pending" />
            <el-option label="已支付" value="paid" />
            <el-option label="已发货" value="shipped" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select></el-col>
          <el-col :span="4"><el-button type="primary" @click="loadData">搜索</el-button></el-col>
        </el-row>
      </template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="payAmount" label="金额" width="100"><template #default="{ row }">¥{{ row.payAmount }}</template></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待付款</el-tag>
            <el-tag v-else-if="row.status === 'paid'" type="primary">已支付</el-tag>
            <el-tag v-else-if="row.status === 'shipped'">已发货</el-tag>
            <el-tag v-else-if="row.status === 'completed'" type="success">已完成</el-tag>
            <el-tag v-else type="info">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/order/${row.orderId}`)">详情</el-button>
            <el-button link type="success" v-if="row.status === 'paid'" @click="handleShip(row)">发货</el-button>
            <el-button link type="warning" v-if="row.status === 'pending'" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
    </el-card>
    <el-dialog v-model="shipDialogVisible" title="发货" width="400px">
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="物流公司"><el-input v-model="shipForm.logisticsCompany" /></el-form-item>
        <el-form-item label="物流单号"><el-input v-model="shipForm.logisticsNo" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确定发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref({ orderNo: '', status: '' })
const shipDialogVisible = ref(false)
const currentOrder = ref<any>(null)
const shipForm = ref({ logisticsCompany: '', logisticsNo: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/order/list', { params: { ...search.value, page: page.value } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const handleShip = (row: any) => { currentOrder.value = row; shipDialogVisible.value = true }

const confirmShip = async () => {
  await request.put(`/api/v1/admin/order/${currentOrder.value.orderId}/ship`, null, { params: shipForm.value })
  ElMessage.success('发货成功')
  shipDialogVisible.value = false
  loadData()
}

const handleCancel = async (row: any) => {
  await request.put(`/api/v1/admin/order/${row.orderId}/cancel`, null, { params: { reason: '管理员取消' } })
  ElMessage.success('取消成功')
  loadData()
}

onMounted(loadData)
</script>