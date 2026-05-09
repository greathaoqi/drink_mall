<template>
  <div>
    <el-card>
      <template #header><el-button type="primary" @click="handleAdd">发布公告</el-button></template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="summary" label="摘要" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '发布' : '草稿' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.id)"><template #reference><el-button link type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
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
const form = ref({ id: 0, title: '', summary: '', content: '', status: 1 })

const loadData = async () => {
  loading.value = true
  const res = await request.get('/api/v1/admin/content/announcements', { params: { page: page.value } })
  tableData.value = res.data?.records || []
  total.value = res.data?.total || 0
  loading.value = false
}

const handleAdd = () => { isEdit.value = false; Object.assign(form.value, { id: 0, title: '', summary: '', content: '', status: 1 }); dialogVisible.value = true }
const handleEdit = (row: any) => { isEdit.value = true; Object.assign(form.value, row); dialogVisible.value = true }

const handleSubmit = async () => {
  if (isEdit.value) {
    await request.put(`/api/v1/admin/content/announcements/${form.value.id}`, form.value)
  } else {
    await request.post('/api/v1/admin/content/announcements', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id: number) => {
  await request.delete(`/api/v1/admin/content/announcements/${id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>