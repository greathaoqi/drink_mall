const assert = require('node:assert/strict')
const { existsSync, readFileSync } = require('node:fs')
const { join } = require('node:path')
const test = require('node:test')

const root = join(__dirname, '..')
const file = (path) => join(root, path)
const read = (path) => readFileSync(file(path), 'utf8')

test('referral utility centralizes share path and scene parsing', () => {
  const utilityPath = 'src/utils/referral.ts'

  assert.equal(existsSync(file(utilityPath)), true)

  const source = read(utilityPath)
  assert.match(source, /parseReferralOptions/)
  assert.match(source, /buildMiniProgramSharePath/)
  assert.match(source, /buildProductSharePath/)
  assert.match(source, /decodeURIComponent/)
  assert.match(source, /inviteCode/)
  assert.match(source, /sourceProductId/)
})

test('mini program pages persist referral code from share or qr scene', () => {
  const app = read('src/App.vue')
  const index = read('src/pages/index/index.vue')
  const detail = read('src/pages/product/detail/index.vue')
  const login = read('src/pages/login/index.vue')

  assert.match(app, /parseReferralOptions/)
  assert.match(index, /captureReferral/)
  assert.match(detail, /captureReferral/)
  assert.match(login, /parseReferralOptions/)
})

test('share paths include the current user invite code and product source when available', () => {
  const index = read('src/pages/index/index.vue')
  const detail = read('src/pages/product/detail/index.vue')

  assert.match(index, /buildMiniProgramSharePath\(store\.userInfo\?\.inviteCode/)
  assert.match(detail, /buildProductSharePath\([^)]*inviteCode:\s*useUserStore\(\)\.userInfo\?\.inviteCode/)
  assert.match(detail, /scene:\s*'share_product'/)
})
