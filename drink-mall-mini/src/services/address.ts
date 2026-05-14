import { http } from '@/utils/request'
export const addressApi = {
  list: () => http.get<any[]>('/address'),
  detail: (id: string | number) => http.get<any>('/address/' + id),
  default: () => http.get<any>('/address/default'),
  create: (data: any) => http.post('/address', data, { showLoading: true, loadingText: '保存中' }),
  update: (id: string | number, data: any) => http.put('/address/' + id, data, { showLoading: true, loadingText: '保存中' }),
  remove: (id: string | number) => http.delete('/address/' + id),
  setDefault: (id: string | number) => http.put('/address/' + id + '/default')
}
