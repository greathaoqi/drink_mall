import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface UserInfo {
  userId: number
  token?: string
  nickname: string
  avatarUrl: string
  ageVerified: boolean
  balance: number
  points: number
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string>(uni.getStorageSync('token') || '')
  const isLoggedIn = computed(() => userInfo.value !== null)
  const ageVerified = computed(() => userInfo.value?.ageVerified ?? false)

  function setUser(user: UserInfo | null) {
    userInfo.value = user
    if (user?.token) {
      token.value = user.token
      uni.setStorageSync('token', user.token)
    }
  }

  function setToken(value: string) {
    token.value = value
    uni.setStorageSync('token', value)
  }

  function setAgeVerified(verified: boolean) {
    if (userInfo.value) {
      userInfo.value.ageVerified = verified
    }
  }

  function clearUser() {
    userInfo.value = null
    token.value = ''
    uni.removeStorageSync('token')
  }

  return {
    userInfo,
    token,
    isLoggedIn,
    ageVerified,
    setUser,
    setToken,
    setAgeVerified,
    clearUser
  }
})
