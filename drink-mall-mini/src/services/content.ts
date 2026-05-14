import { http } from '@/utils/request'
export const contentApi = {
  list: (params: any) => http.get<any>('/content', params, { requireAuth: false }),
  detail: (id: string | number) => http.get<any>('/content/' + id, {}, { requireAuth: false }),
  buy: (id: string | number, payMethod: string) => http.post('/content/' + id + '/buy', { payMethod }, { showLoading: true, loadingText: '购买中' }),
  announcements: () => http.get<any[]>('/content/announcements', {}, { requireAuth: false }),
  announcementDetail: (id: string | number) => http.get<any>('/content/announcements/' + id, {}, { requireAuth: false })
}
