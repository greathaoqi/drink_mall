<template>
  <div>
    <el-card v-loading="loading">
      <template #header>
        <el-row justify="space-between" align="middle">
          <span>用户详情</span>
          <el-button @click="$router.back()">返回</el-button>
        </el-row>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ user.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="等级">{{ user.level || user.levelCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="推荐人ID">{{ user.inviterId || user.parentId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ user.createdAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="消费金额">¥{{ user.totalConsumption || 0 }}</el-descriptions-item>
        <el-descriptions-item label="余额">¥{{ user.balance || 0 }}</el-descriptions-item>
        <el-descriptions-item label="积分">{{ user.points || 0 }}</el-descriptions-item>
        <el-descriptions-item label="年龄认证">
          <el-tag :type="user.ageVerified ? 'success' : 'info'">{{ user.ageVerified ? '已认证' : '未认证' }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="mt16">
      <template #header>资产账户</template>
      <el-table :data="assetRows" empty-text="暂无资产数据">
        <el-table-column prop="label" label="资产类型" width="160" />
        <el-table-column prop="balance" label="可用余额" />
        <el-table-column prop="frozen" label="冻结余额" />
      </el-table>
    </el-card>

    <el-card class="mt16">
      <template #header>等级变更记录</template>
      <el-table :data="levelLogs" empty-text="暂无等级变更记录">
        <el-table-column prop="beforeLevel" label="变更前" />
        <el-table-column prop="afterLevel" label="变更后" />
        <el-table-column prop="reason" label="原因" />
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'

const route = useRoute()
const loading = ref(false)
const detail = ref<any>({})

const user = computed(() => detail.value.user || detail.value || {})
const levelLogs = computed(() => detail.value.levelLogs || detail.value.levelChangeLogs || [])
const assetRows = computed(() => {
  const assets = detail.value.assets || detail.value.assetAccounts || {}
  if (Array.isArray(assets)) return assets
  return [
    { label: '余额', balance: user.value.balance || assets.balance || 0, frozen: user.value.frozenBalance || assets.frozenBalance || 0 },
    { label: 'DF', balance: assets.df || assets.dfBalance || 0, frozen: assets.frozenDf || 0 },
    { label: '酒豆', balance: assets.wineBean || assets.wineBeanBalance || 0, frozen: assets.frozenWineBean || 0 },
    { label: '积分', balance: user.value.points || assets.points || 0, frozen: assets.frozenPoints || 0 },
    { label: '期权', balance: assets.option || assets.optionBalance || 0, frozen: assets.frozenOption || 0 }
  ]
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get(`/api/v1/admin/user/${route.params.id}`)
    detail.value = res.data || {}
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.mt16 { margin-top: 16px; }
</style>
