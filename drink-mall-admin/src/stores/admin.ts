import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import request from '@/utils/request'

export interface AdminUser {
  id?: number
  username?: string
  name?: string
  realName?: string
  role?: string
  permissions?: string[]
}

const rolePermissions: Record<string, string[]> = {
  admin: ['*'],
  operator: [
    'dashboard:view',
    'user:view',
    'product:view',
    'order:view',
    'aftersale:view',
    'content:view',
    'operation:log:view'
  ],
  finance: [
    'dashboard:view',
    'user:view',
    'finance:view',
    'finance:adjust',
    'withdrawal:review',
    'operation:log:view'
  ]
}

const readStoredUser = (): AdminUser | null => {
  const raw = localStorage.getItem('adminUser')
  if (!raw) return null
  try {
    return JSON.parse(raw) as AdminUser
  } catch {
    return null
  }
}

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('token') || '')
  const adminUser = ref<AdminUser | null>(readStoredUser())

  const role = computed(() => adminUser.value?.role || '')
  const permissions = computed(() => {
    const explicit = adminUser.value?.permissions || []
    const byRole = rolePermissions[role.value] || []
    return Array.from(new Set([...explicit, ...byRole]))
  })

  const hasPermission = (permission?: string) => {
    if (!permission) return true
    return permissions.value.includes('*') || permissions.value.includes(permission)
  }

  const setSession = (nextToken: string, nextUser: AdminUser) => {
    token.value = nextToken
    adminUser.value = nextUser
    localStorage.setItem('token', nextToken)
    localStorage.setItem('adminUser', JSON.stringify(nextUser))
  }

  const clearSession = () => {
    token.value = ''
    adminUser.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('adminUser')
  }

  const fetchInfo = async () => {
    if (!token.value) return null
    const res = await request.get('/api/v1/admin/auth/info')
    adminUser.value = res.data || null
    if (adminUser.value) localStorage.setItem('adminUser', JSON.stringify(adminUser.value))
    return adminUser.value
  }

  return { token, adminUser, role, permissions, hasPermission, setSession, clearSession, fetchInfo }
})
