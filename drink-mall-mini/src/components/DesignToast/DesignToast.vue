<template>
  <view></view>
</template>

<script setup lang="ts">
export interface DesignToastOptions {
  title: string
  icon?: 'success' | 'error' | 'loading' | 'none'
  duration?: number
  mask?: boolean
}

function show(options: DesignToastOptions | string) {
  const config = typeof options === 'string' ? { title: options } : options
  uni.showToast({
    title: config.title,
    icon: config.icon || 'none',
    duration: config.duration || 1800,
    mask: config.mask ?? false
  })
}

function success(title: string, duration = 1800) {
  show({ title, icon: 'success', duration })
}

function error(title: string, duration = 1800) {
  show({ title, icon: 'none', duration })
}

function loading(title = '加载中...') {
  uni.showLoading({ title, mask: true })
}

function hideLoading() {
  uni.hideLoading()
}

defineExpose({
  show,
  success,
  error,
  loading,
  hideLoading
})
</script>
