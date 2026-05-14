import { http } from '@/utils/request'
import { useUserStore, type UserInfo } from '@/store/user'

export interface LoginResponse { token: string; user: UserInfo; isNewUser?: boolean }
export function getWechatCode() {
  return new Promise<string>((resolve, reject) => uni.login({ provider: 'weixin', success: (res) => resolve(res.code), fail: reject }))
}
async function applyLoginResponse(data: any) {
  const store = useUserStore()
  const token = data.token
  store.setToken(token)
  if (data.user) {
    store.setUser({ ...data.user, token })
    return data.user
  }
  try {
    const profile = await checkAuth()
    store.setUser({ ...profile, token })
    return profile
  } catch {
    store.setUser({ userId: data.userId, nickname: data.nickname, avatarUrl: data.avatarUrl, token })
    return store.userInfo
  }
}
export async function wechatLogin(payload: { inviteCode: string; scene?: string; sourceProductId?: string | number; userAgreement: boolean; privacyPolicy: boolean }) {
  const code = await getWechatCode()
  const res = await http.post<LoginResponse>('/auth/wechat-login', { ...payload, code }, { requireAuth: false, showLoading: true, loadingText: '登录中' })
  await applyLoginResponse(res.data)
  return res.data
}
export async function demoLogin() {
  const res = await http.post<LoginResponse>('/auth/demo-login', {}, { requireAuth: false, showLoading: true, loadingText: '进入演示' })
  await applyLoginResponse(res.data)
  return res.data
}
export async function checkAuth() {
  const res = await http.get<UserInfo>('/auth/check', {}, { showError: false })
  useUserStore().setUser({ ...res.data, token: useUserStore().token })
  return res.data
}
export async function logout() {
  try { await http.post('/auth/logout', {}) } finally { useUserStore().clearUser() }
}
