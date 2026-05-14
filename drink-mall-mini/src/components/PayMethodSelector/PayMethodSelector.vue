<template>
  <view class="pay-box">
    <view class="pay-head">
      <text class="pay-title">{{ title }}</text>
      <text class="pay-note">单选支付</text>
    </view>
    <view v-if="!methods.length" class="empty">暂无可用支付方式，请稍后重试</view>
    <view
      v-for="item in methods"
      :key="item.value"
      class="pay-row"
      :class="{ active: modelValue === item.value, disabled: item.disabled }"
      @click="select(item)"
    >
      <view class="pay-copy">
        <text class="name">{{ item.label }}</text>
        <text class="tip">{{ item.tip || '单一支付，不支持组合支付' }}</text>
      </view>
      <uni-icons :type="modelValue === item.value ? 'checkbox-filled' : 'circle'" size="22" :color="modelValue === item.value ? '#b97700' : '#bbb'" />
    </view>
  </view>
</template>

<script setup lang="ts">
export interface PayMethod {
  value: string
  label: string
  tip?: string
  disabled?: boolean
  insufficientText?: string
}

defineProps<{ modelValue: string; methods: PayMethod[]; title?: string }>()
const emit = defineEmits<{ (e: 'update:modelValue', value: string): void }>()

function select(item: PayMethod) {
  if (item.disabled) {
    uni.showToast({ title: item.insufficientText || '当前支付方式不可用', icon: 'none' })
    return
  }
  emit('update:modelValue', item.value)
}
</script>

<style scoped>
.pay-box{background:#fff;margin:20rpx;padding:28rpx;border-radius:16rpx;box-shadow:0 8rpx 24rpx rgba(58,32,12,.06)}
.pay-head{display:flex;align-items:center;justify-content:space-between;margin-bottom:18rpx}
.pay-title{font-size:30rpx;font-weight:800;color:#24170c}
.pay-note{font-size:22rpx;color:#b97700;background:#fff4df;border-radius:999rpx;padding:6rpx 16rpx}
.empty{padding:24rpx;color:#8d8175;background:#f8f4ed;border-radius:12rpx;font-size:24rpx}
.pay-row{min-height:96rpx;border:2rpx solid #eee7db;border-radius:14rpx;margin-top:16rpx;padding:0 22rpx;display:flex;align-items:center;justify-content:space-between;gap:18rpx}
.pay-row.active{border-color:#b97700;background:#fff8e8}
.pay-row.disabled{opacity:.48}
.pay-copy{min-width:0;flex:1}
.name{display:block;font-size:28rpx;font-weight:700;color:#24170c}
.tip{display:block;margin-top:8rpx;font-size:23rpx;color:#8d8175;line-height:1.35}
</style>
