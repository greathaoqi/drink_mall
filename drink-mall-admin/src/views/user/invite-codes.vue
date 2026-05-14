<template>
  <el-card>
    <template #header>
      <el-row :gutter="12" align="middle">
        <el-col :span="5">
          <el-input v-model.number="search.ownerUserId" placeholder="归属用户ID" clearable />
        </el-col>
        <el-col :span="4">
          <el-select v-model="search.status" placeholder="状态" clearable>
            <el-option label="未使用" value="unused" />
            <el-option label="已使用" value="used" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-button type="primary" :loading="loading" @click="loadData">查询</el-button>
          <el-button @click="openCreate">创建邀请码</el-button>
        </el-col>
      </el-row>
    </template>

    <el-table :data="tableData" v-loading="loading" empty-text="暂无邀请码">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="code" label="邀请码" min-width="160" />
      <el-table-column prop="ownerUserId" label="归属用户ID" width="120" />
      <el-table-column prop="usedByUserId" label="使用用户ID" width="120" />
      <el-table-column prop="source" label="来源" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'used' ? 'success' : row.status === 'disabled' ? 'info' : 'warning'">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column prop="usedAt" label="使用时间" width="180" />
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" title="创建邀请码" width="420px">
      <el-alert title="后台创建邀请码会写入操作日志，请确认归属用户与用途。" type="warning" show-icon :closable="false" />
      <el-form :model="form" label-width="100px" class="dialog-form">
        <el-form-item label="归属用户ID" required><el-input-number v-model="form.ownerUserId" :min="1" /></el-form-item>
        <el-form-item label="业务来源" required><el-input v-model="form.source" placeholder="如 admin_seed / offline_invite" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">确认创建</el-button>
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
const total = ref(0)
const page = ref(1)
const dialogVisible = ref(false)
const search = ref<{ ownerUserId?: number; status: string }>({ ownerUserId: undefined, status: '' })
const form = ref({ ownerUserId: 1, source: 'admin_seed' })

const statusText = (status: string) => ({ unused: '未使用', used: '已使用', disabled: '禁用' }[status] || status || '-')

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/user/invite-codes', { params: { ...search.value, page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  form.value = { ownerUserId: 1, source: 'admin_seed' }
  dialogVisible.value = true
}

const submitCreate = async () => {
  await ElMessageBox.confirm('确认创建邀请码？该操作会记录后台操作日志。', '二次确认', { type: 'warning' })
  submitting.value = true
  try {
    await request.post('/api/v1/admin/user/invite-codes', form.value)
    ElMessage.success('邀请码已创建')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.dialog-form { margin-top: 16px; }
</style>
