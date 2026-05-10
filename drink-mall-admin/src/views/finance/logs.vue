<template>
  <div>
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="积分流水" name="points" />
        <el-tab-pane label="余额流水" name="balance" />
      </el-tabs>
      <el-table :data="tableData" v-loading="loading" v-if="activeTab === 'points'">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="points" label="积分变动" width="100"><template #default="{ row }"><span :style="{ color: row.points > 0 ? 'green' : 'red' }">{{ row.points > 0 ? '+' : '' }}{{ row.points }}</span></template></el-table-column>
        <el-table-column prop="changeType" label="类型" width="120" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>
      <el-table :data="tableData" v-loading="loading" v-else>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="changeType" label="变动类型" width="120" />
        <el-table-column prop="amount" label="金额" width="100"><template #default="{ row }"><span :style="{ color: row.amount > 0 ? 'green' : 'red' }">{{ row.amount > 0 ? '+' : '' }}{{ row.amount }}</span></template></el-table-column>
        <el-table-column prop="beforeBalance" label="变动前余额" width="120" />
        <el-table-column prop="afterBalance" label="变动后余额" width="120" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const activeTab = ref('points')
const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const url = activeTab.value === 'points' ? '/api/v1/admin/finance/points-logs' : '/api/v1/admin/finance/balance-logs'
    const res = await request.get(url, { params: { page: page.value, size: 20 } })
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

onMounted(loadData)
</script>