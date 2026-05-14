import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAdminStore } from '@/stores/admin'

const routes: RouteRecordRaw[] = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { public: true } },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue'), meta: { permission: 'dashboard:view' } },
      { path: 'product', name: 'ProductList', component: () => import('@/views/product/index.vue'), meta: { permission: 'product:view' } },
      { path: 'product/add', name: 'ProductAdd', component: () => import('@/views/product/edit.vue'), meta: { permission: 'product:write' } },
      { path: 'product/edit/:id', name: 'ProductEdit', component: () => import('@/views/product/edit.vue'), meta: { permission: 'product:write' } },
      { path: 'product/category', name: 'CategoryList', component: () => import('@/views/product/category.vue'), meta: { permission: 'product:view' } },
      { path: 'order', name: 'OrderList', component: () => import('@/views/order/index.vue'), meta: { permission: 'order:view' } },
      { path: 'order/:id', name: 'OrderDetail', component: () => import('@/views/order/detail.vue'), meta: { permission: 'order:view' } },
      { path: 'order/aftersale', name: 'AftersaleList', component: () => import('@/views/order/aftersale.vue'), meta: { permission: 'aftersale:view' } },
      { path: 'user', name: 'UserList', component: () => import('@/views/user/index.vue'), meta: { permission: 'user:view' } },
      { path: 'user/:id', name: 'UserDetail', component: () => import('@/views/user/detail.vue'), meta: { permission: 'user:view' } },
      { path: 'user/invite-codes', name: 'InviteCodeList', component: () => import('@/views/user/invite-codes.vue'), meta: { permission: 'user:invite' } },
      { path: 'user/real-name', name: 'RealNameReview', component: () => import('@/views/user/real-name.vue'), meta: { permission: 'real-name:review' } },
      { path: 'content/banner', name: 'BannerList', component: () => import('@/views/content/banner.vue'), meta: { permission: 'content:view' } },
      { path: 'content/announcement', name: 'AnnouncementList', component: () => import('@/views/content/announcement.vue'), meta: { permission: 'content:view' } },
      { path: 'content/video', name: 'VideoList', component: () => import('@/views/content/video.vue'), meta: { permission: 'content:view' } },
      { path: 'content/help', name: 'HelpList', component: () => import('@/views/content/help.vue'), meta: { permission: 'content:view' } },
      { path: 'finance/withdrawal', name: 'WithdrawalList', component: () => import('@/views/finance/withdrawal.vue'), meta: { permission: 'withdrawal:review' } },
      { path: 'finance/logs', name: 'FinanceLogs', component: () => import('@/views/finance/logs.vue'), meta: { permission: 'finance:view' } },
      { path: 'finance/rewards', name: 'RewardRecords', component: () => import('@/views/finance/rewards.vue'), meta: { permission: 'finance:view' } },
      { path: 'system/admin', name: 'AdminUserList', component: () => import('@/views/system/admin.vue'), meta: { permission: 'system:admin' } },
      { path: 'system/config', name: 'SystemConfig', component: () => import('@/views/system/config.vue'), meta: { permission: 'system:config' } },
      { path: 'system/logs', name: 'OperationLogs', component: () => import('@/views/system/logs.vue'), meta: { permission: 'operation:log:view' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const adminStore = useAdminStore()
  const token = adminStore.token || localStorage.getItem('token')
  if (!token && !to.meta.public) {
    next('/login')
  } else if (token && to.path === '/login') {
    next('/dashboard')
  } else if (token && !adminStore.hasPermission(to.meta.permission as string | undefined)) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
