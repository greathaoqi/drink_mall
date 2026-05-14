<template>
  <el-card>
    <template #header>
      <el-row :gutter="12">
        <el-col :span="4"><el-input v-model.number="search.userId" placeholder="用户ID" clearable /></el-col>
        <el-col :span="4">
          <el-select v-model="search.status" placeholder="奖励状态" clearable>
            <el-option label="冻结中" value="frozen" />
            <el-option label="已解冻" value="released" />
            <el-option label="已回滚" value="rolled_back" />
          </el-select>
        </el-col>
        <el-col :span="4"><el-button type="primary" :loading="loading" @click="loadData">查询</el-button></el-col>
      </el-row>
    </template>
    <el-table :data="tableData" v-loading="loading" empty-text="暂无奖励记录">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="sourceUserId" label="来源用户" width="100" />
      <el-table-column prop="rewardType" label="奖励类型" width="140" />
      <el-table-column prop="amount" label="奖励金额" width="120" />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column prop="remark" label="备注" min-width="180" />
      <el-table-column prop="createdAt" label="生成时间" width="180" />
      <el-table-column prop="releasedAt" label="解冻时间" width="180" />
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const search = ref<{ userId?: number; status: string }>({ userId: undefined, status: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/finance/reward-records', { params: { ...search.value, page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
