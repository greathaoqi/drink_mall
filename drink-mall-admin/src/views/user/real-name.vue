<template>
  <el-card>
    <template #header>
      <el-row :gutter="12">
        <el-col :span="5"><el-input v-model="search.keyword" placeholder="用户ID/姓名/证件号" clearable @keyup.enter="loadData" /></el-col>
        <el-col :span="4">
          <el-select v-model="search.status" placeholder="审核状态" clearable>
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-col>
        <el-col :span="4"><el-button type="primary" :loading="loading" @click="loadData">查询</el-button></el-col>
      </el-row>
    </template>

    <el-alert class="mb12" type="info" show-icon :closable="false" title="身份证唯一性由后端校验；审核通过和拒绝都必须填写原因并记录操作日志。" />
    <el-table :data="tableData" v-loading="loading" empty-text="暂无实名申请">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="idCardNo" label="身份证号" min-width="190" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'rejected' ? 'danger' : 'warning'">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rejectReason" label="拒绝原因/提示" min-width="180" />
      <el-table-column prop="createdAt" label="提交时间" width="180" />
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button link type="success" v-if="can('real-name:review') && row.status === 'pending'" @click="review(row, true)">通过</el-button>
          <el-button link type="danger" v-if="can('real-name:review') && row.status === 'pending'" @click="review(row, false)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { can, confirmCritical, promptReason } from '@/utils/adminAction'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const search = ref({ keyword: '', status: 'pending' })

const statusText = (status: string) => ({ pending: '待审核', approved: '已通过', rejected: '已拒绝' }[status] || status || '-')

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/real-name/list', { params: { ...search.value, page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const review = async (row: any, approved: boolean) => {
  const reason = await promptReason(approved ? '实名审核通过' : '实名审核拒绝', approved ? '请输入通过原因' : '请输入拒绝原因')
  await confirmCritical(`确认${approved ? '通过' : '拒绝'}用户 ${row.userId} 的实名认证？`, '实名审核确认')
  await request.post(`/api/v1/admin/real-name/${row.id}/review`, {
    approved,
    rejectReason: reason
  })
  ElMessage.success('审核操作已提交')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.mb12 { margin-bottom: 12px; }
</style>
