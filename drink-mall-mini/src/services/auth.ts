import { http } from '@/utils/request'
import { useUserStore, type UserInfo } from '@/store/user'

export interface LoginResponse { token: string; user: UserInfo; isNewUser?: boolean }
export function getWechatCode() {
  return new Promise<string>((resolve, reject) => uni.login({ provider: 'weixin', success: (res) => resolve(res.code), fail: reject }))
}
export async function wechatLogin(payload: { inviteCode: string; scene?: string; sourceProductId?: string | number; userAgreement: boolean; privacyPolicy: boolean }) {
  const code = await getWechatCode()
  const res = await http.post<LoginResponse>('/auth/wechat-login', { ...payload, code }, { requireAuth: false, showLoading: true, loadingText: '登录中' })
  const store = useUserStore()
  store.setToken(res.data.token)
  store.setUser({ ...(res.data.user || {}), token: res.data.token })
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
