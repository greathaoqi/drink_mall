<template>
  <u-popup :show="show" mode="center" :closeOnClickOverlay="false" :round="16">
    <view class="age-verify-modal">
      <view class="modal-title">年龄确认</view>
      <view class="modal-content">
        <view class="warning-icon">18+</view>
        <text class="warning-text">
          根据国家相关法律法规，未成年人禁止购买酒类商品。
          请确认您已年满18周岁。
        </text>
      </view>
      <view class="checkbox-row">
        <u-checkbox v-model="confirmed" shape="circle" activeColor="#07C160">
          我已年满18周岁
        </u-checkbox>
      </view>
      <view class="modal-actions">
        <u-button type="default" plain shape="circle" @click="handleCancel" customStyle="width: 45%">
          取消
        </u-button>
        <u-button type="primary" shape="circle" :disabled="!confirmed" @click="handleConfirm" color="#07C160" customStyle="width: 45%">
          确认已满18岁
        </u-button>
      </view>
    </view>
  </u-popup>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { verifyAge } from '@/services/user'

const props = defineProps<{
  show: boolean
}>()

const emit = defineEmits<{
  (e: 'confirm'): void
  (e: 'cancel'): void
}>()

const userStore = useUserStore()
const confirmed = ref(false)

async function handleConfirm() {
  if (!confirmed.value) return

  try {
    const success = await verifyAge()
    if (success) {
      userStore.setAgeVerified(true)
      emit('confirm')
    } else {
      uni.showToast({ title: '验证失败，请重试', icon: 'none' })
    }
  } catch (error) {
    uni.showToast({ title: '验证失败', icon: 'none' })
  }
}

function handleCancel() {
  confirmed.value = false
  emit('cancel')
}
</script>

<style scoped lang="scss">
.age-verify-modal {
  width: 560rpx;
  padding: 48rpx;
  background: #FFFFFF;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  text-align: center;
  margin-bottom: 32rpx;
}

.modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32rpx;
}

.warning-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b1f0d, #c06f2b);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  font-weight: 800;
  margin-bottom: 24rpx;
}

.warning-text {
  font-size: 28rpx;
  color: #333333;
  line-height: 1.5;
  text-align: center;
}

.checkbox-row {
  margin-bottom: 32rpx;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
}
</style>
