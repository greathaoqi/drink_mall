const assert = require('node:assert/strict')
const { readFileSync } = require('node:fs')
const { join } = require('node:path')
const test = require('node:test')

const root = join(__dirname, '..')
const read = (path) => readFileSync(join(root, path), 'utf8')

test('product detail accepts shared productId and blocks missing id requests', () => {
  const detail = read('src/pages/product/detail/index.vue')

  assert.match(detail, /normalizeProductId/)
  assert.match(detail, /if\s*\(!productId\.value\)/)
  assert.doesNotMatch(detail, /productId\.value\s*=\s*options\.id\s*\|\|\s*['"]{2}/)
})

test('product service rejects empty product id before calling product endpoints', () => {
  const productService = read('src/services/product.ts')

  assert.match(productService, /normalizeProductId/)
  assert.match(productService, /商品ID缺失/)
  assert.match(productService, /detail:\s*\(id:[^)]+\)\s*=>\s*http\.get<any>\('\/public\/products\/'\s*\+\s*requireProductId\(id\)/)
  assert.match(productService, /payMethods:\s*\(id:[^)]+\)\s*=>\s*http\.get<any\[\]>\('\/public\/products\/'\s*\+\s*requireProductId\(id\)/)
})

test('product navigation centralizes id extraction and does not concatenate product.id directly', () => {
  const productUtil = read('src/utils/product.ts')
  const navigationSources = [
    read('src/pages/index/index.vue'),
    read('src/pages/product/list.vue'),
    read('src/pages/zone/investment/index.vue'),
    read('src/pages/zone/gift/index.vue')
  ].join('\n')

  assert.match(productUtil, /\.id/)
  assert.match(productUtil, /\.productId/)
  assert.match(productUtil, /\.product_id/)
  assert.match(productUtil, /normalized === 'undefined'/)
  assert.match(navigationSources, /buildProductDetailUrl/)
  assert.doesNotMatch(navigationSources, /\/pages\/product\/detail\/index\?id=['"]?\s*\+\s*product\.id/)
})

test('bottom action bar avoids mini program component disallowed tag selectors', () => {
  const source = read('src/components/BottomActionBar/BottomActionBar.vue')
  const style = source.match(/<style[^>]*>([\s\S]*?)<\/style>/)?.[1] || ''

  assert.doesNotMatch(style, /:deep\(button\)/)
  assert.doesNotMatch(style, /(^|\n)\s*button\b/)
  assert.doesNotMatch(style, /button::?after/)
})
