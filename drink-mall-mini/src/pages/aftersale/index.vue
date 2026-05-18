<template>
  <view class="dm-page aftersale-page">
    <view class="page-title">
      <text>售后申请</text>
    </view>

    <view class="goods-card dm-card">
      <image class="thumb" :src="firstItem.productImage || firstItem.mainImage || '/static/images/product-wine.png'" mode="aspectFill" />
      <view class="goods-main">
        <text class="goods-name">{{ firstItem.productName || firstItem.name || '订单商品' }}</text>
        <text class="goods-spec">{{ firstItem.specName || firstItem.skuName || '500ml 单瓶' }} x{{ firstItem.quantity || 1 }}</text>
        <text class="price">￥{{ money(firstItem.price || order.payAmount || order.totalAmount) }}</text>
      </view>
    </view>

    <view class="form-card dm-card">
      <view class="form-row">
        <text class="label">售后类型</text>
        <picker :range="types" @change="typeIndex = Number($event.detail.value)">
          <view class="field">{{ types[typeIndex] }}</view>
        </picker>
        <text class="chevron">›</text>
      </view>
      <view class="form-row">
        <text class="label">退款金额</text>
        <text class="field price">￥{{ money(refundAmount) }}</text>
      </view>
      <view class="form-row no-border">
        <text class="label">退款原因</text>
        <picker :range="reasons" @change="reasonIndex = Number($event.detail.value)">
          <view class="field" :class="{ placeholder: !reasons[reasonIndex] }">{{ reasons[reasonIndex] || '请选择' }}</view>
        </picker>
        <text class="chevron">›</text>
      </view>
    </view>

    <view class="desc-card dm-card">
      <text class="section-title">问题描述</text>
      <textarea
        v-model="form.description"
        maxlength="200"
        placeholder="请详细描述遇到的问题，便于客服更快为您处理"
      />
      <text class="counter">{{ form.description.length }}/200</text>

      <text class="section-title upload-title">上传凭证</text>
      <view class="uploads">
        <view v-for="(image, index) in form.images" :key="image" class="upload-img">
          <image :src="image" mode="aspectFill" />
          <text class="remove" @click="removeImage(index)">×</text>
        </view>
        <view v-if="form.images.length < 5" class="add-img" @click="chooseImage">
          <text class="plus">+</text>
          <text>添加 ({{ form.images.length }}/5)</text>
        </view>
      </view>
    </view>

    <view class="phase-tip">
      <text>一期采用线上申请、后台审核、线下处理；暂不接入退换货寄件流程。</text>
    </view>

    <view class="submit-bar">
      <button class="submit" @click="submit">提交申请</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { aftersaleApi } from '@/services/aftersale'
import { orderApi } from '@/services/order'
import { money } from '@/utils/format'
import { requirePageLogin } from '@/utils/auth'

const types = ['仅退款（未收到货）', '退货退款（需后台审核）']
const reasons = ['物流长时间未更新', '商品破损 / 包装异常', '商品规格 / 数量有误', '不想要了', '其他原因']
const typeIndex = ref(0)
const reasonIndex = ref(0)
const order = ref<any>({})
const form = ref<any>({ orderId: '', type: 'refund', reason: '', description: '', images: [] })

const firstItem = computed(() => {
  const items = order.value.items || order.value.orderItems || []
  return items[0] || { productName: order.value.productName || '订单商品', productImage: order.value.productImage, price: order.value.payAmount || order.value.totalAmount, quantity: order.value.quantity || 1 }
})
const refundAmount = computed(() => order.value.refundAmount || firstItem.value.payAmount || firstItem.value.price || order.value.payAmount || order.value.totalAmount || 0)

async function loadOrder() {
  if (!form.value.orderId) return
  try {
    order.value = (await orderApi.detail(form.value.orderId)).data || {}
  } catch {
    order.value = {}
  }
}

function chooseImage() {
  uni.chooseImage({
    count: 5 - form.value.images.length,
    success: (res) => {
      form.value.images = form.value.images.concat(res.tempFilePaths || []).slice(0, 5)
    }
  })
}

function removeImage(index: number) {
  form.value.images.splice(index, 1)
}

async function submit() {
  if (!requirePageLogin()) return
  if (!form.value.orderId) {
    uni.showToast({ title: '缺少订单信息', icon: 'none' })
    return
  }
  if (!form.value.description.trim()) {
    uni.showToast({ title: '请填写售后原因', icon: 'none' })
    return
  }
  form.value.type = typeIndex.value === 0 ? 'refund' : 'return_refund'
  form.value.reason = reasons[reasonIndex.value]
  form.value.refundAmount = refundAmount.value
  const res = await aftersaleApi.apply(form.value)
  uni.showToast({ title: '已提交', icon: 'success' })
  const id = res.data?.id || res.data?.aftersaleId
  setTimeout(() => {
    uni.redirectTo({ url: id ? '/pages/aftersale/detail/index?id=' + id : '/pages/aftersale/detail/index?orderId=' + form.value.orderId })
  }, 400)
}

onLoad((options: any) => {
  if (!requirePageLogin()) return
  form.value.orderId = options.orderId || ''
  loadOrder()
})
</script>

<style scoped lang="scss">
.aftersale-page {
  padding: 24rpx 0 calc(132rpx + env(safe-area-inset-bottom));
}

.page-title {
  margin: 0 32rpx 20rpx;
  color: var(--dm-text-1);
  font-size: 36rpx;
  font-weight: 900;
}

.goods-card,
.form-card,
.desc-card {
  margin: 0 32rpx 20rpx;
}

.goods-card {
  display: flex;
  gap: 20rpx;
  padding: 24rpx;
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

.goods-name,
.goods-spec,
.price,
.label,
.field,
.section-title,
.counter,
.phase-tip text {
  display: block;
}

.goods-name {
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 700;
  line-height: 1.45;
}

.goods-spec {
  margin-top: 6rpx;
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.price {
  margin-top: 8rpx;
  color: var(--dm-gold-500);
  font-size: 30rpx;
  font-weight: 800;
}

.form-card {
  overflow: hidden;
}

.form-row {
  min-height: 96rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 0 28rpx;
  border-bottom: 2rpx solid var(--dm-line-soft);
}

.form-row.no-border {
  border-bottom: 0;
}

.label {
  width: 148rpx;
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 600;
}

.form-row picker {
  flex: 1;
}

.field {
  color: var(--dm-text-1);
  font-size: 28rpx;
  text-align: right;
}

.field.placeholder {
  color: var(--dm-text-3);
}

.chevron {
  color: #C9BFA9;
  font-size: 34rpx;
}

.desc-card {
  padding: 28rpx;
}

.section-title {
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 800;
}

textarea {
  width: 100%;
  height: 180rpx;
  margin-top: 16rpx;
  padding: 20rpx;
  border-radius: 16rpx;
  background: var(--dm-bg-app);
  color: var(--dm-text-1);
  font-size: 26rpx;
  box-sizing: border-box;
}

.counter {
  margin-top: 8rpx;
  color: var(--dm-text-3);
  font-size: 22rpx;
  text-align: right;
}

.upload-title {
  margin-top: 20rpx;
}

.uploads {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-top: 16rpx;
}

.upload-img,
.add-img {
  position: relative;
  width: 140rpx;
  height: 140rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.upload-img image {
  width: 100%;
  height: 100%;
}

.remove {
  position: absolute;
  top: 4rpx;
  right: 4rpx;
  width: 34rpx;
  height: 34rpx;
  line-height: 30rpx;
  border-radius: 50%;
  text-align: center;
  color: #FFFFFF;
  background: rgba(26, 16, 6, 0.82);
  font-size: 28rpx;
}

.add-img {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
  color: var(--dm-text-3);
  border: 3rpx dashed var(--dm-line);
  font-size: 22rpx;
}

.plus {
  font-size: 40rpx;
  line-height: 1;
}

.phase-tip {
  margin: 0 32rpx;
  padding: 20rpx 24rpx;
  color: #7A5610;
  background: var(--dm-gold-50);
  border-radius: 16rpx;
  font-size: 24rpx;
  line-height: 1.55;
}

.submit-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 32rpx calc(20rpx + env(safe-area-inset-bottom));
  background: #FFFFFF;
  box-shadow: var(--dm-shadow-action);
}

.submit {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 999rpx;
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-size: 30rpx;
  font-weight: 800;
  box-shadow: 0 10rpx 28rpx rgba(211, 138, 0, 0.25);
}

button::after {
  border: 0;
}
</style>
