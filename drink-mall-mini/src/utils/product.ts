export function normalizeProductId(productOrId: any) {
  const raw = typeof productOrId === 'object' && productOrId !== null
    ? productOrId.id ?? productOrId.productId ?? productOrId.product_id ?? productOrId.goodsId
    : productOrId
  const normalized = String(raw ?? '').trim()
  if (!normalized || normalized === 'undefined' || normalized === 'null') return ''
  return normalized
}

export function buildProductDetailUrl(productOrId: any, extraQuery: Record<string, string | number | boolean | undefined> = {}) {
  const id = normalizeProductId(productOrId)
  if (!id) return ''
  const params = [`id=${encodeURIComponent(id)}`]
  Object.entries(extraQuery).forEach(([key, value]) => {
    if (value === undefined || value === '') return
    params.push(`${key}=${encodeURIComponent(String(value))}`)
  })
  return `/pages/product/detail/index?${params.join('&')}`
}
