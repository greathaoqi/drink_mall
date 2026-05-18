<template>
  <view class="dm-page cart-page">
    <view class="cart-head">
      <view>
        <text class="title">购物车</text>
        <text class="count">共 {{ items.length }} 件商品</text>
      </view>
      <text class="manage">管理</text>
    </view>

    <PageState :loading="loading" :empty="items.length === 0" empty-text="购物车还是空的">
      <view class="section-line">
        <view class="section-dot"><uni-icons type="checkmarkempty" size="12" color="#fff" /></view>
        <text>已选商品</text>
        <text class="section-tip">请分开结算礼包与普通商品</text>
      </view>

      <view class="cart-list">
        <view class="cart-item" v-for="item in items" :key="item.cartId">
          <checkbox class="check" :checked="item.selected" @click="toggle(item)" />
          <image class="pic" :src="item.productImage || item.mainImage || '/static/images/product-wine.png'" mode="aspectFill" />
          <view class="info">
            <view class="name-row">
              <text class="name">{{ item.productName || item.name }}</text>
              <text class="delete" @click="remove(item)">删除</text>
            </view>
            <view class="chips">
              <text>{{ item.skuName || item.zoneName || '默认规格' }}</text>
              <text v-if="isGiftItem(item)">纯积分</text>
            </view>
            <view class="item-foot">
              <text class="price">{{ isGiftItem(item) ? giftPointsPrice(item) + ' 积分' : '￥' + money(item.price) }}</text>
              <view class="stepper">
                <button @click="changeQty(item, -1)">-</button>
                <text>{{ item.quantity }}</text>
                <button @click="changeQty(item, 1)">+</button>
              </view>
            </view>
          </view>
        </view>
      </view>
    </PageState>

    <BottomActionBar>
      <template #left>
        <view class="all-check" @click="toggleAll">
          <checkbox :checked="allSelected" />
          <text>全选</text>
        </view>
      </template>
      <view class="sum">
        <text>合计</text>
        <text class="sum-price">￥{{ total }}</text>
        <text v-if="selectedGiftCount" class="sum-tip">含 {{ selectedGiftCount }} 件积分礼包</text>
      </view>
      <button class="checkout" @click="checkout">结算 ({{ selectedItems.length }})</button>
    </BottomActionBar>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import BottomActionBar from '@/components/BottomActionBar/BottomActionBar.vue'
import PageState from '@/components/PageState/PageState.vue'
import { cartApi } from '@/services/cart'
import { listOf, money } from '@/utils/format'
import { giftPointsPrice, isGiftItem } from '@/utils/business'
import { requirePageLogin } from '@/utils/auth'

const items = ref<any[]>([])
const loading = ref(false)
const selectedItems = computed(() => items.value.filter((item) => item.selected))
const selectedGiftCount = computed(() => selectedItems.value.filter(isGiftItem).length)
const allSelected = computed(() => items.value.length > 0 && items.value.every((item) => item.selected))
const total = computed(() => money(selectedItems.value.reduce((sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 1), 0)))

async function load() {
  if (!requirePageLogin()) return
  loading.value = true
  try {
    const res = await cartApi.list()
    items.value = listOf(res.data)
  } finally {
    loading.value = false
  }
}

async function toggle(item: any) {
  item.selected = !item.selected
  await cartApi.select(item.cartId, item.selected)
}

async function toggleAll() {
  const next = !allSelected.value
  await Promise.all(items.value.map((item) => {
    item.selected = next
    return cartApi.select(item.cartId, next)
  }))
}

async function changeQty(item: any, delta: number) {
  const next = Math.max(1, Number(item.quantity || 1) + delta)
  item.quantity = next
  await cartApi.updateQuantity(item.cartId, next)
}

function remove(item: any) {
  uni.showModal({
    title: '删除商品',
    content: '确定从购物车移除该商品吗？',
    success: async (res) => {
      if (res.confirm) {
        await cartApi.remove(item.cartId)
        load()
      }
    }
  })
}

function checkout() {
  const ids = selectedItems.value.map((item) => item.cartId)
  if (!ids.length) {
    uni.showToast({ title: '请选择商品', icon: 'none' })
    return
  }
  uni.navigateTo({ url: '/pages/checkout/index?ids=' + ids.join(',') })
}

onShow(load)
</script>

<style scoped lang="scss">
.cart-page { padding-bottom: calc(150rpx + env(safe-area-inset-bottom)); }
.cart-head { padding: 32rpx 28rpx 12rpx; display: flex; align-items: flex-end; justify-content: space-between; background: #fff; }
.title, .count { display: block; }
.title { color: var(--dm-text-1); font-size: 42rpx; font-weight: 900; }
.count, .manage { color: var(--dm-text-3); font-size: 24rpx; margin-top: 6rpx; }
.manage { color: var(--dm-text-2); }
.section-line { display: flex; align-items: center; gap: 12rpx; padding: 24rpx 28rpx 12rpx; font-size: 26rpx; font-weight: 700; }
.section-dot { width: 36rpx; height: 36rpx; border-radius: 50%; background: var(--dm-grad-gold); display: flex; align-items: center; justify-content: center; }
.section-tip { margin-left: auto; color: var(--dm-text-3); font-size: 22rpx; font-weight: 400; }
.cart-list { padding: 0 20rpx 24rpx; }
.cart-item { position: relative; display: flex; align-items: center; gap: 18rpx; padding: 22rpx; margin-bottom: 18rpx; background: #fff; border-radius: 24rpx; box-shadow: var(--dm-shadow-card); }
.check { flex: 0 0 auto; }
.pic { width: 156rpx; height: 156rpx; border-radius: 14rpx; background: var(--dm-cream-100); flex: 0 0 156rpx; }
.info { flex: 1; min-width: 0; }
.name-row { display: flex; align-items: flex-start; gap: 10rpx; }
.name { flex: 1; display: -webkit-box; color: var(--dm-text-1); font-size: 28rpx; font-weight: 800; line-height: 38rpx; overflow: hidden; text-overflow: ellipsis; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.delete { color: var(--dm-text-3); font-size: 22rpx; }
.chips { display: flex; flex-wrap: wrap; gap: 8rpx; margin-top: 12rpx; }
.chips text { height: 36rpx; line-height: 36rpx; padding: 0 12rpx; border-radius: 6rpx; background: var(--dm-cream-100); color: var(--dm-text-3); font-size: 22rpx; }
.item-foot { display: flex; align-items: center; justify-content: space-between; gap: 12rpx; margin-top: 18rpx; }
.price { color: var(--dm-gold-500); font-size: 32rpx; font-weight: 900; }
.stepper { display: flex; align-items: center; border-radius: 10rpx; border: 1rpx solid var(--dm-line); overflow: hidden; }
.stepper button { width: 54rpx; height: 48rpx; line-height: 48rpx; background: #fff; color: var(--dm-text-1); }
.stepper text { min-width: 58rpx; text-align: center; font-weight: 800; }
.all-check { display: flex; align-items: center; gap: 8rpx; color: var(--dm-text-2); font-size: 25rpx; }
.sum { display: flex; flex-direction: column; align-items: flex-end; color: var(--dm-text-3); font-size: 22rpx; }
.sum-price { color: var(--dm-gold-500); font-size: 34rpx; font-weight: 900; }
.sum-tip { color: var(--dm-gold-500); }
.checkout { width: 210rpx; background: var(--dm-grad-gold); color: #3A1A00; }
button:after { border: 0; }
</style>
