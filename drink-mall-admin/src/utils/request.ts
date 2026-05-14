import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) config.headers.Authorization = token
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res?.code && res.code !== 200) {
      const message = res.message || '请求处理失败'
      ElMessage.error(message)
      return Promise.reject(new Error(message))
    }
    return res
  },
  error => {
    const status = error.response?.status
    if (status === 401 || status === 403) {
      localStorage.removeItem('token')
      localStorage.removeItem('adminUser')
      if (window.location.pathname !== '/login') window.location.href = '/login'
    }
    ElMessage.error(error.response?.data?.message || error.message || '网络请求失败')
    return Promise.reject(error)
  }
)

export default request
