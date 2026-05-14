import { useUserStore } from '@/store/user'

export function requireLogin(message = '请先登录') {
  const store = useUserStore()
  if (store.isLoggedIn) return true
  uni.showToast({ title: message, icon: 'none' })
  setTimeout(() => uni.navigateTo({ url: '/pages/login/index' }), 250)
  return false
}

export function realNameLabel(status?: string) {
  const map: Record<string, string> = { APPROVED: '已实名', PENDING: '审核中', REJECTED: '未通过', UNSUBMITTED: '未实名' }
  return map[status || 'UNSUBMITTED'] || '未实名'
}

export function createSubmitLock() {
  let locked = false
  return async function run<T>(task: () => Promise<T>) {
    if (locked) return undefined
    locked = true
    try { return await task() } finally { locked = false }
  }
}
