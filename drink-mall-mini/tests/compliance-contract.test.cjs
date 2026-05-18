const assert = require('node:assert/strict')
const { readFileSync } = require('node:fs')
const { join } = require('node:path')
const test = require('node:test')

const root = join(__dirname, '..')
const read = (path) => readFileSync(join(root, path), 'utf8')

test('team service exposes only compliant mini-program team endpoints', () => {
  const source = read('src/services/team.ts')

  assert.match(source, /\/team\/overview/)
  assert.match(source, /\/team\/directs/)
  assert.match(source, /\/team\/indirects/)
  assert.doesNotMatch(source, /third|tree|children|descendants|level3|三级/)
})

test('team page renders direct and indirect lists without expandable tree data', () => {
  const source = read('src/pages/team/index.vue')
  const script = source.match(/<script setup lang="ts">([\s\S]*?)<\/script>/)?.[1] || ''

  assert.match(source, /directs/)
  assert.match(source, /indirects/)
  assert.match(source, /我的邀请人/)
  assert.match(source, /直推人员/)
  assert.match(source, /间推人员/)
  assert.match(source, /团队总人数/)
  assert.doesNotMatch(script, /thirdLevel|children|tree|expand|level3/)
  assert.doesNotMatch(source, /v-for=".*children|@click=".*expand/)
  assert.doesNotMatch(source, /三级|树状图|无限层级|裂变收益/)
})

test('checkout page blocks disabled payment methods before creating order', () => {
  const source = read('src/pages/checkout/index.vue')

  assert.match(source, /selectedMethod\.value\.disabled/)
  assert.match(source, /orderApi\.create/)
  assert.match(source, /uni\.showToast/)
})

test('profile and distribution navigation excludes phase-one blocked screens', () => {
  const profile = read('src/pages/profile/index.vue')
  const level = read('src/pages/distribution/level/index.vue')
  const settings = read('src/pages/settings/index.vue')
  const source = profile + level + settings

  assert.doesNotMatch(source, /优惠券|消息中心|体验店|合伙人/)
  assert.doesNotMatch(source, /coupon|message-center|experience-store|partner/i)
  assert.match(profile, /我的资产/)
  assert.match(profile, /我的团队/)
  assert.match(profile, /分享邀请/)
  assert.match(profile, /实名认证/)
})

test('asset pages keep independent account labels and expose df gift states through existing routes', () => {
  const wallet = read('src/pages/wallet/index.vue')
  const history = read('src/pages/wallet/history/index.vue')
  const source = wallet + history

  for (const label of ['可提现余额', '冻结余额', 'DF', '酒豆', '积分', '期权']) {
    assert.match(source, new RegExp(label))
  }

  assert.match(wallet, /type=dfGift/)
  assert.match(wallet, /type=dfGiftRecords/)
  assert.match(history, /dfGift/)
  assert.match(history, /dfGiftRecords/)
  assert.doesNotMatch(source, /余额\s*\+\s*酒豆|积分\s*\+\s*现金|积分\s*\+\s*酒豆|组合支付/)
})
