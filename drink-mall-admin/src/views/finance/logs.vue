<template>
  <div>
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="loadData">
        <el-tab-pane label="积分流水" name="points" />
        <el-tab-pane label="余额流水" name="balance" />
      </el-tabs>
      <el-table :data="tableData" v-loading="loading" v-if="activeTab === 'points'">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="points" label="积分" width="100"><template #default="{ row }"><span :style="{ color: row.points > 0 ? 'green' : 'red' }">{{ row.points > 0 ? '+' : '' }}{{ row.points }}</span></template></el-table-column>
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>
      <el-table :data="tableData" v-loading="loading" v-else>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="type" label="类型" width="120" />
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
  const url = activeTab.value === 'points' ? '/api/v1/admin/finance/points-logs' : '/api/v1/admin/finance/balance-logs'
  const res = await request.get(url, { params: { page: page.value } })
  tableData.value = res.data?.records || []
  total.value = res.data?.total || 0
  loading.value = false
}

onMounted(loadData)
</script>