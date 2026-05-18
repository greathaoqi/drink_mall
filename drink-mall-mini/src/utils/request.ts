import { useUserStore } from '@/store/user'

export interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
  showLoading?: boolean
  showError?: boolean
  requireAuth?: boolean
  loadingText?: string
  timeout?: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://hy.ajiu.lol/api/v1'
let redirectingLogin = false

function buildUrl(url: string, data: any, method: string) {
  if (method !== 'GET' || !data) return API_BASE_URL + url
  const query = Object.keys(data)
    .filter((key) => data[key] !== undefined && data[key] !== null && data[key] !== '')
    .map((key) => encodeURIComponent(key) + '=' + encodeURIComponent(data[key]))
    .join('&')
  return API_BASE_URL + url + (query ? (url.includes('?') ? '&' : '?') + query : '')
}

function normalizeResponse<T>(raw: any): ApiResponse<T> {
  if (raw && typeof raw.code === 'number') return raw
  if (raw && typeof raw.success === 'boolean') return { code: raw.success ? 200 : 500, message: raw.message || '', data: raw.data }
  return { code: 200, message: 'ok', data: raw as T }
}

function goLogin() {
  if (redirectingLogin) return
  redirectingLogin = true
  uni.navigateTo({ url: '/pages/login/index', complete: () => setTimeout(() => { redirectingLogin = false }, 500) })
}

export async function request<T = any>(options: RequestOptions): Promise<ApiResponse<T>> {
  const { url, method = 'GET', data, header = {}, showLoading = false, showError = true, requireAuth = true, loadingText = '加载中', timeout = 30000 } = options
  const userStore = useUserStore()

  if (requireAuth && !userStore.token) {
    goLogin()
    throw new Error('请先登录')
  }
  if (showLoading) uni.showLoading({ title: loadingText, mask: true })

  try {
    const response = await new Promise<ApiResponse<T>>((resolve, reject) => {
      const timer = setTimeout(() => reject(new Error('请求超时，请检查网络后重试')), timeout)
      uni.request({
        url: buildUrl(url, data, method),
        method,
        data: method === 'GET' ? undefined : data,
        header: {
          'Content-Type': 'application/json',
          ...(userStore.token ? { Authorization: userStore.token, token: userStore.token } : {}),
          ...header
        },
        timeout,
        success: (res) => {
          clearTimeout(timer)
          if (res.statusCode === 401) {
            userStore.clearUser()
            if (requireAuth) goLogin()
            reject(new Error('登录已过期，请重新登录'))
            return
          }
          if (res.statusCode < 200 || res.statusCode >= 300) {
            reject(new Error('接口请求失败：' + res.statusCode))
            return
          }
          const body = normalizeResponse<T>(res.data)
          if (body.code === 401) {
            userStore.clearUser()
            if (requireAuth) goLogin()
            reject(new Error(body.message || '登录已过期，请重新登录'))
            return
          }
          if (body.code !== 200 && body.code !== 0) {
            reject(new Error(body.message || '操作失败'))
            return
          }
          resolve(body)
        },
        fail: (err) => {
          clearTimeout(timer)
          reject(new Error(err.errMsg || '网络异常，请稍后重试'))
        }
      })
    })
    return response
  } catch (error: any) {
    if (showError) uni.showToast({ title: error.message || '请求失败', icon: 'none' })
    throw error
  } finally {
    if (showLoading) uni.hideLoading()
  }
}

export const http = {
  get<T = any>(url: string, data?: any, options?: Partial<RequestOptions>) { return request<T>({ url, method: 'GET', data, ...options }) },
  post<T = any>(url: string, data?: any, options?: Partial<RequestOptions>) { return request<T>({ url, method: 'POST', data, ...options }) },
  put<T = any>(url: string, data?: any, options?: Partial<RequestOptions>) { return request<T>({ url, method: 'PUT', data, ...options }) },
  delete<T = any>(url: string, data?: any, options?: Partial<RequestOptions>) { return request<T>({ url, method: 'DELETE', data, ...options }) }
}

export default http
