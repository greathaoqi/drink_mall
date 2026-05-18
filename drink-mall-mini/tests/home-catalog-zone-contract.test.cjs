const assert = require('node:assert/strict')
const { readFileSync } = require('node:fs')
const { join } = require('node:path')
const test = require('node:test')

const root = join(__dirname, '..')
const read = (path) => readFileSync(join(root, path), 'utf8').replace(/^\uFEFF/, '')

test('home/catalog routes cover current zone entry contract without standalone search route', () => {
  const pages = JSON.parse(read('src/pages.json'))
  const paths = pages.pages.map((page) => page.path)

  assert.ok(paths.includes('pages/index/index'))
  assert.ok(paths.includes('pages/product/list'))
  assert.ok(paths.includes('pages/zone/investment/index'))
  assert.ok(paths.includes('pages/zone/gift/index'))
  assert.equal(paths.includes('pages/product/search/index'), false)
  assert.equal(paths.includes('pages/product/category/index'), false)
})

test('home and catalog map all four design zones to backend zoneType values', () => {
  const source = [
    read('src/pages/index/index.vue'),
    read('src/pages/product/list.vue'),
    read('src/pages/zone/investment/index.vue'),
    read('src/pages/zone/gift/index.vue')
  ].join('\n')

  assert.match(source, /zoneType[=:][^,\n]+main/)
  assert.match(source, /zoneType[=:][^,\n]+investment/)
  assert.match(source, /zoneType[=:][^,\n]+retail/)
  assert.match(source, /zoneType[=:][^,\n]+gift/)
  assert.match(source, /主产品专区/)
  assert.match(source, /招商专区/)
  assert.match(source, /零售专区/)
  assert.match(source, /礼包专区/)
})

test('catalog passes backend sortBy values and preserves configured payment labels on cards', () => {
  const list = read('src/pages/product/list.vue')
  const card = read('src/components/ProductCard/ProductCard.vue')

  assert.match(list, /sortBy/)
  assert.match(list, /price_asc/)
  assert.match(list, /price_desc/)
  assert.match(list, /sales/)
  assert.match(card, /payMethodText/)
  assert.doesNotMatch(card, /余额\s*\+\s*酒豆|积分\s*\+\s*现金|组合支付/)
})
