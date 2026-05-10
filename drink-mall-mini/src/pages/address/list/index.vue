<template>
  <view class="address-list">
    <view v-for="addr in addresses" :key="addr.id" class="address-card" @click="selectAddress(addr)">
      <view class="info">
        <view class="receiver">
          <text class="name">{{ addr.name }}</text>
          <text class="phone">{{ addr.phone }}</text>
          <text v-if="addr.isDefault" class="default-tag">默认</text>
        </view>
        <view class="address">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</view>
      </view>
      <view class="actions">
        <view class="action-btn" @click.stop="setDefault(addr)" v-if="!addr.isDefault">
          <uni-icons type="checkbox" size="20" />设为默认
        </view>
        <view class="action-btn" @click.stop="editAddress(addr.id)">
          <uni-icons type="compose" size="20" />编辑
        </view>
        <view class="action-btn" @click.stop="deleteAddress(addr.id)">
          <uni-icons type="trash" size="20" />删除
        </view>
      </view>
    </view>

    <view class="empty" v-if="addresses.length === 0">
      <uni-icons type="location" size="60" color="#ccc" />
      <text>暂无收货地址</text>
    </view>

    <view class="bottom-bar">
      <button class="add-btn" @click="addAddress">新增地址</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import request from '@/utils/request'

const addresses = ref<any[]>([])
const isSelect = ref(false)

const loadAddresses = async () => {
  const res = await request.get('/address')
  addresses.value = res.data || []
}

const selectAddress = (addr: any) => {
  if (isSelect.value) {
    const pages = getCurrentPages()
    const prevPage = pages[pages.length - 2]
    if (prevPage) {
      uni.$emit('addressSelected', addr)
    }
    uni.navigateBack()
  }
}

const setDefault = async (addr: any) => {
    await request.put(`/address/${addr.id}/default`)
  loadAddresses()
}

const editAddress = (id: number) => {
  uni.navigateTo({ url: `/pages/address/edit/index?id=${id}` })
}

const deleteAddress = async (id: number) => {
  uni.showModal({ title: '提示', content: '确定删除此地址?', success: async (res) => {
    if (res.confirm) {
      await request.delete(`/address/${id}`)
      loadAddresses()
    }
  }})
}

const addAddress = () => {
  uni.navigateTo({ url: '/pages/address/edit/index' })
}

onLoad((options: any) => {
  isSelect.value = options.select === '1'
})

onShow(() => loadAddresses())
</script>

<style scoped>
.address-list { background: #f5f5f5; min-height: 100vh; padding-bottom: 120rpx; }
.address-card { background: #fff; margin: 20rpx; padding: 30rpx; border-radius: 12rpx; }
.info { border-bottom: 1rpx solid #f5f5f5; padding-bottom: 20rpx; }
.receiver { display: flex; align-items: center; gap: 20rpx; }
.name { font-size: 32rpx; font-weight: bold; }
.phone { font-size: 28rpx; color: #666; }
.default-tag { font-size: 20rpx; background: #e93b3d; color: #fff; padding: 4rpx 12rpx; border-radius: 4rpx; }
.address { font-size: 26rpx; color: #666; margin-top: 10rpx; }
.actions { display: flex; gap: 30rpx; margin-top: 20rpx; }
.action-btn { display: flex; align-items: center; gap: 8rpx; font-size: 24rpx; color: #666; }
.empty { display: flex; flex-direction: column; align-items: center; padding: 100rpx; color: #999; }
.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; padding: 20rpx; }
.add-btn { background: #e93b3d; color: #fff; border-radius: 40rpx; font-size: 30rpx; }
</style>
