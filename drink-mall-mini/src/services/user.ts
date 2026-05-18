import { API_BASE_URL, http } from '@/utils/request'
import { useUserStore, type UserInfo } from '@/store/user'

function parseUploadResponse(raw: string) {
  const body = JSON.parse(raw || '{}')
  if (body.code !== 200 && body.code !== 0) {
    throw new Error(body.message || '上传失败')
  }
  return body.data || {}
}

export const userApi = {
  profile: () => http.get<UserInfo>('/user/info'),
  memberCenter: () => http.get<any>('/user/member-center'),
  submitRealName: (data: any) => http.post('/user/real-name', data, { showLoading: true, loadingText: '提交中' }),
  uploadImage: (filePath: string) => new Promise<{ url: string }>((resolve, reject) => {
    const token = useUserStore().token
    uni.uploadFile({
      url: API_BASE_URL + '/upload/image',
      filePath,
      name: 'file',
      header: token ? { Authorization: token, token } : {},
      success: (res) => {
        try {
          resolve(parseUploadResponse(res.data))
        } catch (error) {
          reject(error)
        }
      },
      fail: (err) => reject(new Error(err.errMsg || '上传失败'))
    })
  }),
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
