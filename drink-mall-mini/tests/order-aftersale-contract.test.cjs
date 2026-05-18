const assert = require('node:assert/strict')
const { readFileSync } = require('node:fs')
const { join } = require('node:path')
const test = require('node:test')

const root = join(__dirname, '..')
const read = (path) => readFileSync(join(root, path), 'utf8')

test('order pages expose design status tabs and cancellation contracts', () => {
  const list = read('src/pages/order/list/index.vue')
  const detail = read('src/pages/order/detail/index.vue')
  const logistics = read('src/pages/order/logistics/index.vue')
  const source = list + detail + logistics

  for (const label of ['全部', '待付款', '待发货', '待收货', '已完成', '售后']) {
    assert.match(list, new RegExp(label))
  }

  for (const reason of ['不想买了 / 暂时不需要', '商品规格 / 数量选错', '收货地址填错了', '希望更换支付方式']) {
    assert.match(detail, new RegExp(reason.replaceAll('/', '\\/')))
  }

  assert.match(detail, /确认取消订单/)
  assert.match(detail, /取消后无法恢复/)
  assert.match(logistics, /一期由后台人工录入物流信息/)
  assert.doesNotMatch(source, /锟|�|璁|鍞|鐗|鏆|寰|宸|绔/)
})

test('aftersale pages match phase-one apply and manual review contract', () => {
  const apply = read('src/pages/aftersale/index.vue')
  const detail = read('src/pages/aftersale/detail/index.vue')
  const source = apply + detail

  assert.match(apply, /售后申请/)
  assert.match(apply, /仅退款/)
  assert.match(apply, /后台审核/)
  assert.match(apply, /线下处理/)
  assert.match(apply, /提交申请/)
  assert.match(detail, /退款处理中/)
  assert.match(detail, /商家审核/)
  assert.match(detail, /联系客服/)
  assert.doesNotMatch(source, /第三方物流|线上退换货物流闭环/)
  assert.doesNotMatch(source, /锟|�|璁|鍞|鐗|鏆|寰|宸|绔/)
})
