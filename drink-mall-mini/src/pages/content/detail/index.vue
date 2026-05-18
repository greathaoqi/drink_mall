<template>
  <view class="dm-page detail-page">
    <view class="article" :class="{ locked: !canView }">
      <view class="tag-row">
        <text class="tag">{{ typeLabel }}</text>
        <text v-if="detail.paid" class="tag pay-tag">{{ canView ? '已解锁' : '权限内容' }}</text>
      </view>
      <text class="title">{{ detail.title || '内容详情' }}</text>
      <view class="author-row">
        <view class="avatar">醇</view>
        <view class="author-main">
          <text class="author-name">{{ detail.author || '醇品汇 · 品酒师' }}</text>
          <text class="meta">{{ formatDate(detail.createdAt || detail.publishTime) }} · 阅读 {{ detail.viewCount || detail.views || 0 }}</text>
        </view>
        <button class="follow">关注</button>
      </view>

      <image v-if="detail.type !== 'video'" class="cover" :src="detail.coverUrl || detail.imageUrl || '/static/images/page-content.png'" mode="aspectFill" />
      <video v-if="detail.type === 'video' && canView" :src="detail.videoUrl" controls class="video" />

      <rich-text v-if="detail.type !== 'video' && canView" class="content" :nodes="detail.content || detail.summary || ''" />
      <view v-if="canView && detail.type === 'video'" class="content">
        <text>{{ detail.summary || detail.description || '视频内容已解锁，可点击上方播放器观看。' }}</text>
      </view>

      <view v-if="canView && detail.purchased" class="notice">已购买，后续可直接观看。</view>

      <!-- D-PREVIEW-01: Show preview for unpurchased paid content -->
      <view v-if="!canView && detail.preview" class="preview-section">
        <text class="preview-label">内容预览</text>
        <text class="preview-text">{{ detail.preview }}</text>
      </view>

      <view v-if="!canView" class="lock-card">
        <view class="lock-icon">锁</view>
        <text class="lock-title">当前内容需解锁</text>
        <text class="lock-text">{{ detail.lockReason || '权限由后台配置返回，请按提示完成购买或升级后查看。' }}</text>
        <PayMethodSelector v-if="detail.canBuy" v-model="payMethod" :methods="methods" title="内容支付方式" />
        <button v-if="detail.canBuy" class="buy-btn" @click="buy">购买内容</button>
      </view>
    </view>

    <view class="bottom-bar">
      <view class="comment-input">写下你的看法...</view>
      <!-- D-LIKE-01: Like button with toggle and count -->
      <button class="icon-btn" :class="{ liked: liked }" @click="toggleLike">{{ liked ? '♥' : '♡' }}</button>
      <text class="like-count">{{ likeCount }}</text>
      <button class="icon-btn" open-type="share">↗</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import PayMethodSelector from '@/components/PayMethodSelector/PayMethodSelector.vue'
import { contentApi } from '@/services/content'
import { requireLogin } from '@/utils/auth'

const id = ref('')
const type = ref('video')
const detail = ref<any>({})
const payMethod = ref('')
const methods = ref<any[]>([])
// D-LIKE-01: Like state variables
const liked = ref(false)
const likeCount = ref(0)
const canView = computed(() => Boolean(detail.value.canView))
const selectedMethod = computed(() => methods.value.find((m) => m.value === payMethod.value))
const typeLabel = computed(() => {
  if (detail.value.categoryName) return detail.value.categoryName
  if ((detail.value.type || type.value) === 'video') return '视频'
  if ((detail.value.type || type.value) === 'activity') return '联营商动态'
  return '品酒知识'
})

function normalizeMethods(raw: any[]) {
  return raw.map((m) => ({
    value: m.value || m.code || m.payMethod || m.method,
    label: m.label || m.text || m.name || m.title || m.value || m.code,
    tip: m.tip || m.description,
    disabled: Boolean(m.disabled || m.available === false),
    insufficientText: m.insufficientText || m.unavailableReason || m.reason
  })).filter((m) => m.value)
}

async function load() {
  detail.value = (await contentApi.detail(id.value, type.value)).data || {}
  const raw = detail.value.payMethods || detail.value.availablePayMethods || []
  methods.value = detail.value.canBuy ? normalizeMethods(raw) : []
  payMethod.value = methods.value.find((m) => !m.disabled)?.value || ''
  // D-LIKE-01: Extract like info (userLiked populated in Plan 06)
  liked.value = Boolean(detail.value.userLiked)
  likeCount.value = detail.value.likes || 0
}

async function buy() {
  if (!requireLogin()) return
  if (detail.value.purchased) {
    uni.showToast({ title: '已购买，无需重复购买', icon: 'none' })
    return
  }
  if (!detail.value.canBuy) {
    uni.showToast({ title: detail.value.lockReason || '当前内容暂不可购买', icon: 'none' })
    return
  }
  if (!payMethod.value || !selectedMethod.value) {
    uni.showToast({ title: '请选择支付方式', icon: 'none' })
    return
  }
  if (selectedMethod.value.disabled) {
    uni.showToast({ title: selectedMethod.value.insufficientText || '当前支付方式不可用', icon: 'none' })
    return
  }

  try {
    const res = await contentApi.buy(id.value, type.value, payMethod.value)

    // D-CPAY-05: Check if response is PayResponse (online payment)
    if (res.data && res.data.orderNo && res.data.prepayId) {
      // Online payment: show payment created message
      // For actual WeChat Pay integration, would need wx.requestPayment API call
      uni.showToast({ title: '支付订单已创建', icon: 'success' })
      // Poll for payment status or wait for callback
      // For demo/Phase 1: reload to check status
      setTimeout(() => load(), 2000)
    } else {
      // Non-online payment: immediate success
      uni.showToast({ title: '购买成功', icon: 'success' })
      load()
    }
  } catch (e: any) {
    // D-CPAY-09: Show error toast on failure
    uni.showToast({ title: e.message || '购买失败', icon: 'none' })
  }
}

// D-LIKE-01: Toggle like function
async function toggleLike() {
  if (!requireLogin()) return
  if (!detail.value.purchased) {
    uni.showToast({ title: '请先购买内容后再点赞', icon: 'none' })
    return
  }
  try {
    const res = (await contentApi.like(id.value, type.value)).data
    liked.value = res.liked
    likeCount.value = res.likes
  } catch (e: any) {
    uni.showToast({ title: e.message || '操作失败', icon: 'none' })
  }
}

function formatDate(value?: string) {
  if (!value) return '刚刚'
  return String(value).slice(0, 10)
}

onLoad((options: any) => {
  id.value = options.id
  type.value = options.type || 'video'
  load()
})
</script>

<style scoped lang="scss">
.detail-page {
  min-height: 100vh;
  background: #FFFFFF;
  padding-bottom: calc(128rpx + env(safe-area-inset-bottom));
}

.article {
  padding: 28rpx 40rpx 40rpx;
}

.tag-row {
  display: flex;
  gap: 12rpx;
}

.tag {
  display: inline-flex;
  padding: 6rpx 14rpx;
  border-radius: var(--dm-radius-xs);
  background: var(--dm-gold-50);
  color: var(--dm-gold-600);
  font-size: 22rpx;
  font-weight: 700;
}

.pay-tag {
  background: #E8EFFD;
  color: var(--dm-blue-500);
}

.title {
  display: block;
  margin-top: 18rpx;
  color: var(--dm-text-1);
  font-size: 44rpx;
  font-weight: 900;
  line-height: 1.4;
}

.author-row {
  display: flex;
  align-items: center;
  gap: 18rpx;
  margin-top: 24rpx;
}

.avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: var(--dm-grad-gold);
  color: #3A1A00;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  font-weight: 900;
}

.author-main {
  flex: 1;
  min-width: 0;
}

.author-name,
.meta {
  display: block;
}

.author-name {
  color: var(--dm-text-1);
  font-size: 24rpx;
  font-weight: 800;
}

.meta {
  margin-top: 4rpx;
  color: var(--dm-text-3);
  font-size: 22rpx;
}

.follow {
  width: 112rpx;
  height: 56rpx;
  line-height: 56rpx;
  border: 2rpx solid var(--dm-gold-500);
  border-radius: var(--dm-radius-pill);
  background: #FFFFFF;
  color: var(--dm-gold-500);
  font-size: 24rpx;
  font-weight: 800;
}

.cover,
.video {
  width: 100%;
  height: 420rpx;
  margin-top: 32rpx;
  border-radius: 20rpx;
  overflow: hidden;
  background: var(--dm-cream-100);
}

.content {
  display: block;
  margin-top: 36rpx;
  color: var(--dm-text-1);
  font-size: 30rpx;
  line-height: 1.9;
}

.notice {
  display: inline-flex;
  margin-top: 28rpx;
  padding: 16rpx 22rpx;
  border-radius: var(--dm-radius-sm);
  background: var(--dm-gold-50);
  color: #7A5610;
  font-size: 24rpx;
}

.lock-card {
  margin-top: 36rpx;
  padding: 34rpx 28rpx;
  border: 2rpx solid var(--dm-line);
  border-radius: 24rpx;
  background: var(--dm-cream-50);
}

.lock-icon {
  width: 76rpx;
  height: 76rpx;
  border-radius: 50%;
  background: var(--dm-grad-gold);
  color: #3A1A00;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
}

.lock-title,
.lock-text {
  display: block;
}

.lock-title {
  margin-top: 18rpx;
  color: var(--dm-text-1);
  font-size: 32rpx;
  font-weight: 900;
}

.lock-text {
  margin-top: 10rpx;
  color: var(--dm-text-3);
  font-size: 25rpx;
  line-height: 1.6;
}

.buy-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  margin-top: 24rpx;
  border-radius: var(--dm-radius-pill);
  background: var(--dm-grad-gold);
  color: #3A1A00;
  font-size: 30rpx;
  font-weight: 900;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  gap: 18rpx;
  padding: 20rpx 32rpx calc(20rpx + env(safe-area-inset-bottom));
  border-top: 2rpx solid var(--dm-line-soft);
  background: #FFFFFF;
}

.comment-input {
  flex: 1;
  height: 72rpx;
  line-height: 72rpx;
  padding: 0 28rpx;
  border-radius: var(--dm-radius-pill);
  background: var(--dm-bg-app);
  color: var(--dm-text-3);
  font-size: 24rpx;
}

.icon-btn {
  width: 64rpx;
  height: 64rpx;
  line-height: 64rpx;
  border-radius: 50%;
  background: #FFFFFF;
  color: var(--dm-text-2);
  font-size: 34rpx;
}

/* D-PREVIEW-01: Preview section styles */
.preview-section {
  margin-top: 28rpx;
  padding: 24rpx;
  background: var(--dm-cream-50);
  border-radius: var(--dm-radius-md);
}

.preview-label {
  display: block;
  font-size: 24rpx;
  color: var(--dm-gold-600);
  margin-bottom: 12rpx;
}

.preview-text {
  display: block;
  font-size: 28rpx;
  color: var(--dm-text-2);
  line-height: 1.6;
}

/* D-LIKE-01: Like button styles */
.liked {
  color: #E53935;
}

.like-count {
  font-size: 24rpx;
  color: var(--dm-text-3);
  margin-left: 4rpx;
}
</style>
