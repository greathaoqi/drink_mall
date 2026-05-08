<template>
  <view class="agreement-box">
    <u-checkbox v-model="userAgreementChecked" shape="circle" activeColor="#07C160" @change="handleUserAgreementChange">
      <text class="agreement-text">我已阅读并同意《用户协议》</text>
    </u-checkbox>
    <u-checkbox v-model="privacyPolicyChecked" shape="circle" activeColor="#07C160" class="checkbox-item" @change="handlePrivacyPolicyChange">
      <text class="agreement-text">我已阅读并同意《隐私政策》</text>
    </u-checkbox>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const userAgreementChecked = ref(false)
const privacyPolicyChecked = ref(false)

const allChecked = computed(() => userAgreementChecked.value && privacyPolicyChecked.value)

const emit = defineEmits<{
  (e: 'update:userAgreement', value: boolean): void
  (e: 'update:privacyPolicy', value: boolean): void
  (e: 'change', value: { userAgreement: boolean; privacyPolicy: boolean }): void
}>()

function handleUserAgreementChange(value: boolean) {
  emit('update:userAgreement', value)
  emitChange()
}

function handlePrivacyPolicyChange(value: boolean) {
  emit('update:privacyPolicy', value)
  emitChange()
}

function emitChange() {
  emit('change', {
    userAgreement: userAgreementChecked.value,
    privacyPolicy: privacyPolicyChecked.value
  })
}

defineExpose({
  allChecked,
  userAgreementChecked,
  privacyPolicyChecked
})
</script>

<style scoped lang="scss">
.agreement-box {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.checkbox-item {
  margin-top: 16rpx;
}

.agreement-text {
  font-size: 28rpx;
  color: #333333;
}
</style>