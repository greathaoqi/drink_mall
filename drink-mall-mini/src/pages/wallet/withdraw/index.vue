<template>
  <view class="withdraw-page">
    <view class="form-card">
      <view class="balance-hint">可用余额：¥{{ availableBalance }}</view>
      <view class="form-item">
        <text class="label">提现金额（元）</text>
        <input v-model="form.amount" type="digit" placeholder="最低1元" class="input" />
      </view>
      <view class="form-item">
        <text class="label">开户银行</text>
        <input v-model="form.bankName" type="text" placeholder="如：中国工商银行" class="input" />
      </view>
      <view class="form-item">
        <text class="label">银行卡号</text>
        <input v-model="form.bankAccount" type="digit" placeholder="请输入银行卡号" class="input" />
      </view>
      <view class="form-item">
        <text class="label">开户人姓名</text>
        <input v-model="form.accountName" type="text" placeholder="请输入真实姓名" class="input" />
      </view>
    </view>
    <view class="tips">提现审核1-3个工作日，余额将冻结至审核完成</view>
    <button class="submit-btn" :loading="loading" @click="handleSubmit">提交申请</button>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { http } from '@/utils/request'

const loading = ref(false)
const form = ref({ amount: '', bankName: '', bankAccount: '', accountName: '' })
const availableBalance = ref('0.00')

onLoad(async () => {
  const res = await http.get('/user/info')
  if (res.code === 200) availableBalance.value = res.data?.balance || '0.00'
})

const handleSubmit = async () => {
  if (!form.value.amount || Number(form.value.amount) < 1) {
    uni.showToast({ title: '最低提现金额为1元', icon: 'none' }); return
  }
  if (!form.value.bankName) {
    uni.showToast({ title: '请填写开户银行', icon: 'none' }); return
  }
  if (!form.value.bankAccount) {
    uni.showToast({ title: '请填写银行卡号', icon: 'none' }); return
  }
  if (!form.value.accountName) {
    uni.showToast({ title: '请填写开户人姓名', icon: 'none' }); return
  }
  loading.value = true
  try {
    const res = await http.post('/user/withdrawal', {
      amount: Number(form.value.amount),
      bankName: form.value.bankName,
      bankAccount: form.value.bankAccount,
      accountName: form.value.accountName
    })
    if (res.code === 200) {
      uni.showToast({ title: '申请已提交', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 1500)
    } else {
      uni.showToast({ title: res.message || '提交失败', icon: 'none' })
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.withdraw-page { background: #f5f5f5; min-height: 100vh; padding: 20rpx; }
.form-card { background: #fff; border-radius: 12rpx; padding: 0 30rpx; margin-bottom: 20rpx; }
.form-item { padding: 28rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.form-item:last-child { border-bottom: none; }
.label { font-size: 28rpx; color: #333; display: block; margin-bottom: 16rpx; }
.input { width: 100%; font-size: 28rpx; color: #333; }
.tips { font-size: 24rpx; color: #999; padding: 0 10rpx 20rpx; }
.submit-btn { margin: 20rpx 0; background: #8a4f22; color: #fff; border-radius: 40rpx; font-size: 30rpx; border: none; }
.balance-hint { padding: 20rpx 0 10rpx; font-size: 26rpx; color: #8a4f22; font-weight: 600; }
</style>
