<template>
  <div>
    <el-card v-loading="loading">
      <template #header>用户详情</template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ user.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
        <el-descriptions-item label="积分">{{ user.points }}</el-descriptions-item>
        <el-descriptions-item label="余额">¥{{ user.balance }}</el-descriptions-item>
        <el-descriptions-item label="冻结余额">¥{{ user.frozenBalance }}</el-descriptions-item>
        <el-descriptions-item label="年龄验证"><el-tag :type="user.ageVerified ? 'success' : 'info'">{{ user.ageVerified ? '已验证' : '未验证' }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ user.createdAt }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'

const route = useRoute()
const loading = ref(false)
const user = ref<any>({})

const loadData = async () => {
  loading.value = true
  const res = await request.get(`/api/v1/admin/user/${route.params.id}`)
  user.value = res.data?.user || {}
  loading.value = false
}

onMounted(loadData)
</script>