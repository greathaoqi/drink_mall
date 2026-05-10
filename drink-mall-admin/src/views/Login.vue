<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>酒水商城管理后台</h2>
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123' })

const handleLogin = async () => {
  loading.value = true
  try {
    const res = await request.post('/api/v1/admin/auth/login', {
      username: form.username,
      passwordHash: form.password
    })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('adminUser', JSON.stringify(res.data.adminUser))
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e: any) {
    ElMessage.error('用户名或密码错误')
  }
  loading.value = false
}
</script>

<style scoped>
.login-container { height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-card { width: 400px; padding: 20px; }
h2 { text-align: center; margin-bottom: 30px; color: #303133; }
</style>
