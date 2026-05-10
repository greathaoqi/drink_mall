import { useUserStore } from '@/store/user'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
  showLoading?: boolean
  showError?: boolean
  requireAuth?: boolean  // 是否需要登录，默认true，public接口设为false
}

interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

const BASE_URL = 'http://localhost:8080/api/v1'

async function request<T = any>(options: RequestOptions): Promise<ApiResponse<T>> {
  const { url, method = 'GET', data, header = {}, showLoading = false, showError = true, requireAuth = true } = options

  if (showLoading) {
    uni.showLoading({ title: '加载中...' })
  }

  try {
    const response = await new Promise<ApiResponse<T>>((resolve, reject) => {
      uni.request({
        url: BASE_URL + url,
        method,
        data,
        header: {
          'Content-Type': 'application/json',
          ...(useUserStore().token ? { Authorization: useUserStore().token } : {}),
          ...header
        },
        withCredentials: true,
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data as ApiResponse<T>)
          } else if (res.statusCode === 401) {
            // 只对需要认证的接口清除用户状态并报错
            if (requireAuth) {
              const userStore = useUserStore()
              userStore.clearUser()
              reject(new Error('请先登录'))
            } else {
              // 公共接口401直接返回空数据
              resolve({ code: 401, message: '未授权', data: null as T })
            }
          } else {
            reject(new Error(`请求失败: ${res.statusCode}`))
          }
        },
        fail: (err) => {
          reject(new Error(err.errMsg || '网络请求失败'))
        }
      })
    })

    return response
  } catch (error: any) {
    if (showError) {
      uni.showToast({
        title: error.message || '网络错误',
        icon: 'none'
      })
    }
    throw error
  } finally {
    if (showLoading) {
      uni.hideLoading()
    }
  }
}

const http = {
  get<T = any>(url: string, data?: any, options?: Partial<RequestOptions>) {
    return request<T>({ url, method: 'GET', data, ...options })
  },
  post<T = any>(url: string, data?: any, options?: Partial<RequestOptions>) {
    return request<T>({ url, method: 'POST', data, ...options })
  },
  put<T = any>(url: string, data?: any, options?: Partial<RequestOptions>) {
    return request<T>({ url, method: 'PUT', data, ...options })
  },
  delete<T = any>(url: string, data?: any, options?: Partial<RequestOptions>) {
    return request<T>({ url, method: 'DELETE', data, ...options })
  }
}

export { http }
export { request }
export default http
