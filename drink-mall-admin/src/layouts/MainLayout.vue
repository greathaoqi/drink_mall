<template>
  <el-container class="layout-container">
    <el-aside width="232px" class="aside">
      <div class="logo">酒水商城管理后台</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#243447"
        text-color="#d4dde8"
        active-text-color="#ffffff"
      >
        <template v-for="item in visibleMenus" :key="item.index">
          <el-menu-item v-if="!item.children" :index="item.index">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
          <el-sub-menu v-else :index="item.index">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.index" :index="child.index">
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="page-title">{{ routeTitle }}</div>
        <el-dropdown>
          <span class="user-info">
            <el-icon><User /></el-icon>
            {{ adminStore.adminUser?.name || adminStore.adminUser?.username || '管理员' }}
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item disabled>角色：{{ adminStore.adminUser?.role || '-' }}</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import { useAdminStore } from '@/stores/admin'

interface MenuItem {
  index: string
  title: string
  icon: string
  permission?: string
  children?: Omit<MenuItem, 'icon'>[]
}

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()

const menus: MenuItem[] = [
  { index: '/dashboard', title: '控制台', icon: 'DataBoard', permission: 'dashboard:view' },
  {
    index: 'user',
    title: '用户管理',
    icon: 'User',
    children: [
      { index: '/user', title: '用户列表', permission: 'user:view' },
      { index: '/user/real-name', title: '实名认证审核', permission: 'real-name:review' },
      { index: '/user/invite-codes', title: '邀请码管理', permission: 'user:invite' }
    ]
  },
  {
    index: 'product',
    title: '商品管理',
    icon: 'Goods',
    children: [
      { index: '/product', title: '商品列表', permission: 'product:view' },
      { index: '/product/category', title: '分类管理', permission: 'product:view' }
    ]
  },
  {
    index: 'order',
    title: '订单售后',
    icon: 'List',
    children: [
      { index: '/order', title: '订单列表', permission: 'order:view' },
      { index: '/order/aftersale', title: '售后管理', permission: 'aftersale:view' }
    ]
  },
  {
    index: 'content',
    title: '内容运营',
    icon: 'Picture',
    children: [
      { index: '/content/banner', title: '轮播图', permission: 'content:view' },
      { index: '/content/announcement', title: '公告管理', permission: 'content:view' },
      { index: '/content/video', title: '视频管理', permission: 'content:view' },
      { index: '/content/help', title: '图文管理', permission: 'content:view' }
    ]
  },
  {
    index: 'finance',
    title: '财务资产',
    icon: 'Money',
    children: [
      { index: '/finance/withdrawal', title: '提现审核', permission: 'withdrawal:review' },
      { index: '/finance/logs', title: '资产明细与调整', permission: 'finance:view' },
      { index: '/finance/rewards', title: '奖励记录', permission: 'finance:view' }
    ]
  },
  {
    index: 'system',
    title: '系统管理',
    icon: 'Setting',
    children: [
      { index: '/system/admin', title: '管理员账号', permission: 'system:admin' },
      { index: '/system/config', title: '运营配置', permission: 'system:config' },
      { index: '/system/logs', title: '操作日志', permission: 'operation:log:view' }
    ]
  }
]

const visibleMenus = computed(() => menus
  .map(item => {
    if (!item.children) return adminStore.hasPermission(item.permission) ? item : null
    const children = item.children.filter(child => adminStore.hasPermission(child.permission))
    return children.length ? { ...item, children } : null
  })
  .filter(Boolean) as MenuItem[])

const routeTitle = computed(() => {
  const flat = visibleMenus.value.flatMap(item => item.children || [item])
  return flat.find(item => item.index === route.path)?.title || '管理后台'
})

const handleLogout = async () => {
  try {
    await request.post('/api/v1/admin/auth/logout')
  } finally {
    adminStore.clearSession()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.aside { background: #243447; }
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; font-weight: 700; background: #1c2b3a; }
.header { background: #fff; border-bottom: 1px solid #e5e7eb; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; }
.page-title { font-size: 16px; font-weight: 700; color: #1f2937; }
.user-info { cursor: pointer; display: flex; align-items: center; gap: 6px; color: #374151; }
.main { background: #f5f7fb; padding: 20px; overflow: auto; }
</style>
