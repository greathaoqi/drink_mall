<template>
  <view class="order-detail">
    <view class="status-card">
      <view class="status-text">{{ statusText(order.status) }}</view>
      <view class="status-desc">{{ statusDesc(order.status) }}</view>
    </view>

    <view class="address-card">
      <view class="receiver">{{ order.address?.receiverName }} {{ order.address?.receiverPhone }}</view>
      <view class="address">{{ order.address?.province }}{{ order.address?.city }}{{ order.address?.district }}{{ order.address?.detail }}</view>
    </view>

    <view class="goods-card">
      <view v-for="item in order.items" :key="item.itemId" class="goods-item">
        <image :src="item.productImage" class="goods-img" />
        <view class="goods-info">
          <text class="goods-name">{{ item.productName }}</text>
          <view class="goods-bottom">
            <text class="goods-price">¥{{ item.price }}</text>
            <text class="goods-quantity">x{{ item.quantity }}</text>
          </view>
        </view>
      </view>
      <view class="goods-total">
        <text>商品金额</text>
        <text>¥{{ order.totalAmount }}</text>
      </view>
    </view>

    <view class="info-card">
      <view class="info-item">
        <text class="label">订单编号</text>
        <text class="value">{{ order.orderNo }}</text>
      </view>
      <view class="info-item">
        <text class="label">创建时间</text>
        <text class="value">{{ order.createdAt }}</text>
      </view>
      <view class="info-item" v-if="order.paymentTime">
        <text class="label">支付时间</text>
        <text class="value">{{ order.paymentTime }}</text>
      </view>
    </view>

    <view class="bottom-bar">
      <button v-if="order.status === 'pending'" class="btn cancel" @click="handleCancel">取消订单</button>
      <button v-if="order.status === 'pending'" class="btn pay" @click="handlePay">立即支付</button>
      <button v-if="order.status === 'shipped'" class="btn confirm" @click="handleConfirm">确认收货</button>
      <button v-if="['paid', 'shipped'].includes(order.status)" class="btn aftersale" @click="goAftersale">申请售后</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request'

const order = ref<any>({})
const orderId = ref(0)

const statusText = (status: string) => {
  const map: Record<string, string> = { pending: '待付款', paid: '待发货', shipped: '待收货', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}

const statusDesc = (status: string) => {
  const map: Record<string, string> = { pending: '请在30分钟内完成支付', paid: '商家正在准备发货', shipped: '商品正在配送中', completed: '感谢您的支持', cancelled: '订单已取消' }
  return map[status] || ''
}

const loadOrder = async () => {
  const res = await request.get(`/api/v1/order/${orderId.value}`)
  order.value = res.data || {}
}

const handleCancel = async () => {
  await request.post(`/api/v1/order/${orderId.value}/cancel`)
  uni.showToast({ title: '已取消', icon: 'success' })
  loadOrder()
}

const handlePay = async () => {
  await request.post(`/api/v1/order/${orderId.value}/pay`)
  uni.showToast({ title: '支付成功', icon: 'success' })
  loadOrder()
}

const handleConfirm = async () => {
  await request.post(`/api/v1/order/${orderId.value}/confirm`)
  uni.showToast({ title: '已确认收货', icon: 'success' })
  loadOrder()
}

const goAftersale = () => {
  uni.navigateTo({ url: `/pages/aftersale/index?orderId=${orderId.value}` })
}

onLoad((options: any) => {
  orderId.value = options.id
  loadOrder()
})
</script>

<style scoped>
.order-detail { background: #f5f5f5; min-height: 100vh; padding-bottom: 120rpx; }
.status-card { background: linear-gradient(135deg, #e93b3d, #ff6b6b); color: #fff; padding: 40rpx; }
.status-text { font-size: 36rpx; font-weight: bold; }
.status-desc { font-size: 28rpx; margin-top: 10rpx; opacity: 0.9; }
.address-card { background: #fff; margin: 20rpx; padding: 30rpx; border-radius: 12rpx; }
.receiver { font-size: 30rpx; font-weight: bold; }
.address { font-size: 26rpx; color: #666; margin-top: 10rpx; }
.goods-card { background: #fff; margin: 20rpx; padding: 20rpx; border-radius: 12rpx; }
.goods-item { display: flex; padding: 20rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.goods-img { width: 160rpx; height: 160rpx; border-radius: 8rpx; }
.goods-info { flex: 1; margin-left: 20rpx; }
.goods-name { font-size: 28rpx; }
.goods-bottom { display: flex; justify-content: space-between; margin-top: 60rpx; }
.goods-price { color: #e93b3d; }
.goods-quantity { color: #999; }
.goods-total { display: flex; justify-content: space-between; padding: 20rpx 0; font-size: 28rpx; }
.info-card { background: #fff; margin: 20rpx; padding: 20rpx; border-radius: 12rpx; }
.info-item { display: flex; justify-content: space-between; padding: 15rpx 0; font-size: 26rpx; }
.label { color: #999; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; padding: 20rpx; display: flex; gap: 20rpx; justify-content: flex-end; }
.btn { font-size: 28rpx; padding: 16rpx 40rpx; border-radius: 40rpx; }
.btn.cancel { background: #fff; border: 1rpx solid #ddd; color: #666; }
.btn.pay { background: #e93b3d; color: #fff; border: none; }
.btn.confirm { background: #52c41a; color: #fff; border: none; }
.btn.aftersale { background: #fff; border: 1rpx solid #e93b3d; color: #e93b3d; }
</style>