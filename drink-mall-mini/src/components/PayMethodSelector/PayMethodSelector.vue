<template>
  <view class="pay-box">
    <view class="pay-head">
      <text class="pay-title">{{ title }}</text>
      <text class="pay-note">单一支付</text>
    </view>
    <view v-if="!methods.length" class="empty">暂无可用支付方式，请稍后重试</view>
    <view
      v-for="item in methods"
      :key="item.value"
      class="pay-row"
      :class="{ active: modelValue === item.value, disabled: item.disabled }"
      @click="select(item)"
    >
      <view class="method-icon" :class="item.value">
        <uni-icons :type="iconOf(item.value)" size="22" :color="colorOf(item.value)" />
      </view>
      <view class="pay-copy">
        <text class="name">{{ item.label }}</text>
        <text class="tip">{{ item.disabled ? (item.insufficientText || '当前支付方式不可用') : (item.tip || '不支持组合支付') }}</text>
      </view>
      <view class="radio" :class="{ checked: modelValue === item.value }">
        <uni-icons v-if="modelValue === item.value" type="checkmarkempty" size="16" color="#fff" />
      </view>
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

function iconOf(value: string) {
  if (value === 'wechat') return 'weixin'
  if (value === 'points') return 'gift'
  if (value === 'df' || value === 'wineBean') return 'wallet'
  return 'wallet'
}

function colorOf(value: string) {
  if (value === 'wechat') return '#12B95B'
  if (value === 'points') return '#D1467A'
  if (value === 'df') return '#3A6FD1'
  return '#D38A00'
}
</script>

<style scoped lang="scss">
.pay-box {
  background: #fff;
  margin: 20rpx;
  padding: 28rpx;
  border-radius: 24rpx;
  box-shadow: var(--dm-shadow-card);
}

.pay-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18rpx;
}

.pay-title {
  font-size: 30rpx;
  font-weight: 800;
  color: var(--dm-text-1);
}

.pay-note {
  font-size: 22rpx;
  color: var(--dm-gold-600);
  background: var(--dm-gold-50);
  border-radius: 999rpx;
  padding: 6rpx 16rpx;
}

.empty {
  padding: 24rpx;
  color: var(--dm-text-3);
  background: var(--dm-cream-100);
  border-radius: 14rpx;
  font-size: 24rpx;
}

.pay-row {
  min-height: 108rpx;
  border-bottom: 1rpx solid var(--dm-line-soft);
  padding: 12rpx 0;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.pay-row:last-child {
  border-bottom: 0;
}

.pay-row.disabled {
  opacity: .48;
}

.method-icon {
  width: 76rpx;
  height: 76rpx;
  border-radius: 50%;
  background: var(--dm-gold-50);
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 76rpx;
}

.method-icon.wechat {
  background: rgba(18, 185, 91, .1);
}

.method-icon.points {
  background: rgba(209, 70, 122, .1);
}

.method-icon.df {
  background: rgba(58, 111, 209, .1);
}

.pay-copy {
  min-width: 0;
  flex: 1;
}

.name {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: var(--dm-text-1);
}

.tip {
  display: block;
  margin-top: 8rpx;
  font-size: 23rpx;
  color: var(--dm-text-3);
  line-height: 1.35;
}

.radio {
  width: 42rpx;
  height: 42rpx;
  border-radius: 50%;
  border: 3rpx solid var(--dm-line);
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 42rpx;
}

.radio.checked {
  border-color: transparent;
  background: var(--dm-grad-gold);
}
</style>
