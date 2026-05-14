import { http } from '@/utils/request'
export const cartApi = {
  list: () => http.get<any>('/cart'),
  add: (data: any) => http.post('/cart', data, { showLoading: true, loadingText: '加入中' }),
  updateQuantity: (cartId: string | number, quantity: number) => http.put('/cart/' + cartId + '/quantity?quantity=' + encodeURIComponent(quantity)),
  select: (cartId: string | number, selected: boolean) => http.put('/cart/' + cartId + '/selected?selected=' + encodeURIComponent(selected)),
  remove: (cartId: string | number) => http.delete('/cart/' + cartId)
}
