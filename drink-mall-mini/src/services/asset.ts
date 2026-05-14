import { http } from '@/utils/request'
export const assetApi = {
  overview: () => http.get<any>('/asset/overview'),
  records: (assetType: string, params?: any) => http.get<any>('/asset/records', { assetType, ...(params || {}) }),
  withdraw: (data: any) => http.post('/asset/withdraw', data, { showLoading: true, loadingText: '提交中' }),
  transferDf: (data: any) => http.post('/asset/df/transfer', data, { showLoading: true, loadingText: '赠送中' })
}
