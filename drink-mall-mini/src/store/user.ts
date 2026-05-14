import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export interface UserInfo {
  userId: number | string
  nickname?: string
  avatarUrl?: string
  mobile?: string
  realNameStatus?: string
  levelName?: string
  inviteCode?: string
  inviterName?: string
  balance?: number | string
  frozenBalance?: number | string
  df?: number | string
  wineBean?: number | string
  points?: number | string
  optionValue?: number | string
  showOption?: boolean
  token?: string
}

const TOKEN_KEY = 'drink_mall_token'
const USER_KEY = 'drink_mall_user'
const REFERRAL_KEY = 'drink_mall_referral'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(uni.getStorageSync(TOKEN_KEY) || uni.getStorageSync('token') || '')
  const userInfo = ref<UserInfo | null>(uni.getStorageSync(USER_KEY) || null)
  const referralCode = ref<string>(uni.getStorageSync(REFERRAL_KEY) || '')
  const isLoggedIn = computed(() => !!token.value)
  const realNameApproved = computed(() => userInfo.value?.realNameStatus === 'APPROVED')

  function setReferral(code?: string) {
    referralCode.value = code || ''
    if (code) uni.setStorageSync(REFERRAL_KEY, code)
  }

  function setToken(value: string) {
    token.value = value || ''
    if (value) {
      uni.setStorageSync(TOKEN_KEY, value)
      uni.setStorageSync('token', value)
    }
  }

  function setUser(user: UserInfo | null) {
    userInfo.value = user
    if (user?.token) setToken(user.token)
    if (user) uni.setStorageSync(USER_KEY, user)
  }

  function patchUser(partial: Partial<UserInfo>) {
    setUser({ ...(userInfo.value || {}), ...partial } as UserInfo)
  }

  function clearUser() {
    token.value = ''
    userInfo.value = null
    uni.removeStorageSync(TOKEN_KEY)
    uni.removeStorageSync('token')
    uni.removeStorageSync(USER_KEY)
  }

  return { token, userInfo, referralCode, isLoggedIn, realNameApproved, setReferral, setToken, setUser, patchUser, clearUser }
})
