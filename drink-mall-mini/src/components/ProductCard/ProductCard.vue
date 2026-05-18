<template>
  <view class="product-card mall-card mall-press" @click="$emit('tap', product)">
    <view class="cover-wrap">
      <image class="cover" :src="image" mode="aspectFill" />
      <text v-if="zoneLabel" class="zone-tag">{{ zoneLabel }}</text>
      <view class="cart-dot">
        <uni-icons type="plusempty" size="17" color="#3A1A00" />
      </view>
    </view>
    <view class="body">
      <text class="name">{{ product.name || product.productName || '商品' }}</text>
      <view class="tags">
        <text v-if="product.levelDiscountText">{{ product.levelDiscountText }}</text>
        <text v-if="product.payMethodText">{{ product.payMethodText }}</text>
      </view>
      <view class="foot">
        <text class="price">￥{{ price }}</text>
        <text class="sold">{{ soldText }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { money } from '@/utils/format'

const props = defineProps<{ product: any }>()
defineEmits<{ (e: 'tap', product: any): void }>()

const image = computed(() => props.product.mainImage || props.product.productImage || '/static/images/product-wine.png')
const price = computed(() => {
  if (props.product.zoneType === 'gift' && props.product.pointsPrice) return String(props.product.pointsPrice)
  return money(props.product.discountPrice ?? props.product.price)
})
const soldText = computed(() => props.product.sales ? `已售 ${props.product.sales}` : '立即选购')
const zoneLabel = computed(() => {
  const map: Record<string, string> = {
    main: '主产品',
    investment: '招商',
    retail: '零售',
    gift: '礼包'
  }
  return map[props.product.zoneType] || props.product.zoneName || ''
})
</script>

<style scoped lang="scss">
.product-card {
  overflow: hidden;
  min-width: 0;
  border-radius: var(--dm-radius-md);
}

.cover-wrap {
  position: relative;
  aspect-ratio: 1 / 1;
  background: var(--dm-cream-100);
  overflow: hidden;
}

.cover {
  display: block;
  width: 100%;
  height: 100%;
}

.cover-wrap::after {
  content: '';
  position: absolute;
  inset: 0;
  border: 1rpx solid rgba(40, 20, 0, .06);
  pointer-events: none;
}

.zone-tag {
  position: absolute;
  left: 16rpx;
  top: 16rpx;
  height: 38rpx;
  line-height: 38rpx;
  padding: 0 14rpx;
  border-radius: var(--dm-radius-xs);
  background: var(--dm-grad-gold);
  color: #3A1A00;
  font-size: 22rpx;
  font-weight: 700;
}

.cart-dot {
  position: absolute;
  right: 16rpx;
  bottom: 16rpx;
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: var(--dm-grad-gold);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 20rpx rgba(211, 138, 0, .35);
}

.body {
  padding: 16rpx 20rpx 22rpx;
}

.name {
  display: -webkit-box;
  min-height: 72rpx;
  line-height: 36rpx;
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  min-height: 34rpx;
  margin-top: 10rpx;
}

.tags text {
  max-width: 100%;
  height: 32rpx;
  line-height: 32rpx;
  padding: 0 10rpx;
  border-radius: var(--dm-radius-xs);
  background: var(--dm-gold-50);
  color: var(--dm-gold-600);
  font-size: 20rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.foot {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 8rpx;
  margin-top: 12rpx;
}

.price {
  color: var(--dm-gold-500);
  font-size: 34rpx;
  font-weight: 900;
  font-variant-numeric: tabular-nums;
}

.sold {
  flex: 0 0 auto;
  color: var(--dm-text-3);
  font-size: 22rpx;
}
</style>
