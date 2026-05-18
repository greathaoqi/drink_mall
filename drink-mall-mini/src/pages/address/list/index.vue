<template>
  <view class="dm-page address-page">
    <view class="address-list">
      <view class="address-card" v-for="a in addresses" :key="a.id" @click="choose(a)">
        <view class="top">
          <text class="name">{{ a.name }}</text>
          <text class="phone">{{ a.phone }}</text>
          <text v-if="a.isDefault" class="tag">默认</text>
          <uni-icons type="compose" size="18" color="#C9BFA9" @click.stop="edit(a.id)" />
        </view>
        <text class="detail">{{ a.province }}{{ a.city }}{{ a.district }}{{ a.detail }}</text>
        <view class="ops">
          <text @click.stop="def(a.id)">设为默认</text>
          <text @click.stop="remove(a.id)">删除</text>
        </view>
      </view>
      <view v-if="!addresses.length" class="empty-card">
        <text>暂无收货地址</text>
        <text>新增地址后可用于下单配送</text>
      </view>
    </view>
    <button class="add primary" @click="edit('')">新增收货地址</button>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { addressApi } from '@/services/address'
import { requirePageLogin } from '@/utils/auth'

const addresses = ref<any[]>([])
const select = ref(false)

async function load() {
  if (!requirePageLogin()) return
  addresses.value = (await addressApi.list()).data || []
}

function choose(a: any) {
  if (select.value) {
    uni.$emit('addressSelected', a)
    uni.navigateBack()
  }
}

function edit(id: any) {
  uni.navigateTo({ url: '/pages/address/edit/index' + (id ? '?id=' + id : '') })
}

async function def(id: any) {
  await addressApi.setDefault(id)
  load()
}

function remove(id: any) {
  uni.showModal({
    title: '删除地址',
    content: '确定删除该收货地址吗？',
    success: async (res) => {
      if (res.confirm) {
        await addressApi.remove(id)
        load()
      }
    }
  })
}

onLoad((o: any) => { select.value = o.select === '1' })
onShow(load)
</script>

<style scoped lang="scss">
.address-page { padding-bottom: calc(140rpx + env(safe-area-inset-bottom)); }
.address-list { padding: 20rpx; }
.address-card, .empty-card { background: #fff; border-radius: 24rpx; padding: 28rpx; margin-bottom: 20rpx; box-shadow: var(--dm-shadow-card); }
.top { display: flex; align-items: center; gap: 16rpx; }
.name { font-size: 30rpx; font-weight: 800; color: var(--dm-text-1); }
.phone { font-size: 25rpx; color: var(--dm-text-2); }
.tag { height: 34rpx; line-height: 34rpx; padding: 0 12rpx; border-radius: 6rpx; background: var(--dm-gold-50); color: var(--dm-gold-600); font-size: 21rpx; }
.top uni-icons { margin-left: auto; }
.detail { display: block; margin-top: 16rpx; color: var(--dm-text-2); font-size: 25rpx; line-height: 1.55; }
.ops { display: flex; justify-content: flex-end; gap: 30rpx; margin-top: 20rpx; color: var(--dm-gold-600); font-size: 24rpx; }
.empty-card { display: flex; flex-direction: column; align-items: center; gap: 10rpx; color: var(--dm-text-3); font-size: 25rpx; padding: 70rpx 28rpx; }
.empty-card text:first-child { color: var(--dm-text-1); font-size: 30rpx; font-weight: 800; }
.add { position: fixed; left: 24rpx; right: 24rpx; bottom: calc(28rpx + env(safe-area-inset-bottom)); height: 88rpx; line-height: 88rpx; border: 0; border-radius: 999rpx; }
.primary { background: var(--dm-grad-gold); color: #3A1A00; font-weight: 800; }
button:after { border: 0; }
</style>
