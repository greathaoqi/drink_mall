import { http } from '@/utils/request'
export const paymentApi = {
  methods: (params?: any) => http.get<any[]>('/payment/methods', params),
  pay: (orderId: string | number, payMethod: string) => http.post<any>('/payment/pay', { orderId, payMethod }, { showLoading: true, loadingText: '支付中' })
}
