<template>
  <view class="dm-page detail-page">
    <view class="hero">
      <view class="hero-icon">{{ heroIcon }}</view>
      <view class="hero-main">
        <text class="hero-title">{{ order.statusText || statusText }}</text>
        <text class="hero-copy">{{ statusCopy }}</text>
      </view>
    </view>

    <view class="content">
      <view class="address-card dm-card">
        <text class="pin">⌖</text>
        <view class="address-main">
          <text class="receiver">{{ receiverLine }}</text>
          <text class="address">{{ addressLine }}</text>
        </view>
      </view>

      <view class="dm-card panel">
        <view v-for="item in items" :key="item.id || item.productId || item.productName" class="goods-row">
          <image class="thumb" :src="item.productImage || item.mainImage || '/static/images/product-wine.png'" mode="aspectFill" />
          <view class="goods-main">
            <text class="goods-name">{{ item.productName || item.name || '订单商品' }}</text>
            <text class="goods-spec">{{ item.specName || item.skuName || '500ml 单瓶' }}</text>
          </view>
          <view class="goods-side">
            <text class="price">￥{{ money(item.price || item.payAmount || item.salePrice) }}</text>
            <text class="qty">x{{ item.quantity || 1 }}</text>
          </view>
        </view>
      </view>

      <PayMethodSelector v-if="normalizedStatus(order.status) === 'pending'" v-model="payMethod" :methods="methods" title="待付款支付方式" />

      <view class="dm-card panel amount-panel">
        <view class="line"><text>商品总额</text><text>￥{{ money(order.goodsAmount || order.productAmount || order.totalAmount) }}</text></view>
        <view class="line"><text>运费</text><text>{{ freightText }}</text></view>
        <view v-if="discountAmount > 0" class="line"><text>优惠</text><text>-￥{{ money(discountAmount) }}</text></view>
        <view class="line total"><text>实付金额</text><text>￥{{ money(order.payAmount || order.totalAmount) }}</text></view>
      </view>

      <view class="dm-card panel">
        <view v-for="row in metaRows" :key="row.label" class="meta-row">
          <text class="meta-label">{{ row.label }}</text>
          <text class="meta-value">{{ row.value }}</text>
          <text v-if="row.copy" class="copy" @click="copy(row.value)">复制</text>
        </view>
      </view>

      <view v-if="order.commissionText || order.estimatedCommission" class="commission">
        <text>预计佣金 </text>
        <text class="commission-amount">{{ order.commissionText || `￥${money(order.estimatedCommission)}` }}</text>
        <text> · 确认收货后按规则到账</text>
      </view>
    </view>

    <view v-if="hasActions" class="bottom-bar">
      <button v-if="normalizedStatus(order.status) === 'pending'" class="light" @click="openCancelSheet">取消订单</button>
      <button v-if="normalizedStatus(order.status) === 'pending'" class="primary" @click="pay">立即付款</button>
      <button v-if="normalizedStatus(order.status) === 'shipped'" class="light" @click="logistics">查看物流</button>
      <button v-if="normalizedStatus(order.status) === 'shipped'" class="primary" @click="confirm">确认收货</button>
      <button v-if="['paid', 'completed'].includes(normalizedStatus(order.status))" class="light" @click="contact">联系客服</button>
      <button v-if="['paid', 'completed'].includes(normalizedStatus(order.status))" class="primary" @click="aftersale">申请售后</button>
    </view>

    <view v-if="cancelSheetVisible" class="sheet-layer">
      <view class="sheet-mask" @click="cancelSheetVisible = false"></view>
      <view class="cancel-sheet">
        <view class="sheet-head">
          <text class="sheet-title">请选择取消原因</text>
          <text class="sheet-close" @click="cancelSheetVisible = false">×</text>
        </view>
        <text class="sheet-tip">取消后将立即提交处理，已使用的优惠与积分按订单规则返还。</text>
        <view class="reason-list">
          <view
            v-for="reason in cancelReasons"
            :key="reason"
            class="reason"
            :class="{ selected: cancelReason === reason }"
            @click="cancelReason = reason"
          >
            <text class="radio">{{ cancelReason === reason ? '✓' : '' }}</text>
            <text>{{ reason }}</text>
          </view>
        </view>
        <view class="sheet-actions">
          <button class="light" @click="cancelSheetVisible = false">我再想想</button>
          <button class="primary" @click="askCancelConfirm">提交取消</button>
        </view>
      </view>
    </view>

    <view v-if="cancelConfirmVisible" class="modal-mask">
      <view class="confirm-card">
        <text class="confirm-title">确认取消订单？</text>
        <text class="confirm-copy">取消后无法恢复，已使用的优惠权益将按规则退回，积分抵扣将立即返还。</text>
        <view class="confirm-actions">
          <button @click="cancelConfirmVisible = false">再想想</button>
          <button @click="submitCancel">确认取消</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import PayMethodSelector from '@/components/PayMethodSelector/PayMethodSelector.vue'
import { orderApi } from '@/services/order'
import { paymentApi } from '@/services/payment'
import { normalizePayMethods } from '@/utils/business'
import { money } from '@/utils/format'
import { requirePageLogin } from '@/utils/auth'

const id = ref('')
const order = ref<any>({})
const payMethod = ref('')
const methods = ref<any[]>([])
const cancelSheetVisible = ref(false)
const cancelConfirmVisible = ref(false)
const cancelReason = ref('不想买了 / 暂时不需要')

const cancelReasons = [
  '不想买了 / 暂时不需要',
  '商品规格 / 数量选错',
  '其他平台价格更优惠',
  '收货地址填错了',
  '希望更换支付方式',
  '商品发货太慢',
  '重复下单',
  '其他原因'
]

const selectedMethod = computed(() => methods.value.find((m) => m.value === payMethod.value))
const items = computed(() => {
  const rows = order.value.items || order.value.orderItems || []
  return rows.length ? rows : [{ productName: order.value.productName || '订单商品', productImage: order.value.productImage, price: order.value.payAmount || order.value.totalAmount, quantity: order.value.quantity || 1 }]
})
const hasActions = computed(() => ['pending', 'paid', 'shipped', 'completed'].includes(normalizedStatus(order.value.status)))
const statusText = computed(() => {
  const map: Record<string, string> = {
    pending: '等待付款',
    paid: '已付款 · 备货中',
    shipped: '已发货 · 配送中',
    completed: '交易完成',
    cancelled: '订单已取消',
    aftersale: '售后处理中'
  }
  return map[normalizedStatus(order.value.status)] || '订单详情'
})
const statusCopy = computed(() => {
  const state = normalizedStatus(order.value.status)
  if (state === 'pending') return order.value.expireText || '请在倒计时结束前完成支付'
  if (state === 'paid') return '仓库正在为您打包，请耐心等待'
  if (state === 'shipped') return order.value.deliveryEstimate || '预计 5 月 18 日送达'
  if (state === 'completed') return '感谢购买，可再次购买或申请售后'
  return `订单 ${order.value.orderNo || order.value.id || id.value}`
})
const heroIcon = computed(() => {
  const state = normalizedStatus(order.value.status)
  if (state === 'shipped') return '运'
  if (state === 'completed') return '✓'
  if (state === 'pending') return '付'
  return '单'
})
const receiverLine = computed(() => {
  const address = order.value.address || {}
  const name = order.value.receiverName || address.name || '收货人'
  const phone = order.value.receiverPhone || address.phone || ''
  return `${name} ${maskPhone(phone)}`
})
const addressLine = computed(() => {
  const address = order.value.address || {}
  return order.value.receiverAddress || [address.province, address.city, address.district, address.detail].filter(Boolean).join('') || '暂无收货信息'
})
const freightText = computed(() => Number(order.value.freightAmount || order.value.freight || 0) > 0 ? `￥${money(order.value.freightAmount || order.value.freight)}` : '免运费')
const discountAmount = computed(() => Number(order.value.discountAmount || order.value.couponAmount || 0))
const metaRows = computed(() => [
  { label: '订单编号', value: order.value.orderNo || order.value.id || id.value, copy: true },
  { label: '下单时间', value: order.value.createdAt || order.value.createTime || '暂无' },
  { label: '付款时间', value: order.value.paidAt || order.value.payTime || '暂无' },
  { label: '发货时间', value: order.value.shippedAt || order.value.deliveryTime || '暂无' },
  { label: '配送方式', value: order.value.logisticsCompany || order.value.deliveryMethod || '后台录入' },
  { label: '运单号', value: order.value.trackingNo || order.value.expressNo || '暂无', copy: Boolean(order.value.trackingNo || order.value.expressNo) }
])

async function load() {
  if (!requirePageLogin()) return
  order.value = (await orderApi.detail(id.value)).data || {}
  if (normalizedStatus(order.value.status) === 'pending') {
    const raw = order.value.payMethods || order.value.availablePayMethods
    if (Array.isArray(raw) && raw.length) {
      methods.value = normalizePayMethods(raw)
    } else {
      try {
        methods.value = normalizePayMethods((await paymentApi.methods({ scene: 'order', orderId: id.value })).data || [])
      } catch {
        methods.value = []
      }
    }
    payMethod.value = order.value.payMethod || methods.value.find((m) => !m.disabled)?.value || ''
  }
}

function normalizedStatus(value: string) {
  if (['unpaid', 'wait_pay'].includes(value)) return 'pending'
  if (['wait_ship', 'paid'].includes(value)) return 'paid'
  if (['wait_receive', 'delivered', 'shipping'].includes(value)) return 'shipped'
  if (['done', 'finished'].includes(value)) return 'completed'
  return value || 'pending'
}

function maskPhone(phone: string) {
  return String(phone || '').replace(/^(\d{3})\d{4}(\d+)/, '$1 **** $2')
}

function copy(value: any) {
  if (!value || value === '暂无') return
  uni.setClipboardData({ data: String(value) })
}

async function pay() {
  if (!payMethod.value || !selectedMethod.value) {
    uni.showToast({ title: '请选择单一支付方式', icon: 'none' })
    return
  }
  if (selectedMethod.value.disabled) {
    uni.showToast({ title: selectedMethod.value.insufficientText || '当前支付方式不可用', icon: 'none' })
    return
  }
  const res = await paymentApi.pay(id.value, payMethod.value)
  const status = res.data?.status || 'success'
  uni.redirectTo({ url: '/pages/payment/result/index?orderId=' + id.value + '&status=' + status })
}

function openCancelSheet() {
  cancelSheetVisible.value = true
}

function askCancelConfirm() {
  cancelSheetVisible.value = false
  cancelConfirmVisible.value = true
}

async function submitCancel() {
  await orderApi.cancel(id.value, { reason: cancelReason.value })
  cancelConfirmVisible.value = false
  uni.showToast({ title: '订单已取消', icon: 'success' })
  load()
}

async function confirm() {
  await orderApi.confirm(id.value)
  uni.showToast({ title: '已确认收货', icon: 'success' })
  load()
}

function aftersale() {
  uni.navigateTo({ url: '/pages/aftersale/index?orderId=' + id.value })
}

function logistics() {
  uni.navigateTo({ url: '/pages/order/logistics/index?orderId=' + id.value })
}

function contact() {
  uni.navigateTo({ url: '/pages/help/index' })
}

onLoad((options: any) => {
  if (!requirePageLogin()) return
  id.value = options.id || ''
  load()
})
</script>

<style scoped lang="scss">
.detail-page {
  padding-bottom: calc(132rpx + env(safe-area-inset-bottom));
}

.hero {
  display: flex;
  align-items: center;
  gap: 20rpx;
  min-height: 196rpx;
  padding: 28rpx 40rpx 54rpx;
  color: var(--dm-text-on-brown);
  background: var(--dm-grad-brown);
}

.hero-icon {
  width: 88rpx;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 50%;
  text-align: center;
  color: #FFD685;
  background: rgba(228, 165, 22, 0.18);
  font-weight: 800;
}

.hero-main {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.hero-title {
  color: #FFE0A0;
  font-size: 36rpx;
  font-weight: 800;
}

.hero-copy {
  color: rgba(246, 231, 194, 0.72);
  font-size: 24rpx;
}

.content {
  padding: 0 32rpx 28rpx;
}

.address-card {
  display: flex;
  align-items: flex-start;
  gap: 18rpx;
  padding: 28rpx;
  margin-top: -22rpx;
}

.pin {
  color: var(--dm-gold-500);
  font-size: 34rpx;
}

.address-main {
  flex: 1;
  min-width: 0;
}

.receiver,
.address,
.goods-name,
.goods-spec,
.price,
.qty,
.line text,
.meta-row text,
.commission {
  display: block;
}

.receiver {
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 700;
}

.address {
  margin-top: 8rpx;
  color: var(--dm-text-2);
  font-size: 26rpx;
  line-height: 1.5;
}

.panel {
  margin-top: 20rpx;
  padding: 24rpx 28rpx;
}

.goods-row {
  display: flex;
  gap: 20rpx;
  padding: 16rpx 0;
  border-top: 2rpx solid var(--dm-line-soft);
}

.goods-row:first-child {
  border-top: 0;
}

.thumb {
  width: 120rpx;
  height: 120rpx;
  flex: 0 0 120rpx;
  border-radius: 12rpx;
  background: var(--dm-cream-100);
}

.goods-main {
  flex: 1;
  min-width: 0;
}

.goods-name {
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 600;
}

.goods-spec,
.qty,
.meta-label {
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.goods-spec,
.qty {
  margin-top: 6rpx;
}

.goods-side {
  text-align: right;
}

.price {
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 700;
}

.line,
.meta-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
  min-height: 54rpx;
  color: var(--dm-text-2);
  font-size: 26rpx;
}

.line {
  justify-content: space-between;
}

.line.total {
  margin-top: 10rpx;
  padding-top: 16rpx;
  border-top: 2rpx solid var(--dm-line-soft);
  color: var(--dm-text-1);
  font-weight: 800;
}

.line.total text:last-child {
  color: var(--dm-gold-500);
  font-size: 36rpx;
}

.meta-label {
  width: 144rpx;
}

.meta-value {
  flex: 1;
  min-width: 0;
  color: var(--dm-text-1);
  font-size: 25rpx;
}

.copy {
  color: var(--dm-gold-500);
  font-size: 24rpx;
}

.commission {
  margin-top: 20rpx;
  padding: 24rpx 28rpx;
  color: #7A5610;
  background: var(--dm-gold-50);
  border-radius: 20rpx;
  font-size: 24rpx;
}

.commission-amount {
  color: var(--dm-gold-500);
  font-weight: 800;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 30;
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
  padding: 20rpx 28rpx calc(20rpx + env(safe-area-inset-bottom));
  background: #FFFFFF;
  border-top: 2rpx solid var(--dm-line-soft);
  box-shadow: var(--dm-shadow-action);
}

.bottom-bar button,
.sheet-actions button {
  height: 76rpx;
  line-height: 76rpx;
  padding: 0 32rpx;
  border-radius: 999rpx;
  font-size: 26rpx;
  font-weight: 700;
}

.light {
  color: var(--dm-text-2);
  background: #FFFFFF;
  border: 2rpx solid var(--dm-line);
}

.primary {
  color: #3A1A00;
  background: var(--dm-grad-gold);
  border: 0;
  box-shadow: 0 8rpx 20rpx rgba(211, 138, 0, 0.24);
}

.sheet-layer,
.sheet-mask,
.modal-mask {
  position: fixed;
  inset: 0;
}

.sheet-layer,
.modal-mask {
  z-index: 80;
}

.sheet-mask,
.modal-mask {
  background: rgba(0, 0, 0, 0.5);
}

.cancel-sheet {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 81;
  padding: 36rpx 32rpx calc(32rpx + env(safe-area-inset-bottom));
  background: #FFFFFF;
  border-radius: 40rpx 40rpx 0 0;
  box-shadow: var(--dm-shadow-pop);
}

.sheet-head,
.reason,
.sheet-actions,
.confirm-actions {
  display: flex;
  align-items: center;
}

.sheet-head {
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.sheet-title {
  color: var(--dm-text-1);
  font-size: 32rpx;
  font-weight: 800;
}

.sheet-close {
  color: var(--dm-text-3);
  font-size: 44rpx;
}

.sheet-tip {
  display: block;
  color: var(--dm-text-3);
  font-size: 24rpx;
  line-height: 1.5;
}

.reason-list {
  margin-top: 10rpx;
}

.reason {
  gap: 20rpx;
  min-height: 92rpx;
  color: var(--dm-text-2);
  font-size: 28rpx;
  border-bottom: 2rpx solid var(--dm-line-soft);
}

.reason.selected {
  color: var(--dm-text-1);
  font-weight: 700;
}

.radio {
  width: 36rpx;
  height: 36rpx;
  line-height: 36rpx;
  border-radius: 50%;
  text-align: center;
  color: #FFFFFF;
  border: 3rpx solid var(--dm-line);
  font-size: 22rpx;
}

.selected .radio {
  border: 0;
  background: var(--dm-grad-gold);
}

.sheet-actions {
  gap: 20rpx;
  margin-top: 32rpx;
}

.sheet-actions .light {
  flex: 1;
}

.sheet-actions .primary {
  flex: 1.4;
}

.modal-mask {
  display: flex;
  align-items: center;
  justify-content: center;
}

.confirm-card {
  width: 600rpx;
  overflow: hidden;
  padding: 44rpx 40rpx 0;
  background: #FFFFFF;
  border-radius: 28rpx;
  box-shadow: var(--dm-shadow-pop);
}

.confirm-title,
.confirm-copy {
  display: block;
  text-align: center;
}

.confirm-title {
  color: var(--dm-text-1);
  font-size: 34rpx;
  font-weight: 800;
}

.confirm-copy {
  margin-top: 20rpx;
  color: var(--dm-text-2);
  font-size: 26rpx;
  line-height: 1.6;
}

.confirm-actions {
  margin: 36rpx -40rpx 0;
  border-top: 2rpx solid var(--dm-line);
}

.confirm-actions button {
  flex: 1;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 0;
  background: #FFFFFF;
  color: var(--dm-text-2);
  font-size: 32rpx;
}

.confirm-actions button:last-child {
  color: var(--dm-gold-500);
  border-left: 2rpx solid var(--dm-line);
}

button::after {
  border: 0;
}
</style>
