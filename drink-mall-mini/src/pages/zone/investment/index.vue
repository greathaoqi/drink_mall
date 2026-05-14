<template>
  <view class="page">
    <view class="hero">
      <text class="eyebrow">实名认证后购买</text>
      <text class="title">招商专区</text>
      <text class="muted">商品为联营商升级套餐。购买前必须完成实名认证，并确认合作声明；金额与规则以后端配置为准。</text>
    </view>

    <view class="card warn" v-if="!approved">
      <text class="section-title">实名认证未完成</text>
      <text class="muted">完成审核后可继续购买招商套餐。</text>
      <button class="primary" @click="goReal">去实名认证</button>
    </view>

    <view class="card">
      <label class="agree"><checkbox :checked="confirmed" @click="confirmed = !confirmed" />我已阅读并确认合作声明</label>
    </view>

    <view class="card" v-for="p in products" :key="p.id">
      <text class="section-title">{{ p.name }}</text>
      <text class="muted">{{ p.upgradeText || '升级说明以后端配置为准' }}</text>
      <text class="price">¥{{ p.price }}</text>
      <button class="primary" @click="buy(p)">购买套餐</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { productApi } from '@/services/product'
import { useUserStore } from '@/store/user'
import { listOf } from '@/utils/format'

const store = useUserStore()
const products = ref<any[]>([])
const confirmed = ref(false)
const approved = computed(() => store.realNameApproved)

async function load() { products.value = listOf((await productApi.list({ zoneType: 'investment' })).data) }
function goReal() { uni.navigateTo({ url: '/pages/auth/realname/index' }) }
function buy(p: any) {
  if (!approved.value) { goReal(); return }
  if (!confirmed.value) {
    uni.showToast({ title: '请先确认合作声明', icon: 'none' })
    return
  }
  uni.navigateTo({ url: '/pages/product/detail/index?id=' + p.id + '&confirmCooperation=1' })
}
onShow(load)
</script>

<style scoped lang="scss">
.page{min-height:100vh;background:#f6f1e8;color:#24170c;padding:20rpx}
.hero{background:#2b1207;color:#fff;border-radius:18rpx;padding:32rpx 28rpx;margin-bottom:20rpx}
.eyebrow{display:block;color:#f0bf55;font-size:23rpx}
.title{display:block;font-size:42rpx;font-weight:900;margin-top:10rpx}
.muted{display:block;color:#8d8175;font-size:24rpx;line-height:1.5;margin-top:12rpx}
.hero .muted{color:#e8d6b8}
.card{background:#fff;margin-bottom:20rpx;padding:28rpx;border-radius:16rpx;box-shadow:0 8rpx 24rpx rgba(58,32,12,.06)}
.warn{border-left:8rpx solid #b97700}
.section-title{display:block;font-size:32rpx;font-weight:900;line-height:1.35}
.price{display:block;color:#b97700;font-weight:900;margin-top:16rpx;font-size:36rpx}
.agree{display:flex;align-items:center;color:#6f6254;font-size:26rpx;line-height:1.45}
.card button{margin-top:24rpx;height:78rpx;line-height:78rpx;width:100%;border:0;border-radius:999rpx}
.primary{background:#b97700;color:#fff}
button:after{border:0}
</style>
