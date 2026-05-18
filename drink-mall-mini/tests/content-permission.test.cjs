const assert = require('node:assert/strict')
const { readFileSync } = require('node:fs')
const { join } = require('node:path')
const test = require('node:test')

const root = join(__dirname, '..')
const read = (path) => readFileSync(join(root, path), 'utf8')

test('content list passes content type into detail navigation', () => {
  const source = read('src/pages/content/list/index.vue')

  assert.match(source, /const contentType = item\.type \|\| activeType\.value \|\| 'article'/)
  assert.match(source, /'&type='\+contentType/)
})

test('content detail trusts backend canView without local paid or price bypass', () => {
  const source = read('src/pages/content/detail/index.vue')
  const canViewLine = source.match(/const canView = computed\(\(\) => ([^\n]+)\)/)?.[1] || ''

  assert.match(canViewLine, /detail\.value\.canView/)
  assert.doesNotMatch(canViewLine, /detail\.value\.paid/)
  assert.doesNotMatch(canViewLine, /detail\.value\.price/)
})

test('content detail normalizes backend payment method code and text fields', () => {
  const source = read('src/pages/content/detail/index.vue')

  assert.match(source, /m\.code/)
  assert.match(source, /m\.text/)
})

test('content detail sends content type and selected pay method when buying', () => {
  const service = read('src/services/content.ts')
  const detail = read('src/pages/content/detail/index.vue')

  assert.match(service, /buy: \(id: string \| number, type: string, payMethod: string\)/)
  assert.match(detail, /contentApi\.buy\(id\.value, type\.value, payMethod\.value\)/)
})

test('content worker pages do not contain common mojibake fragments', () => {
  const files = [
    'src/pages/content/list/index.vue',
    'src/pages/content/detail/index.vue',
    'src/pages/announcement/list/index.vue',
    'src/pages/announcement/detail/index.vue',
    'src/pages/video/index.vue',
    'src/pages/help/index.vue'
  ]

  for (const file of files) {
    const source = read(file)
    assert.doesNotMatch(source, /�|瑙嗛|鍥炬|鍏|鍔ㄦ|璐拱|鏆傛棤|绛夌骇|鏉冮檺/, file)
  }
})

test('content hub exposes design tabs and navigates announcements without pages.json changes', () => {
  const source = read('src/pages/content/list/index.vue')

  assert.match(source, /推荐/)
  assert.match(source, /公告/)
  assert.match(source, /品酒知识/)
  assert.match(source, /联营商动态/)
  assert.match(source, /\/pages\/announcement\/list\/index/)
})

test('video and help pages render owned experiences instead of redirect shims', () => {
  const video = read('src/pages/video/index.vue')
  const help = read('src/pages/help/index.vue')

  assert.doesNotMatch(video, /redirectTo/)
  assert.match(video, /contentApi\.list\(\{ type: 'video' \}\)/)
  assert.doesNotMatch(help, /redirectTo/)
  assert.match(help, /configApi\.customerService\(\)/)
})
