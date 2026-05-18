<template>
  <view class="dm-page help-page">
    <view class="hero">
      <view class="status-space"></view>
      <view class="hero-inner">
        <view class="headset">☎</view>
        <text class="hero-title">客服中心</text>
        <text class="hero-subtitle">很高兴为您服务，可选择以下方式联系我们</text>
      </view>
    </view>

    <view class="body">
      <view class="contact-grid">
        <view class="contact-card online dm-press" @click="openOnline">
          <text class="contact-icon">聊</text>
          <text class="contact-title">在线客服</text>
          <text class="contact-subtitle">{{ service.onlineTime || '09:00 - 22:00 在线' }}</text>
        </view>
        <view class="contact-card phone dm-press" @click="callPhone">
          <text class="contact-icon">电</text>
          <text class="contact-title">电话客服</text>
          <text class="phone-number">{{ service.phone || '400-668-8821' }}</text>
        </view>
      </view>

      <view class="section">
        <text class="section-title">常见问题</text>
        <view class="faq-list dm-card">
          <view v-for="item in faqs" :key="item.title" class="faq-row dm-press" @click="showFaq(item)">
            <view class="faq-icon">{{ item.icon }}</view>
            <text>{{ item.title }}</text>
            <text class="chevron">›</text>
          </view>
        </view>
      </view>

      <view class="mail-tip">
        <text>反馈与建议请发送邮件至 {{ service.email || 'service@chunpinhui.com' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { configApi } from '@/services/config'

const service = ref<any>({})
const faqs = [
  { title: '如何成为联营商？', icon: '营', answer: '请进入招商专区查看入驻说明，具体门槛和权益以后台配置为准。' },
  { title: '佣金如何计算与结算？', icon: '佣', answer: '佣金比例、冻结期和结算规则均由后台配置，订单满足结算条件后入账。' },
  { title: '订单发货时间及物流查询', icon: '物', answer: '已发货订单可在订单详情查看物流单号和配送进度。' },
  { title: '退换货政策与售后流程', icon: '售', answer: '一期支持线上提交售后申请，后台审核后线下处理。' },
  { title: '提现规则与到账时间', icon: '提', answer: '最小提现金额、手续费和到账说明均以系统配置为准。' },
  { title: '团队等级与升级规则', icon: '级', answer: '等级名称、顺序和晋升条件由后台统一配置。' }
]

async function load() {
  try {
    service.value = (await configApi.customerService()).data || {}
  } catch {
    service.value = {}
  }
}

function openOnline() {
  if (service.value.wechat) {
    uni.setClipboardData({ data: service.value.wechat })
    return
  }
  uni.showToast({ title: '请通过页面客服入口联系平台', icon: 'none' })
}

function callPhone() {
  const phone = service.value.phone || '400-668-8821'
  uni.makePhoneCall({ phoneNumber: phone })
}

function showFaq(item: any) {
  uni.showModal({ title: item.title, content: item.answer, showCancel: false, confirmText: '知道了' })
}

onShow(load)
</script>

<style scoped lang="scss">
.help-page {
  padding-bottom: 40rpx;
}

.hero {
  background: var(--dm-grad-brown);
  color: var(--dm-text-on-brown);
  padding-bottom: 60rpx;
}

.status-space {
  height: var(--status-bar-height);
}

.hero-inner {
  padding: 18rpx 48rpx 0;
  text-align: center;
}

.headset {
  width: 96rpx;
  height: 96rpx;
  margin: 0 auto;
  border-radius: 50%;
  background: rgba(255, 214, 70, 0.14);
  color: #FFD685;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 46rpx;
  font-weight: 900;
}

.hero-title,
.hero-subtitle,
.contact-title,
.contact-subtitle,
.phone-number,
.section-title {
  display: block;
}

.hero-title {
  margin-top: 20rpx;
  color: #FFE0A0;
  font-size: 36rpx;
  font-weight: 900;
}

.hero-subtitle {
  margin-top: 12rpx;
  color: rgba(246, 231, 194, 0.72);
  font-size: 24rpx;
}

.body {
  margin-top: -32rpx;
  padding: 0 32rpx 40rpx;
}

.contact-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}

.contact-card {
  min-height: 184rpx;
  border-radius: 28rpx;
  padding: 32rpx 28rpx;
  box-sizing: border-box;
}

.contact-card.online {
  background: var(--dm-grad-gold);
  color: #3A1A00;
}

.contact-card.phone {
  border: 2rpx solid var(--dm-line);
  background: #FFFFFF;
  color: var(--dm-text-1);
}

.contact-icon {
  display: flex;
  width: 56rpx;
  height: 56rpx;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  font-size: 25rpx;
  font-weight: 900;
}

.contact-title {
  margin-top: 16rpx;
  font-size: 28rpx;
  font-weight: 900;
}

.contact-subtitle,
.phone-number {
  margin-top: 8rpx;
  font-size: 22rpx;
}

.phone-number {
  color: var(--dm-gold-500);
  font-weight: 800;
}

.section {
  margin-top: 28rpx;
}

.section-title {
  margin-bottom: 18rpx;
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 900;
}

.faq-list {
  overflow: hidden;
}

.faq-row {
  min-height: 96rpx;
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 0 24rpx;
  border-bottom: 2rpx solid var(--dm-line-soft);
  color: var(--dm-text-1);
  font-size: 27rpx;
}

.faq-row:last-child {
  border-bottom: 0;
}

.faq-icon {
  width: 48rpx;
  height: 48rpx;
  border-radius: 14rpx;
  background: var(--dm-gold-50);
  color: var(--dm-gold-600);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22rpx;
  font-weight: 900;
}

.chevron {
  margin-left: auto;
  color: #C9BFA9;
  font-size: 36rpx;
}

.mail-tip {
  margin-top: 28rpx;
  padding: 24rpx 28rpx;
  border-radius: 20rpx;
  background: var(--dm-gold-50);
  color: #7A5610;
  font-size: 24rpx;
  line-height: 1.55;
}
</style>
