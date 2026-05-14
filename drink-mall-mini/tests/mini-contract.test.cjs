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
  assert.doesNotMatch(script, /thirdLevel|children|tree|expand|level3/)
  assert.doesNotMatch(source, /v-for=".*children|@click=".*expand/)
})

test('checkout page blocks disabled payment methods before creating order', () => {
  const source = read('src/pages/checkout/index.vue')

  assert.match(source, /selectedMethod\.value\.disabled/)
  assert.match(source, /orderApi\.create/)
  assert.match(source, /uni\.showToast/)
})
