export function money(value: any) { return Number(value || 0).toFixed(2) }
export function listOf<T = any>(data: any): T[] { return Array.isArray(data) ? data : (data?.records || data?.list || data?.items || []) }
export function assetName(type: string) {
  const map: Record<string, string> = { balance: '余额', df: 'DF', wineBean: '酒豆', points: '积分', option: '期权' }
  return map[type] || type
}
