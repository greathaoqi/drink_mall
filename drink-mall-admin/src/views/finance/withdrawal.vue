<template>
  <el-card>
    <template #header>
      <el-select v-model="search.status" placeholder="状态" clearable style="width: 160px" @change="loadData">
        <el-option label="待审核" value="pending" />
        <el-option label="已通过" value="approved" />
        <el-option label="已拒绝" value="rejected" />
      </el-select>
    </template>

    <el-alert class="mb12" type="warning" show-icon :closable="false" title="提现审核属于财务操作，通过前必须确认线下打款信息，拒绝必须填写原因。" />
    <el-table :data="tableData" v-loading="loading" empty-text="暂无提现申请">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="amount" label="提现金额" width="120"><template #default="{ row }">¥{{ row.amount }}</template></el-table-column>
      <el-table-column prop="fee" label="手续费" width="100"><template #default="{ row }">¥{{ row.fee || 0 }}</template></el-table-column>
      <el-table-column prop="bankName" label="银行" min-width="130" />
      <el-table-column prop="bankAccount" label="收款账号" min-width="180" />
      <el-table-column prop="accountName" label="户名" width="120" />
      <el-table-column prop="offlineTransferNo" label="线下转账单号" min-width="160" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="申请时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="success" v-if="row.status === 'pending'" @click="handleApprove(row)">通过</el-button>
          <el-button link type="danger" v-if="row.status === 'pending'" @click="handleReject(row)">拒绝</el-button>
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
const search = ref({ status: 'pending' })

const statusText = (status: string) => ({ pending: '待审核', approved: '已通过', rejected: '已拒绝' }[status] || status || '-')

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/finance/withdrawals', { params: { ...search.value, page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row: any) => {
  const result = await ElMessageBox.prompt('请输入线下转账单号或打款备注', '提现通过二次确认', {
    inputPattern: /.+/,
    inputErrorMessage: '线下转账信息必填'
  })
  await ElMessageBox.confirm(`确认已线下打款 ¥${row.amount}，并通过该提现申请？`, '财务二次确认', { type: 'warning' })
  await request.put(`/api/v1/admin/finance/withdrawals/${row.id}/approve`, null, { params: { offlineTransferNo: result.value } })
  ElMessage.success('提现申请已通过')
  loadData()
}

const handleReject = async (row: any) => {
  const result = await ElMessageBox.prompt('请输入拒绝原因', '提现拒绝', {
    inputPattern: /.+/,
    inputErrorMessage: '拒绝原因必填'
  })
  await ElMessageBox.confirm('确认拒绝该提现申请？', '二次确认', { type: 'warning' })
  await request.put(`/api/v1/admin/finance/withdrawals/${row.id}/reject`, null, { params: { reason: result.value } })
  ElMessage.success('提现申请已拒绝')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.mb12 { margin-bottom: 12px; }
</style>
