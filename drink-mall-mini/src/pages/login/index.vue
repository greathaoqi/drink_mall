<template>
  <view class="login-page">
    <view class="hero-glow"></view>
    <view class="hero">
      <view class="brand-mark">醇</view>
      <text class="brand">醇品汇</text>
      <text class="slogan">甄选美酒 · 共创财富</text>
    </view>

    <view class="login-panel">
      <text class="panel-title">欢迎登录</text>
      <text class="panel-desc">登录后享受完整购物与分销服务</text>

      <view class="invite-card">
        <view class="invite-head">
          <text class="invite-title">完善邀请信息</text>
          <text class="invite-status">{{ inviteCode ? '已获取' : '待填写' }}</text>
        </view>
        <view class="invite-input-row">
          <input
            v-model.trim="inviteCode"
            class="invite-input"
            maxlength="24"
            placeholder="请输入好友邀请码"
            placeholder-class="input-placeholder"
          />
          <button class="scan-btn" @click="showScanHint">扫码</button>
        </view>
        <view v-if="sourceText" class="source-row">推荐来源：{{ sourceText }}</view>
        <view class="invite-tip">请通过好友分享码进入小程序完成注册，普通新用户注册必须绑定推荐人。</view>
      </view>

      <button class="wechat-btn" :loading="loading" @click="submit">
        <text class="wechat-icon">微</text>
        微信快捷登录
      </button>
      <button class="guest-btn" @click="goHome">游客浏览</button>
      <button class="demo-btn" :loading="demoLoading" @click="enterDemo">进入演示账号</button>

      <label class="agreement-row" @click.stop="toggleAgreement">
        <view class="check-box" :class="{ checked: agreed }">
          <text v-if="agreed">✓</text>
        </view>
        <view class="agreement-copy">
          已阅读并同意
          <text @click.stop="openAgreement('user')">《用户服务协议》</text>
          <text @click.stop="openAgreement('privacy')">《隐私政策》</text>
        </view>
      </label>
    </view>

    <view v-if="showPhoneSheet" class="sheet-mask" @click="showPhoneSheet = false">
      <view class="phone-sheet" @click.stop>
        <view class="sheet-head">
          <view class="app-badge">醇</view>
          <view class="sheet-title-wrap">
            <text class="sheet-app">醇品汇</text>
            <text class="sheet-sub">申请获取你的手机号</text>
          </view>
          <text class="sheet-close" @click="showPhoneSheet = false">×</text>
        </view>

        <view class="phone-option selected">
          <view>
            <text class="option-title">使用本机号码</text>
            <text class="masked-phone">138 **** 8888</text>
          </view>
          <view class="radio-dot">✓</view>
        </view>
        <view class="phone-option">
          <text class="option-title">使用其他号码</text>
          <text class="chevron">›</text>
        </view>

        <button class="allow-btn" :loading="loading" @click="confirmPhoneAndLogin">确认绑定并完成注册</button>
        <button class="reject-btn" @click="showPhoneSheet = false">拒绝</button>
      </view>
    </view>

    <view v-if="showAgreementPanel" class="agreement-mask">
      <view class="agreement-panel">
        <view class="agreement-nav">
          <text class="back" @click="showAgreementPanel = false">‹</text>
          <text>{{ agreementTitle }}</text>
        </view>
        <scroll-view scroll-y class="agreement-scroll">
          <text class="agreement-title">醇品汇{{ agreementTitle }}</text>
          <text class="agreement-date">更新于 2026 年 4 月 15 日</text>
          <view class="agreement-section">
            <text class="section-title">一、服务条款的接受</text>
            <text class="section-copy">欢迎使用醇品汇微信小程序提供的酒类商品选购、分销邀请、内容浏览等服务。使用完整功能前，请仔细阅读并充分理解本协议内容。</text>
          </view>
          <view class="agreement-section">
            <text class="section-title">二、账号注册与推荐绑定</text>
            <text class="section-copy">普通新用户须通过好友邀请码、分享链接、商品分享或二维码场景完成推荐绑定后，方可完成注册。邀请人绑定后按平台规则保持稳定关系。</text>
          </view>
          <view class="agreement-section">
            <text class="section-title">三、商品与订单</text>
            <text class="section-copy">商品价格、库存、规格、支付方式与售后规则以页面展示及后台配置为准。平台不支持未经配置允许的组合支付或资产互通。</text>
          </view>
          <view class="agreement-section">
            <text class="section-title">四、分销与奖励</text>
            <text class="section-copy">奖励、等级、佣金、提现等规则以后端配置为准。用户不得通过虚假交易、刷单等方式套取奖励。</text>
          </view>
          <view class="agreement-section">
            <text class="section-title">五、隐私与信息保护</text>
            <text class="section-copy">平台将按照法律法规和本隐私政策处理你的微信授权信息、联系方式、订单与实名资料，并仅在业务所需范围内使用。</text>
          </view>
          <button class="agree-now" @click="acceptAgreement">已阅读并同意</button>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { demoLogin, wechatLogin } from '@/services/auth'
import { useUserStore } from '@/store/user'
import { parseReferralOptions } from '@/utils/referral'

const store = useUserStore()
const inviteCode = ref(store.referralCode)
const agreed = ref(false)
const loading = ref(false)
const demoLoading = ref(false)
const showPhoneSheet = ref(false)
const showAgreementPanel = ref(false)
const agreementKind = ref<'user' | 'privacy'>('user')
const scene = ref('')
const sourceProductId = ref('')

const sourceText = computed(() => {
  if (sourceProductId.value) return '商品分享'
  if (scene.value === 'share_mini') return '小程序分享'
  if (scene.value) return '二维码场景'
  return inviteCode.value ? '邀请码' : ''
})
const agreementTitle = computed(() => agreementKind.value === 'privacy' ? '隐私政策' : '用户服务协议')

onLoad((options: any) => {
  const referral = parseReferralOptions(options)
  if (referral.inviteCode) {
    inviteCode.value = referral.inviteCode
    store.setReferral(inviteCode.value)
  }
  sourceProductId.value = referral.sourceProductId
  scene.value = referral.scene
})

function toggleAgreement() {
  agreed.value = !agreed.value
}

function openAgreement(kind: 'user' | 'privacy') {
  agreementKind.value = kind
  showAgreementPanel.value = true
}

function acceptAgreement() {
  agreed.value = true
  showAgreementPanel.value = false
}

function showScanHint() {
  uni.showToast({ title: '请使用微信扫码进入或手动输入邀请码', icon: 'none' })
}

function submit() {
  if (!agreed.value) {
    uni.showToast({ title: '请先勾选用户服务协议和隐私政策', icon: 'none' })
    return
  }
  if (!inviteCode.value) {
    uni.showToast({ title: '注册必须绑定推荐人，请输入邀请码或通过推荐链接进入', icon: 'none' })
    return
  }
  showPhoneSheet.value = true
}

async function confirmPhoneAndLogin() {
  if (!inviteCode.value) {
    showPhoneSheet.value = false
    uni.showToast({ title: '注册必须绑定推荐人，请输入邀请码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await wechatLogin({
      inviteCode: inviteCode.value,
      scene: scene.value,
      sourceProductId: sourceProductId.value,
      userAgreement: true,
      privacyPolicy: true
    })
    showPhoneSheet.value = false
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/profile/index' }), 300)
  } finally {
    loading.value = false
  }
}

async function enterDemo() {
  demoLoading.value = true
  try {
    await demoLogin()
    uni.showToast({ title: '已进入演示账号', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/profile/index' }), 300)
  } finally {
    demoLoading.value = false
  }
}

function goHome() {
  uni.switchTab({ url: '/pages/index/index' })
}
</script>

<style scoped lang="scss">
.login-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--dm-bg-app);
  color: var(--dm-text-1);
}

.hero-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 410rpx;
  background:
    radial-gradient(ellipse 80% 60% at 50% 30%, rgba(228, 165, 22, 0.18), transparent 60%),
    var(--dm-grad-brown);
}

.hero {
  position: relative;
  z-index: 1;
  padding: 126rpx 40rpx 148rpx;
  text-align: center;
  color: var(--dm-text-on-brown);
}

.brand-mark,
.app-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--dm-grad-gold);
  color: #3A1A00;
  font-weight: 900;
}

.brand-mark {
  width: 112rpx;
  height: 112rpx;
  margin: 0 auto 20rpx;
  border-radius: 50%;
  font-size: 48rpx;
  box-shadow: 0 20rpx 60rpx rgba(228, 165, 22, 0.28);
}

.brand,
.slogan,
.panel-title,
.panel-desc,
.invite-title,
.invite-status,
.source-row,
.invite-tip,
.sheet-app,
.sheet-sub,
.option-title,
.masked-phone,
.agreement-title,
.agreement-date,
.section-title,
.section-copy {
  display: block;
}

.brand {
  color: #FFD685;
  font-size: 56rpx;
  font-weight: 900;
  letter-spacing: 12rpx;
}

.slogan {
  margin-top: 14rpx;
  color: rgba(246, 231, 194, 0.72);
  font-size: 26rpx;
  letter-spacing: 3rpx;
}

.login-panel {
  position: relative;
  z-index: 2;
  min-height: 572rpx;
  margin-top: -74rpx;
  padding: 48rpx 40rpx calc(42rpx + env(safe-area-inset-bottom));
  border-radius: 48rpx 48rpx 0 0;
  background: #FFFFFF;
}

.panel-title {
  text-align: center;
  font-size: 44rpx;
  font-weight: 900;
}

.panel-desc {
  margin-top: 10rpx;
  text-align: center;
  color: var(--dm-text-3);
  font-size: 26rpx;
}

.invite-card {
  margin-top: 34rpx;
  padding: 24rpx;
  border: 2rpx solid #F2E0B0;
  border-radius: 20rpx;
  background: var(--dm-cream-50);
}

.invite-head,
.invite-input-row,
.sheet-head,
.phone-option {
  display: flex;
  align-items: center;
}

.invite-head {
  justify-content: space-between;
}

.invite-title {
  font-size: 28rpx;
  font-weight: 800;
}

.invite-status {
  height: 40rpx;
  line-height: 40rpx;
  padding: 0 16rpx;
  border-radius: 999rpx;
  background: var(--dm-gold-50);
  color: var(--dm-gold-500);
  font-size: 22rpx;
}

.invite-input-row {
  gap: 16rpx;
  margin-top: 18rpx;
}

.invite-input {
  flex: 1;
  height: 96rpx;
  padding: 0 22rpx;
  border-radius: 18rpx;
  border: 3rpx solid var(--dm-gold-300);
  background: var(--dm-cream-100);
  color: var(--dm-gold-600);
  font-size: 40rpx;
  font-weight: 900;
  letter-spacing: 8rpx;
  box-sizing: border-box;
}

.input-placeholder {
  color: #B8AA96;
  font-size: 26rpx;
  letter-spacing: 0;
  font-weight: 400;
}

.scan-btn {
  flex: 0 0 112rpx;
  height: 76rpx;
  line-height: 76rpx;
  border-radius: 999rpx;
  background: var(--dm-brown-800);
  color: #FFE0A0;
  font-size: 26rpx;
}

.source-row {
  margin-top: 16rpx;
  color: var(--dm-gold-600);
  font-size: 24rpx;
}

.invite-tip {
  margin-top: 16rpx;
  padding: 18rpx 20rpx;
  border: 2rpx dashed var(--dm-gold-300);
  border-radius: 18rpx;
  background: rgba(255, 246, 218, 0.72);
  color: #7A5610;
  font-size: 24rpx;
  line-height: 1.6;
}

.wechat-btn,
.guest-btn,
.demo-btn,
.allow-btn,
.reject-btn,
.agree-now {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  border-radius: 999rpx;
  font-size: 30rpx;
  font-weight: 800;
}

.wechat-btn {
  margin-top: 34rpx;
  background: var(--dm-green-wechat);
  color: #FFFFFF;
  box-shadow: 0 12rpx 36rpx rgba(18, 185, 91, 0.24);
}

.wechat-icon {
  display: inline-flex;
  width: 40rpx;
  height: 40rpx;
  margin-right: 12rpx;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.18);
  font-size: 22rpx;
}

.guest-btn {
  margin-top: 18rpx;
  border: 2rpx solid #F0E0B7;
  background: var(--dm-cream-100);
  color: var(--dm-gold-600);
}

.demo-btn {
  margin-top: 16rpx;
  background: transparent;
  color: var(--dm-text-3);
  font-size: 26rpx;
  font-weight: 500;
}

.agreement-row {
  display: flex;
  align-items: flex-start;
  gap: 14rpx;
  margin-top: 26rpx;
  color: var(--dm-text-2);
  font-size: 24rpx;
  line-height: 1.6;
}

.check-box {
  flex: 0 0 32rpx;
  width: 32rpx;
  height: 32rpx;
  margin-top: 4rpx;
  border-radius: 8rpx;
  border: 2rpx solid #D8CCBA;
  box-sizing: border-box;
  text-align: center;
  line-height: 28rpx;
  color: #FFFFFF;
  font-size: 22rpx;
}

.check-box.checked {
  border-color: transparent;
  background: var(--dm-grad-gold);
}

.agreement-copy {
  flex: 1;
}

.agreement-copy text {
  color: var(--dm-gold-500);
}

.sheet-mask,
.agreement-mask {
  position: fixed;
  inset: 0;
  z-index: 50;
  background: rgba(0, 0, 0, 0.55);
}

.sheet-mask {
  display: flex;
  align-items: flex-end;
}

.phone-sheet {
  width: 100%;
  padding: 36rpx 40rpx calc(38rpx + env(safe-area-inset-bottom));
  border-radius: 40rpx 40rpx 0 0;
  background: #FFFFFF;
  box-sizing: border-box;
}

.sheet-head {
  justify-content: space-between;
  gap: 16rpx;
}

.app-badge {
  width: 72rpx;
  height: 72rpx;
  border-radius: 16rpx;
  font-size: 30rpx;
}

.sheet-title-wrap {
  flex: 1;
}

.sheet-app {
  font-size: 26rpx;
  font-weight: 800;
}

.sheet-sub {
  margin-top: 4rpx;
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.sheet-close {
  width: 48rpx;
  color: #999999;
  font-size: 42rpx;
  text-align: right;
}

.phone-option {
  justify-content: space-between;
  min-height: 96rpx;
  margin-top: 20rpx;
  padding: 18rpx 22rpx;
  border-radius: 20rpx;
  background: var(--dm-bg-app);
  box-sizing: border-box;
}

.option-title {
  font-size: 28rpx;
  font-weight: 700;
}

.masked-phone {
  margin-top: 6rpx;
  font-size: 36rpx;
  font-weight: 900;
  letter-spacing: 1rpx;
}

.radio-dot {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background: var(--dm-grad-gold);
  color: #FFFFFF;
  text-align: center;
  line-height: 36rpx;
  font-size: 22rpx;
  font-weight: 900;
}

.chevron {
  color: #999999;
  font-size: 42rpx;
}

.allow-btn {
  margin-top: 32rpx;
  color: #3A1A00;
  background: var(--dm-grad-gold);
}

.reject-btn {
  margin-top: 12rpx;
  background: transparent;
  color: var(--dm-text-3);
  font-size: 28rpx;
  font-weight: 500;
}

.agreement-mask {
  z-index: 60;
  background: #FFFFFF;
}

.agreement-panel {
  min-height: 100vh;
  background: #FFFFFF;
}

.agreement-nav {
  display: flex;
  align-items: center;
  height: 96rpx;
  padding: 48rpx 32rpx 0;
  border-bottom: 1rpx solid var(--dm-line-soft);
  color: var(--dm-text-1);
  font-size: 32rpx;
  font-weight: 800;
  box-sizing: content-box;
}

.back {
  width: 52rpx;
  margin-right: 8rpx;
  color: var(--dm-text-2);
  font-size: 58rpx;
  line-height: 1;
}

.agreement-scroll {
  height: calc(100vh - 144rpx);
  padding: 28rpx 40rpx 56rpx;
  box-sizing: border-box;
}

.agreement-title {
  font-size: 42rpx;
  font-weight: 900;
}

.agreement-date {
  margin-top: 10rpx;
  color: var(--dm-text-3);
  font-size: 24rpx;
}

.agreement-section {
  margin-top: 28rpx;
}

.section-title {
  color: var(--dm-text-1);
  font-size: 30rpx;
  font-weight: 800;
}

.section-copy {
  margin-top: 12rpx;
  color: var(--dm-text-2);
  font-size: 26rpx;
  line-height: 1.85;
}

.agree-now {
  margin-top: 38rpx;
  color: #3A1A00;
  background: var(--dm-grad-gold);
}

button::after {
  border: 0;
}
</style>
