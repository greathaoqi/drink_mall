import { http } from '@/utils/request'
export const teamApi = {
  overview: () => http.get<any>('/team/overview'),
  directs: (params?: any) => http.get<any>('/team/directs', params),
  indirects: (params?: any) => http.get<any>('/team/indirects', params)
}
