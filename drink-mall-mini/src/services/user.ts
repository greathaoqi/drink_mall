import { http } from '@/utils/request'
import { useUserStore, type UserInfo } from '@/store/user'

export async function getUserInfo(): Promise<UserInfo | null> {
  try {
    const response = await http.get<UserInfo>('/user/info')
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

export async function verifyAge(): Promise<boolean> {
  try {
    const response = await http.post('/user/verify-age')
    if (response.code === 200) {
      const userStore = useUserStore()
      userStore.setAgeVerified(true)
      return true
    }
    return false
  } catch (error) {
    console.error('Age verification failed:', error)
    return false
  }
}