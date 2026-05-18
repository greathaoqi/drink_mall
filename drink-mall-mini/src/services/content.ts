import { http } from '@/utils/request'
export const contentApi = {
  // D-SEARCH-04: Search UI on content list page
  list: (params: any) => http.get<any>('/content', {
    type: params.type,
    keyword: params.keyword,      // D-SEARCH-04: search keyword
    categoryId: params.categoryId, // D-CAT-05: category filter
    page: params.page || 1,
    size: params.size || 10
  }, { requireAuth: false }),
  detail: (id: string | number, type = 'video') => http.get<any>('/content/' + id, { type }, { requireAuth: false }),
  buy: (id: string | number, type: string, payMethod: string) => http.post('/content/' + id + '/buy?type=' + encodeURIComponent(type), { payMethod, type }, { showLoading: true, loadingText: '购买中' }),
  // D-LIKE-01: Like toggle endpoint
  like: (id: string | number, type: string) => http.post('/content/' + id + '/like', null, { params: { type } }),
  // D-CAT-02, D-CAT-05: Get categories for filter dropdown (public endpoint)
  getCategories: () => http.get<any[]>('/content/categories', {}, { requireAuth: false }),
  announcements: () => http.get<any[]>('/content/announcements', {}, { requireAuth: false }),
  announcementDetail: (id: string | number) => http.get<any>('/content/announcements/' + id, {}, { requireAuth: false })
}
