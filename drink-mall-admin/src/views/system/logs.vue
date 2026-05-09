<template>
  <div>
    <el-card>
      <template #header>
        <el-row :gutter="20">
          <el-col :span="4"><el-select v-model="search.module" placeholder="模块" clearable @change="loadData">
            <el-option label="商品" value="product" /><el-option label="订单" value="order" /><el-option label="用户" value="user" /><el-option label="内容" value="content" /><el-option label="财务" value="finance" /><el-option label="系统" value="system" />
          </el-select></el-col>
          <el-col :span="8"><el-date-picker v-model="search.dateRange" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" @change="loadData" /></el-col>
        </el-row>
      </template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="module" label="模块" width="100">
          <template #default="{ row }"><el-tag>{{ row.module }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="action" label="操作" width="100" />
        <el-table-column prop="targetId" label="目标ID" width="100" />
        <el-table-column prop="detail" label="详情" />
        <el-table-column prop="ip" label="IP" width="120" />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref({ module: '', dateRange: [] as string[] })

const loadData = async () => {
  loading.value = true
  const params: any = { page: page.value }
  if (search.value.module) params.module = search.value.module
  if (search.value.dateRange?.length === 2) {
    params.startDate = search.value.dateRange[0]
    params.endDate = search.value.dateRange[1]
  }
  const res = await request.get('/api/v1/admin/system/logs', { params })
  tableData.value = res.data?.records || []
  total.value = res.data?.total || 0
  loading.value = false
}

onMounted(loadData)
</script>