import { http } from '@/utils/request'

export const productApi = {
  categories: () => http.get<any[]>('/public/categories', {}, { requireAuth: false }),
  list: (params: any = {}) => {
    const query = { ...params, sortBy: params.sortBy ?? params.sort }
    delete query.sort
    return http.get<any>('/public/products', query, { requireAuth: false })
  },
  detail: (id: string | number) => http.get<any>('/public/products/' + id, {}, { requireAuth: false }),
  payMethods: (id: string | number) => http.get<any[]>('/public/products/' + id + '/pay-methods', {}, { requireAuth: false }),
  redeemGift: (id: string | number, quantity = 1) => http.post('/points/redeem/' + id, { payMethod: 'points', quantity }, { showLoading: true, loadingText: '兑换中' })
}
