import { http } from '@/utils/request'
export const configApi = {
  banners: () => http.get<any[]>('/public/banners', {}, { requireAuth: false, showError: false }),
  announcements: () => http.get<any[]>('/content/announcements', {}, { requireAuth: false, showError: false }),
  system: (keys?: string[]) => http.get<any>('/config/system', { keys: keys?.join(',') }, { requireAuth: false, showError: false }),
  customerService: () => http.get<any>('/config/customer-service', {}, { requireAuth: false, showError: false })
}
