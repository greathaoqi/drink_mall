<template>
  <div>
    <el-card>
      <template #header>
        <el-button type="primary" @click="handleAdd">添加分类</el-button>
      </template>
      <el-table :data="tableData" row-key="id" default-expand-all>
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.id)"><template #reference><el-button link type="danger">删除</el-button></template></el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '添加分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="父级">
          <el-select v-model="form.parentId" clearable placeholder="顶级分类">
            <el-option v-for="c in tableData.filter(x => x.id !== form.id)" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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

const tableData = ref<any[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: 0, name: '', parentId: null as number | null, sortOrder: 0, status: 1 })

const loadData = async () => {
  const res = await request.get('/api/v1/admin/product/categories')
  tableData.value = buildTree(res.data || [])
}

const buildTree = (items: any[], parentId: number | null = null) => {
  return items.filter(x => x.parentId === parentId).map(x => ({ ...x, children: buildTree(items, x.id) }))
}

const handleAdd = () => { isEdit.value = false; Object.assign(form.value, { id: 0, name: '', parentId: null, sortOrder: 0, status: 1 }); dialogVisible.value = true }
const handleEdit = (row: any) => { isEdit.value = true; Object.assign(form.value, row); dialogVisible.value = true }

const handleSubmit = async () => {
  if (isEdit.value) {
    await request.put(`/api/v1/admin/product/categories/${form.value.id}`, form.value)
  } else {
    await request.post('/api/v1/admin/product/categories', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (id: number) => {
  await request.delete(`/api/v1/admin/product/categories/${id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>