<template>
  <div>
    <el-card>
      <template #header>
        <el-row :gutter="20">
          <el-col :span="6"><el-input v-model="search.keyword" placeholder="搜索用户" clearable @keyup.enter="loadData" /></el-col>
          <el-col :span="4"><el-button type="primary" @click="loadData">搜索</el-button></el-col>
          <el-col :span="4" style="text-align: right"><el-button @click="handleExport">导出</el-button></el-col>
        </el-row>
      </template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }"><el-avatar :src="row.avatar" /></template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="points" label="积分" width="80" />
        <el-table-column prop="balance" label="余额" width="100"><template #default="{ row }">¥{{ row.balance || 0 }}</template></el-table-column>
        <el-table-column prop="ageVerified" label="年龄验证" width="100">
          <template #default="{ row }"><el-tag :type="row.ageVerified ? 'success' : 'info'">{{ row.ageVerified ? '已验证' : '未验证' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button link type="primary" @click="$router.push(`/user/${row.id}`)">详情</el-button></template>
        </el-table-column>
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
const search = ref({ keyword: '' })

const loadData = async () => {
  loading.value = true
  const res = await request.get('/api/v1/admin/user/list', { params: { ...search.value, page: page.value } })
  tableData.value = res.data?.records || []
  total.value = res.data?.total || 0
  loading.value = false
}

const handleExport = () => {
  window.open('/api/v1/admin/user/export?keyword=' + search.value.keyword)
}

onMounted(loadData)
</script>