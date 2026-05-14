<template>
  <el-card>
    <template #header>
      <el-row justify="space-between" align="middle">
        <el-col :span="18">
          <el-input v-model="search.keyword" placeholder="搜索商品" class="w180" clearable @keyup.enter="loadData" />
          <el-select v-model="search.categoryId" placeholder="分类" clearable class="w150">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
          <el-select v-model="search.zoneType" placeholder="专区" clearable class="w150">
            <el-option label="主产品专区" value="main" />
            <el-option label="招商专区" value="investment" />
            <el-option label="零售专区" value="retail" />
            <el-option label="礼包专区" value="gift" />
          </el-select>
          <el-button type="primary" :loading="loading" @click="loadData">查询</el-button>
        </el-col>
        <el-col :span="6" class="right"><el-button type="primary" @click="$router.push('/product/add')">添加商品</el-button></el-col>
      </el-row>
    </template>
    <el-table :data="tableData" v-loading="loading" empty-text="暂无商品">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="mainImage" label="图片" width="80">
        <template #default="{ row }"><el-image :src="row.mainImage" style="width: 48px; height: 48px" fit="cover" /></template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="180" />
      <el-table-column prop="price" label="价格" width="100"><template #default="{ row }">¥{{ row.price }}</template></el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="giftPoints" label="赠送积分" width="100" />
      <el-table-column prop="allowedPaymentMethods" label="支付方式" min-width="140" />
      <el-table-column prop="zoneType" label="专区" width="110">
        <template #default="{ row }"><el-tag :type="zoneType(row.zoneType)">{{ zoneText(row.zoneType) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/product/edit/${row.id}`)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
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
const categories = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref({ keyword: '', categoryId: '', zoneType: '' })

const zoneText = (zone: string) => ({ main: '主产品', investment: '招商', retail: '零售', gift: '礼包' }[zone] || zone || '-')
const zoneType = (zone: string) => ({ main: 'primary', investment: 'danger', retail: 'success', gift: 'warning' }[zone] || 'info')

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/product/list', { params: { ...search.value, page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  const res = await request.get('/api/v1/admin/product/categories')
  categories.value = res.data || []
}

const toggleStatus = async (row: any) => {
  await ElMessageBox.confirm(`确认${row.status === 1 ? '下架' : '上架'}该商品？`, '商品状态确认', { type: 'warning' })
  await request.put(`/api/v1/admin/product/${row.id}/status`, null, { params: { status: row.status === 1 ? 0 : 1 } })
  ElMessage.success('商品状态已更新')
  loadData()
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.w180 { width: 180px; margin-right: 8px; }
.w150 { width: 150px; margin-right: 8px; }
.right { text-align: right; }
</style>
