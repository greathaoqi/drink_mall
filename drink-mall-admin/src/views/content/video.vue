<template>
  <div>
    <el-card>
      <template #header><el-button type="primary" @click="handleAdd">添加视频</el-button></template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="coverImage" label="封面" width="120">
          <template #default="{ row }"><el-image :src="row.coverImage" style="width: 80px; height: 45px" fit="cover" /></template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="duration" label="时长" width="100" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.id)"><template #reference><el-button link type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑视频' : '添加视频'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="视频URL"><el-input v-model="form.videoUrl" /></el-form-item>
        <el-form-item label="封面URL"><el-input v-model="form.coverImage" /></el-form-item>
        <el-form-item label="时长(秒)"><el-input-number v-model="form.duration" :min="0" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
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
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: 0, title: '', videoUrl: '', coverImage: '', duration: 0, sortOrder: 0, status: 1 })

const loadData = async () => {
  loading.value = true
  const res = await request.get('/api/v1/admin/content/videos', { params: { page: page.value } })
  tableData.value = res.data?.records || []
  total.value = res.data?.total || 0
  loading.value = false
}

const handleAdd = () => { isEdit.value = false; Object.assign(form.value, { id: 0, title: '', videoUrl: '', coverImage: '', duration: 0, sortOrder: 0, status: 1 }); dialogVisible.value = true }
const handleEdit = (row: any) => { isEdit.value = true; Object.assign(form.value, row); dialogVisible.value = true }

const handleSubmit = async () => {
  if (isEdit.value) {
    await request.put(`/api/v1/admin/content/videos/${form.value.id}`, form.value)
  } else {
    await request.post('/api/v1/admin/content/videos', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id: number) => {
  await request.delete(`/api/v1/admin/content/videos/${id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>