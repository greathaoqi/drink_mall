<template>
  <el-card>
    <template #header>
      <el-row justify="space-between" align="middle">
        <span>管理员账号</span>
        <el-button type="primary" @click="handleAdd">添加管理员</el-button>
      </el-row>
    </template>
    <el-table :data="tableData" v-loading="loading" empty-text="暂无管理员">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="role" label="角色" width="120" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="lastLoginAt" label="最近登录" width="180" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="210" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑管理员' : '添加管理员'" width="440px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="用户名" required><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item label="密码" :required="!isEdit"><el-input v-model="form.passwordHash" type="password" :placeholder="isEdit ? '不修改请留空' : ''" show-password /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="超级管理员" value="admin" />
            <el-option label="运营" value="operator" />
            <el-option label="财务" value="finance" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: 0, username: '', passwordHash: '', name: '', role: 'operator' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/system/users', { params: { page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.value = { id: 0, username: '', passwordHash: '', name: '', role: 'operator' }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  form.value = { id: row.id, username: row.username, passwordHash: '', name: row.name, role: row.role || 'operator' }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.username || (!isEdit.value && !form.value.passwordHash)) {
    ElMessage.warning('用户名和密码必填')
    return
  }
  submitting.value = true
  try {
    const payload = { ...form.value }
    if (isEdit.value && !payload.passwordHash) delete (payload as any).passwordHash
    if (isEdit.value) {
      await request.put(`/api/v1/admin/system/users/${form.value.id}`, payload)
    } else {
      await request.post('/api/v1/admin/system/users', payload)
    }
    ElMessage.success('管理员已保存')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (row: any) => {
  await ElMessageBox.confirm(`确认${row.status === 1 ? '禁用' : '启用'}管理员 ${row.username}？`, '管理员状态确认', { type: 'warning' })
  await request.put(`/api/v1/admin/system/users/${row.id}/status`, null, { params: { status: row.status === 1 ? 0 : 1 } })
  ElMessage.success('状态已更新')
  loadData()
}

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm(`确认删除管理员 ${row.username}？`, '删除确认', { type: 'warning' })
  await request.delete(`/api/v1/admin/system/users/${row.id}`)
  ElMessage.success('管理员已删除')
  loadData()
}

onMounted(loadData)
</script>
