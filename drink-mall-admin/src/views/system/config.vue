<template>
  <el-card>
    <template #header>运营配置</template>
    <el-alert class="mb12" type="info" show-icon :closable="false" title="等级、佣金、提现、售后冻结期等规则从后端配置读取；前端不写死业务默认值。" />
    <el-table :data="configs" v-loading="loading" empty-text="暂无配置项">
      <el-table-column prop="configKey" label="配置键" width="240" />
      <el-table-column prop="configValue" label="配置值" min-width="220">
        <template #default="{ row }"><el-input v-model="row.configValue" /></template>
      </el-table-column>
      <el-table-column prop="description" label="说明" min-width="260" />
      <el-table-column prop="updatedAt" label="更新时间" width="180" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }"><el-button link type="primary" @click="updateConfig(row)">保存</el-button></template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const configs = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/system/configs')
    configs.value = res.data || []
  } finally {
    loading.value = false
  }
}

const updateConfig = async (row: any) => {
  await ElMessageBox.confirm(`确认修改配置 ${row.configKey}？该操作应写入操作日志。`, '配置变更确认', { type: 'warning' })
  await request.put(`/api/v1/admin/system/configs/${row.configKey}`, null, { params: { configValue: row.configValue } })
  ElMessage.success('配置已保存')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.mb12 { margin-bottom: 12px; }
</style>
