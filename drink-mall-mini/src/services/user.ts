import { http } from '@/utils/request'
import { useUserStore, type UserInfo } from '@/store/user'
export const userApi = {
  profile: () => http.get<UserInfo>('/user/info'),
  memberCenter: () => http.get<any>('/user/member-center'),
  submitRealName: (data: any) => http.post('/user/real-name', data, { showLoading: true, loadingText: '提交中' }),
  inviteCode: () => http.get<{ inviteCode: string }>('/user/invite-code'),
  verifyAge: () => http.post('/user/verify-age')
}
export async function refreshUserInfo() {
  const res = await userApi.profile()
  useUserStore().setUser({ ...res.data, token: useUserStore().token })
  return res.data
}
export async function verifyAge() {
  await userApi.verifyAge()
  useUserStore().patchUser({ realNameStatus: useUserStore().userInfo?.realNameStatus })
  return true
}
