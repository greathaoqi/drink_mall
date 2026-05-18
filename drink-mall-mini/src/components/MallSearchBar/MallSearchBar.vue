<template>
  <view class="mall-search" :class="{ compact, light, dark: !light }">
    <view class="search-box mall-press" @click="focusInput">
      <uni-icons type="search" size="16" :color="light ? '#9B8E7C' : 'rgba(255,255,255,.72)'" />
      <input
        v-if="editable"
        v-model="innerValue"
        class="search-input"
        :placeholder="placeholder"
        confirm-type="search"
        placeholder-class="search-placeholder"
        @confirm="submit"
        @input="emitInput"
      />
      <text v-else class="placeholder">{{ placeholder }}</text>
    </view>
    <button v-if="editable && showButton" class="search-button mall-press" @click="submit">搜索</button>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = withDefaults(defineProps<{
  modelValue?: string
  placeholder?: string
  editable?: boolean
  compact?: boolean
  light?: boolean
  showButton?: boolean
}>(), {
  modelValue: '',
  placeholder: '搜索酒水商品...',
  editable: false,
  compact: false,
  light: false,
  showButton: true
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'search', value: string): void
  (e: 'tap'): void
}>()

const innerValue = ref(props.modelValue)

watch(() => props.modelValue, (value) => {
  innerValue.value = value || ''
})

function focusInput() {
  if (!props.editable) emit('tap')
}

function emitInput() {
  emit('update:modelValue', innerValue.value)
}

function submit() {
  emit('update:modelValue', innerValue.value)
  emit('search', innerValue.value.trim())
}
</script>

<style scoped lang="scss">
.mall-search {
  display: flex;
  align-items: center;
  gap: 16rpx;
  width: 100%;
}

.search-box {
  flex: 1;
  min-width: 0;
  height: 72rpx;
  padding: 0 28rpx;
  border-radius: var(--dm-radius-pill);
  display: flex;
  align-items: center;
  gap: 14rpx;
  box-sizing: border-box;
}

.dark .search-box {
  background: rgba(255, 255, 255, .1);
  border: 2rpx solid rgba(255, 255, 255, .12);
}

.light .search-box {
  background: #F4EEDF;
  border: 2rpx solid var(--dm-line);
}

.compact .search-box {
  height: 64rpx;
}

.placeholder,
.search-input {
  flex: 1;
  min-width: 0;
  color: var(--dm-text-1);
  font-size: 26rpx;
}

.dark .placeholder,
.dark .search-input {
  color: rgba(255, 255, 255, .78);
}

.placeholder {
  color: var(--dm-text-3);
}

:deep(.search-placeholder) {
  color: var(--dm-text-3);
}

.dark :deep(.search-placeholder) {
  color: rgba(255, 255, 255, .62);
}

.search-button {
  width: 112rpx;
  height: 64rpx;
  line-height: 64rpx;
  border-radius: var(--dm-radius-pill);
  background: var(--dm-grad-gold);
  color: #3A1A00;
  font-size: 26rpx;
  font-weight: 700;
}
</style>
