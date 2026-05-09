<template>
  <div>
    <el-card>
      <template #header>
        <el-row justify="space-between" align="middle">
          <el-col :span="16">
            <el-input v-model="search.keyword" placeholder="搜索商品" style="width: 200px; margin-right: 10px" clearable @keyup.enter="loadData" />
            <el-select v-model="search.categoryId" placeholder="分类" clearable style="width: 150px; margin-right: 10px">
              <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
            <el-select v-model="search.zoneType" placeholder="专区" clearable style="width: 120px; margin-right: 10px">
              <el-option label="主产品专区" value="main" />
              <el-option label="零售专区" value="retail" />
              <el-option label="礼包专区" value="gift" />
            </el-select>
            <el-button type="primary" @click="loadData">搜索</el-button>
          </el-col>
          <el-col :span="8" style="text-align: right">
            <el-button type="primary" @click="$router.push('/product/add')">添加商品</el-button>
          </el-col>
        </el-row>
      </template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="mainImage" label="图片" width="80">
          <template #default="{ row }"><el-image :src="row.mainImage" style="width: 50px; height: 50px" fit="cover" /></template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="price" label="价格" width="100"><template #default="{ row }">¥{{ row.price }}</template></el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="zoneType" label="专区" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.zoneType === 'main'">主产品</el-tag>
            <el-tag v-else-if="row.zoneType === 'retail'" type="success">零售</el-tag>
            <el-tag v-else-if="row.zoneType === 'gift'" type="warning">礼包</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/product/edit/${row.id}`)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.id)"><template #reference><el-button link type="danger">删除</el-button></template></el-popconfirm>
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
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])
const categories = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref({ keyword: '', categoryId: '', zoneType: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/product/list', { params: { ...search.value, page: page.value } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e: any) { ElMessage.error(e.message) }
  loading.value = false
}

const loadCategories = async () => {
  const res = await request.get('/api/v1/admin/product/categories')
  categories.value = res.data || []
}

const toggleStatus = async (row: any) => {
  await request.put(`/api/v1/admin/product/${row.id}/status`, null, { params: { status: row.status === 1 ? 0 : 1 } })
  ElMessage.success('操作成功')
  loadData()
}

const handleDelete = async (id: number) => {
  await request.delete(`/api/v1/admin/product/${id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => { loadData(); loadCategories() })
</script>