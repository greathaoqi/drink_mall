import { http } from '@/utils/request'
import { useUserStore, type UserInfo } from '@/store/user'

interface LoginResponse {
  userId: number
  nickname: string
  avatarUrl: string
  ageVerified: boolean
  isNewUser: boolean
}

export async function wechatLogin(agreements: {
  userAgreement: boolean
  privacyPolicy: boolean
}): Promise<LoginResponse | null> {
  return new Promise((resolve) => {
    uni.login({
      provider: 'weixin',
      success: async (loginRes) => {
        try {
          const response = await http.post<LoginResponse>('/auth/login', {
            code: loginRes.code,
            userAgreement: agreements.userAgreement,
            privacyPolicy: agreements.privacyPolicy
          })

          if (response.code === 200 && response.data) {
            const userStore = useUserStore()
            userStore.setUser({
              userId: response.data.userId,
              nickname: response.data.nickname || '',
              avatarUrl: response.data.avatarUrl || '',
              ageVerified: response.data.ageVerified || false,
              balance: 0,
              points: 0
            })
            resolve(response.data)
          } else {
            resolve(null)
          }
        } catch (error) {
          console.error('Login failed:', error)
          resolve(null)
        }
      },
      fail: (err) => {
        console.log('WeChat login denied:', err)
        resolve(null)
      }
    })
  })
}

export async function logout(): Promise<void> {
  try {
    await http.post('/auth/logout')
  } catch (error) {
    console.error('Logout failed:', error)
  } finally {
    const userStore = useUserStore()
    userStore.clearUser()
  }
}

export async function checkAuth(): Promise<UserInfo | null> {
  try {
    const response = await http.get<UserInfo>('/auth/check')
    if (response.code === 200 && response.data) {
      const userStore = useUserStore()
      userStore.setUser(response.data)
      return response.data
    }
    return null
  } catch (error) {
    return null
  }
}