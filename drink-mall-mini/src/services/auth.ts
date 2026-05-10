import { http } from '@/utils/request'
import { useUserStore, type UserInfo } from '@/store/user'

interface LoginResponse {
  userId: number
  token: string
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
          }, { requireAuth: false })

          if (response.code === 200 && response.data) {
            const userStore = useUserStore()
            userStore.setUser({
              userId: response.data.userId,
              token: response.data.token,
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

export async function demoLogin(): Promise<LoginResponse | null> {
  try {
    const response = await http.post<LoginResponse>('/auth/demo-login', {}, { requireAuth: false })
    if (response.code === 200 && response.data) {
      const userStore = useUserStore()
      userStore.setUser({
        userId: response.data.userId,
        token: response.data.token,
        nickname: response.data.nickname || '演示用户',
        avatarUrl: response.data.avatarUrl || '',
        ageVerified: response.data.ageVerified || true,
        balance: 9999,
        points: 1888
      })
      await checkAuth()
      return response.data
    }
    return null
  } catch (error) {
    console.error('Demo login failed:', error)
    return null
  }
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
      userStore.setUser({ ...response.data, token: userStore.token })
      return response.data
    }
    return null
  } catch (error) {
    return null
  }
}
