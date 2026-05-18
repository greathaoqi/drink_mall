export interface NormalizedPayMethod {
  value: string
  label: string
  tip?: string
  disabled?: boolean
  insufficientText?: string
}

export function normalizePayMethods(raw: any[] = []): NormalizedPayMethod[] {
  return raw.map((m) => ({
    value: m.value || m.code || m.payMethod || m.method,
    label: m.label || m.text || m.name || m.title || m.value || m.code,
    tip: m.tip || m.description,
    disabled: Boolean(m.disabled || m.available === false),
    insufficientText: m.insufficientText || m.unavailableReason || m.reason
  })).filter((m) => m.value)
}

export function zoneOf(item: any) {
  return item.zoneType || item.productZoneType || item.zoneCode || item.product?.zoneType || ''
}

export function isGiftItem(item: any) {
  return zoneOf(item) === 'gift'
}

export function isInvestmentItem(item: any) {
  return zoneOf(item) === 'investment'
}

export function giftPointsPrice(item: any) {
  return Number(item.pointsPrice ?? item.pointPrice ?? item.price ?? 0)
}

export function canUseSinglePayMethod(items: any[], payMethod: string) {
  if (!items.length || !payMethod) return false
  if (items.some(isGiftItem)) return payMethod === 'points'
  return true
}
