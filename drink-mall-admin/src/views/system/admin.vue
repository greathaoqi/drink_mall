<template>
  <div>
    <el-card>
      <template #header><el-button type="primary" @click="handleAdd">添加管理员</el-button></template>
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }"><el-tag>{{ row.role === 'admin' ? '管理员' : '运营' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.id)"><template #reference><el-button link type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑管理员' : '添加管理员'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" :placeholder="isEdit ? '不修改请留空' : ''" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role"><el-option label="管理员" value="admin" /><el-option label="运营" value="operator" /></el-select>
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
const form = ref({ id: 0, username: '', password: '', realName: '', role: 'operator' })

const loadData = async () => {
  loading.value = true
  const res = await request.get('/api/v1/admin/system/users', { params: { page: page.value } })
  tableData.value = res.data?.records || []
  total.value = res.data?.total || 0
  loading.value = false
}

const handleAdd = () => { isEdit.value = false; Object.assign(form.value, { id: 0, username: '', password: '', realName: '', role: 'operator' }); dialogVisible.value = true }
const handleEdit = (row: any) => { isEdit.value = true; Object.assign(form.value, { ...row, password: '' }); dialogVisible.value = true }

const handleSubmit = async () => {
  if (isEdit.value) {
    await request.put(`/api/v1/admin/system/users/${form.value.id}`, form.value)
  } else {
    await request.post('/api/v1/admin/system/users', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const toggleStatus = async (row: any) => {
  await request.put(`/api/v1/admin/system/users/${row.id}/status`, null, { params: { status: row.status === 1 ? 0 : 1 } })
  ElMessage.success('操作成功')
  loadData()
}

const handleDelete = async (id: number) => {
  await request.delete(`/api/v1/admin/system/users/${id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>