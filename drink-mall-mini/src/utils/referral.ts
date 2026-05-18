import type { useUserStore } from '@/store/user'

type UserStore = ReturnType<typeof useUserStore>

export interface ReferralOptions {
  inviteCode?: string | number
  inviter?: string | number
  scene?: string
  productId?: string | number
  sourceProductId?: string | number
  registerSource?: string
}

export interface ReferralPayload {
  inviteCode: string
  scene: string
  sourceProductId: string
}

function text(value?: string | number | null) {
  if (value === undefined || value === null) return ''
  return String(value).trim()
}

function appendParam(params: string[], key: string, value?: string | number | null) {
  const normalized = text(value)
  if (normalized) params.push(`${key}=${encodeURIComponent(normalized)}`)
}

function decode(value: string) {
  try {
    return decodeURIComponent(value)
  } catch {
    return value
  }
}

function parseScene(raw?: string) {
  const decoded = text(raw) ? decode(text(raw)) : ''
  if (!decoded) return {}
  const query = decoded.includes('=') ? decoded : `inviteCode=${decoded}`
  return query.split('&').reduce<Record<string, string>>((acc, part) => {
    const separator = part.indexOf('=')
    const key = separator >= 0 ? part.slice(0, separator) : part
    const value = separator >= 0 ? part.slice(separator + 1) : ''
    if (key) acc[key] = decode(value || '')
    return acc
  }, {})
}

export function parseReferralOptions(options: ReferralOptions = {}): ReferralPayload {
  const sceneParams = parseScene(options.scene)
  const sourceProductId = text(options.sourceProductId || options.productId || sceneParams.sourceProductId || sceneParams.productId)
  const inviteCode = text(options.inviteCode || options.inviter || sceneParams.inviteCode || sceneParams.inviter)
  const registerSource = text(options.registerSource || sceneParams.scene)
  const scene = registerSource || (sourceProductId ? 'share_product' : (inviteCode ? 'share_mini' : ''))

  return { inviteCode, scene, sourceProductId }
}

export function captureReferral(options: ReferralOptions = {}, store: UserStore) {
  const referral = parseReferralOptions(options)
  if (referral.inviteCode) {
    store.setReferral(referral.inviteCode)
  }
  return referral
}

export function buildMiniProgramSharePath(inviteCode?: string | number) {
  const params: string[] = []
  appendParam(params, 'inviteCode', inviteCode)
  appendParam(params, 'scene', inviteCode ? 'share_mini' : '')
  return params.length ? `/pages/index/index?${params.join('&')}` : '/pages/index/index'
}

export function buildProductSharePath(payload: { productId: string | number; inviteCode?: string | number; scene?: string }) {
  const params: string[] = []
  appendParam(params, 'id', payload.productId)
  appendParam(params, 'productId', payload.productId)
  appendParam(params, 'sourceProductId', payload.productId)
  appendParam(params, 'inviteCode', payload.inviteCode)
  appendParam(params, 'scene', payload.scene || 'share_product')
  return `/pages/product/detail/index?${params.join('&')}`
}
