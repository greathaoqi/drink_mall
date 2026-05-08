import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface UserInfo {
  userId: number
  nickname: string
  avatarUrl: string
  ageVerified: boolean
  balance: number
  points: number
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = computed(() => userInfo.value !== null)
  const ageVerified = computed(() => userInfo.value?.ageVerified ?? false)

  function setUser(user: UserInfo | null) {
    userInfo.value = user
  }

  function setAgeVerified(verified: boolean) {
    if (userInfo.value) {
      userInfo.value.ageVerified = verified
    }
  }

  function clearUser() {
    userInfo.value = null
  }

  return {
    userInfo,
    isLoggedIn,
    ageVerified,
    setUser,
    setAgeVerified,
    clearUser
  }
})