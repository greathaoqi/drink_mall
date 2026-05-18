<template>
  <view class="dm-page withdraw-page">
    <view class="withdraw-hero">
      <text class="eyebrow">提现申请</text>
      <text class="amount">¥{{ fmt(overview.balance) }}</text>
      <text class="copy">可提现余额。最小提现金额、手续费、到账周期以后端配置为准。</text>
    </view>

    <view class="dm-card form-card">
      <view class="field">
        <text>提现金额</text>
        <input v-model.trim="form.amount" type="digit" placeholder="请输入提现金额" />
      </view>
      <view class="field">
        <text>开户银行</text>
        <input v-model.trim="form.bankName" placeholder="请输入开户银行" />
      </view>
      <view class="field">
        <text>银行卡号</text>
        <input v-model.trim="form.bankAccount" placeholder="请输入银行卡号" />
      </view>
      <view class="field">
        <text>开户人姓名</text>
        <input v-model.trim="form.accountName" placeholder="需与实名认证姓名一致" />
      </view>
      <view class="rule-box">
        <text>提现前必须完成实名认证；费用、最小金额、冻结期和审核规则由后台配置返回或审核时执行。</text>
      </view>
      <button class="submit" :loading="loading" @click="submit">提交申请</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { assetApi } from '@/services/asset'
import { requirePageLogin, requireRealName } from '@/utils/auth'

const form = ref({ amount: '', bankName: '', bankAccount: '', accountName: '' })
const overview = ref<any>({})
const loading = ref(false)

function fmt(value: any) {
  return Number(value || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

onLoad(async () => {
  if (!requirePageLogin()) return
  requireRealName('提现前请先完成实名认证')
  try {
    overview.value = (await assetApi.overview()).data || {}
  } catch {}
})

async function submit() {
  if (!requirePageLogin() || !requireRealName('提现前请先完成实名认证')) return
  if (!form.value.amount || !form.value.bankName || !form.value.bankAccount || !form.value.accountName) {
    uni.showToast({ title: '请填写完整提现信息', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await assetApi.withdraw(form.value)
    uni.showToast({ title: '申请已提交', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 500)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.withdraw-page {
  padding: 24rpx var(--dm-page-x) 44rpx;
}

.withdraw-hero {
  padding: 32rpx 30rpx;
  border-radius: var(--dm-radius-lg);
  color: #3A1A00;
  background: var(--dm-grad-gold);
  box-shadow: 0 12rpx 36rpx rgba(211, 138, 0, 0.2);
}

.eyebrow,
.copy {
  display: block;
  color: #7A4810;
  font-size: 23rpx;
}

.amount {
  display: block;
  margin-top: 8rpx;
  font-size: 58rpx;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}

.copy {
  margin-top: 8rpx;
  line-height: 1.6;
}

.form-card {
  margin-top: 22rpx;
  padding: 30rpx;
}

.field {
  margin-bottom: 24rpx;
}

.field text {
  display: block;
  color: var(--dm-text-2);
  font-size: 24rpx;
  margin-bottom: 10rpx;
}

.field input {
  height: 84rpx;
  padding: 0 22rpx;
  border-radius: var(--dm-radius-md);
  background: var(--dm-cream-100);
}

.rule-box {
  padding: 22rpx;
  border-radius: var(--dm-radius-md);
  background: var(--dm-gold-50);
  color: #7A5610;
  font-size: 23rpx;
  line-height: 1.6;
}

.submit {
  width: 100%;
  height: 92rpx;
  line-height: 92rpx;
  margin-top: 30rpx;
  border: 0;
  border-radius: var(--dm-radius-pill);
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-weight: 800;
}

button:after {
  border: 0;
}
</style>
