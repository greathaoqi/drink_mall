import { http } from '@/utils/request'
export const contentApi = {
  list: (params: any) => http.get<any>('/content', params, { requireAuth: false }),
  detail: (id: string | number, type = 'video') => http.get<any>('/content/' + id, { type }, { requireAuth: false }),
  buy: (id: string | number, type: string, payMethod: string) => http.post('/content/' + id + '/buy?type=' + encodeURIComponent(type), { payMethod, type }, { showLoading: true, loadingText: '购买中' }),
  announcements: () => http.get<any[]>('/content/announcements', {}, { requireAuth: false }),
  announcementDetail: (id: string | number) => http.get<any>('/content/announcements/' + id, {}, { requireAuth: false })
}
