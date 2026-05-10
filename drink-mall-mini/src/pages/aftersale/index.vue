<template>
  <view class="aftersale-page">
    <view class="form-card">
      <view class="form-item">
        <text class="label">售后类型</text>
        <picker :value="typeIndex" :range="types" range-key="label" @change="typeIndex = $event.detail.value">
          <view class="picker">{{ types[typeIndex].label }}</view>
        </picker>
      </view>
      <view class="form-item">
        <text class="label">问题描述</text>
        <textarea v-model="form.reason" placeholder="请描述您的问题" />
      </view>
      <view class="form-item">
        <text class="label">详细说明</text>
        <textarea v-model="form.description" placeholder="请详细说明情况（选填）" />
      </view>
    </view>

    <button class="submit-btn" @click="handleSubmit">提交申请</button>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request'

const types = [{ label: '退款', value: 'refund' }, { label: '退货', value: 'return' }]
const typeIndex = ref(0)
const form = ref({ orderId: 0, type: 'refund', reason: '', description: '' })

const handleSubmit = async () => {
  if (!form.value.reason) {
    uni.showToast({ title: '请填写问题描述', icon: 'none' })
    return
  }
  form.value.type = types[typeIndex.value].value
  await request.post('/aftersale', form.value)
  uni.showToast({ title: '提交成功', icon: 'success' })
  setTimeout(() => uni.navigateBack(), 1000)
}

onLoad((options: any) => {
  form.value.orderId = Number(options.orderId)
})
</script>

<style scoped>
.aftersale-page { background: #f5f5f5; min-height: 100vh; padding: 20rpx; }
.form-card { background: #fff; border-radius: 12rpx; padding: 0 30rpx; }
.form-item { padding: 30rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.label { font-size: 28rpx; color: #333; display: block; margin-bottom: 20rpx; }
textarea { width: 100%; height: 150rpx; font-size: 28rpx; }
.picker { font-size: 28rpx; color: #666; padding: 20rpx 0; }
.submit-btn { margin: 40rpx; background: #e93b3d; color: #fff; border-radius: 40rpx; font-size: 30rpx; }
</style>