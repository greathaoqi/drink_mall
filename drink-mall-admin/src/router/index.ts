import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue') },
      { path: 'product', name: 'ProductList', component: () => import('@/views/product/index.vue') },
      { path: 'product/add', name: 'ProductAdd', component: () => import('@/views/product/edit.vue') },
      { path: 'product/edit/:id', name: 'ProductEdit', component: () => import('@/views/product/edit.vue') },
      { path: 'product/category', name: 'CategoryList', component: () => import('@/views/product/category.vue') },
      { path: 'order', name: 'OrderList', component: () => import('@/views/order/index.vue') },
      { path: 'order/:id', name: 'OrderDetail', component: () => import('@/views/order/detail.vue') },
      { path: 'order/aftersale', name: 'AftersaleList', component: () => import('@/views/order/aftersale.vue') },
      { path: 'user', name: 'UserList', component: () => import('@/views/user/index.vue') },
      { path: 'user/:id', name: 'UserDetail', component: () => import('@/views/user/detail.vue') },
      { path: 'content/banner', name: 'BannerList', component: () => import('@/views/content/banner.vue') },
      { path: 'content/announcement', name: 'AnnouncementList', component: () => import('@/views/content/announcement.vue') },
      { path: 'content/video', name: 'VideoList', component: () => import('@/views/content/video.vue') },
      { path: 'content/help', name: 'HelpList', component: () => import('@/views/content/help.vue') },
      { path: 'finance/withdrawal', name: 'WithdrawalList', component: () => import('@/views/finance/withdrawal.vue') },
      { path: 'finance/logs', name: 'FinanceLogs', component: () => import('@/views/finance/logs.vue') },
      { path: 'system/admin', name: 'AdminUserList', component: () => import('@/views/system/admin.vue') },
      { path: 'system/config', name: 'SystemConfig', component: () => import('@/views/system/config.vue') },
      { path: 'system/logs', name: 'OperationLogs', component: () => import('@/views/system/logs.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router