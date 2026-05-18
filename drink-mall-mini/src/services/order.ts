import { http } from '@/utils/request'
export const orderApi = {
  list: (params: any) => http.get<any>('/order', params),
  detail: (id: string | number) => http.get<any>('/order/' + id),
  logistics: (id: string | number) => http.get<any>('/order/' + id + '/logistics'),
  create: (data: any) => http.post<any>('/order', data, { showLoading: true, loadingText: '提交中' }),
  cancel: (id: string | number, data?: any) => http.post('/order/' + id + '/cancel', data || {}),
  confirm: (id: string | number) => http.post('/order/' + id + '/confirm')
}
