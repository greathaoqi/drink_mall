<template>
  <div>
    <el-card>
      <template #header>系统配置</template>
      <el-table :data="configs" v-loading="loading">
        <el-table-column prop="configKey" label="配置键" width="200" />
        <el-table-column prop="configValue" label="配置值">
          <template #default="{ row }">
            <el-input v-model="row.configValue" @blur="updateConfig(row)" style="width: 200px" />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" />
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const configs = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  const res = await request.get('/api/v1/admin/system/configs')
  configs.value = res.data || []
  loading.value = false
}

const updateConfig = async (row: any) => {
  await request.put(`/api/v1/admin/system/configs/${row.configKey}`, null, { params: { configValue: row.configValue } })
  ElMessage.success('更新成功')
}

onMounted(loadData)
</script>