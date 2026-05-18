<template>
  <el-card>
    <template #header>
      <el-row :gutter="12" align="middle">
        <el-col :span="6"><el-input v-model="search.keyword" placeholder="昵称/手机号/用户ID" clearable @keyup.enter="loadData" /></el-col>
        <el-col :span="4">
          <el-select v-model="search.ageVerified" placeholder="年龄认证" clearable>
            <el-option label="已认证" :value="1" />
            <el-option label="未认证" :value="0" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" :loading="loading" @click="loadData">查询</el-button>
          <el-button v-if="can('user:write')" @click="openSeed">创建种子账号</el-button>
          <el-button @click="handleExport">导出</el-button>
        </el-col>
      </el-row>
    </template>

    <el-table :data="tableData" v-loading="loading" empty-text="暂无用户">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="avatar" label="头像" width="72">
        <template #default="{ row }"><el-avatar :src="row.avatar" /></template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" min-width="140" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="level" label="等级" width="120" />
      <el-table-column prop="inviterId" label="推荐人ID" width="110" />
      <el-table-column prop="totalConsumption" label="消费金额" width="120">
        <template #default="{ row }">￥{{ row.totalConsumption || 0 }}</template>
      </el-table-column>
      <el-table-column prop="balance" label="余额" width="110"><template #default="{ row }">￥{{ row.balance || 0 }}</template></el-table-column>
      <el-table-column prop="points" label="积分" width="90" />
      <el-table-column prop="ageVerified" label="年龄认证" width="100">
        <template #default="{ row }"><el-tag :type="row.ageVerified ? 'success' : 'info'">{{ row.ageVerified ? '已认证' : '未认证' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="180" />
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }"><el-button link type="primary" @click="$router.push(`/user/${row.id}`)">详情</el-button></template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />

    <el-dialog v-model="seedDialogVisible" title="创建种子账号" width="460px">
      <el-alert title="种子账号不需要上级推荐，创建后会写入后台操作日志。" type="warning" show-icon :closable="false" />
      <el-form :model="seedForm" label-width="100px" class="dialog-form">
        <el-form-item label="OpenID" required><el-input v-model="seedForm.openid" /></el-form-item>
        <el-form-item label="UnionID"><el-input v-model="seedForm.unionid" /></el-form-item>
        <el-form-item label="创建原因" required><el-input v-model="seedForm.reason" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="seedDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitSeed">确认创建</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { can, confirmCritical, requireText } from '@/utils/adminAction'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref<{ keyword: string; ageVerified?: number }>({ keyword: '', ageVerified: undefined })
const seedDialogVisible = ref(false)
const seedForm = ref({ openid: '', unionid: '', reason: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/user/list', { params: { ...search.value, page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const openSeed = () => {
  seedForm.value = { openid: '', unionid: '', reason: '' }
  seedDialogVisible.value = true
}

const submitSeed = async () => {
  if (!requireText(seedForm.value.openid, 'OpenID') || !requireText(seedForm.value.reason, '创建原因')) return
  await confirmCritical('确认创建后台种子账号？普通用户注册仍必须绑定上级。', '二次确认')
  submitting.value = true
  try {
    await request.post('/api/v1/admin/user/seed-accounts', seedForm.value)
    ElMessage.success('种子账号已创建')
    seedDialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleExport = () => {
  const query = encodeURIComponent(search.value.keyword || '')
  window.open(`/api/v1/admin/user/export?keyword=${query}`)
}

onMounted(loadData)
</script>

<style scoped>
.dialog-form { margin-top: 16px; }
</style>
