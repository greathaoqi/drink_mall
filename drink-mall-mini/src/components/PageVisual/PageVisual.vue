<template>
  <view class="page-visual">
    <image class="page-visual__image" :src="image || '/static/images/page-form.png'" mode="aspectFill" />
  </view>
</template>

<script setup lang="ts">
defineProps<{ image?: string }>()
</script>

<style scoped lang="scss">
.page-visual {
  margin: 20rpx;
  height: 156rpx;
  border-radius: 18rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 24rpx rgba(58, 32, 12, .06);
}

.page-visual__image {
  display: block;
  width: 100%;
  height: 100%;
}
</style>
