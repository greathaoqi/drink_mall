import { http } from '@/utils/request'
export const aftersaleApi = {
  apply: (data: any) => http.post('/aftersale', data, { showLoading: true, loadingText: '提交中' }),
  list: (params?: any) => http.get<any>('/aftersale', params),
  detail: (id: string | number) => http.get<any>('/aftersale/' + id)
}
