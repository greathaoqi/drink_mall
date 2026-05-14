<template>
  <div class="login-container">
    <el-card class="login-card" shadow="always">
      <h1>酒水商城管理后台</h1>
      <p>管理员登录</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model.trim="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="passwordHash">
          <el-input v-model="form.passwordHash" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const adminStore = useAdminStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({ username: 'admin', passwordHash: 'admin123' })
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  passwordHash: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await request.post('/api/v1/admin/auth/login', form)
    adminStore.setSession(res.data.token, res.data.adminUser)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #eef2f7; }
.login-card { width: 400px; padding: 14px; border-radius: 8px; }
h1 { margin: 0; text-align: center; font-size: 24px; color: #1f2937; }
p { margin: 10px 0 28px; text-align: center; color: #6b7280; }
.login-button { width: 100%; }
</style>
