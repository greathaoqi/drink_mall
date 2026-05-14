<template>
  <view class="page login">
    <view class="hero">
      <text class="brand">酒水分销商城</text>
      <text class="slogan">注册必须绑定推荐人</text>
    </view>
    <view class="card">
      <view class="field">
        <text>邀请码</text>
        <input v-model.trim="inviteCode" placeholder="请输入邀请码或通过推荐链接进入" />
      </view>
      <view class="hint" v-if="sourceText">推荐来源：{{ sourceText }}</view>
      <label class="agree"><checkbox :checked="agreed" @click="agreed = !agreed" />我已阅读并同意用户协议和隐私协议</label>
      <button class="primary" :loading="loading" @click="submit">微信快捷登录 / 注册</button>
      <button class="ghost" @click="goHome">暂不登录，浏览首页</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { wechatLogin } from '@/services/auth'
import { useUserStore } from '@/store/user'

const store = useUserStore()
const inviteCode = ref(store.referralCode)
const agreed = ref(false)
const loading = ref(false)
const scene = ref('')
const sourceProductId = ref('')
const sourceText = computed(() => sourceProductId.value ? '商品分享' : (scene.value ? '小程序分享' : (inviteCode.value ? '邀请码' : '')))

function readScene(raw?: string) {
  if (!raw) return {}
  const decoded = decodeURIComponent(raw)
  const query = decoded.includes('=') ? decoded : 'inviteCode=' + decoded
  return query.split('&').reduce<Record<string, string>>((acc, part) => {
    const [key, value] = part.split('=')
    if (key) acc[key] = value || ''
    return acc
  }, {})
}

onLoad((options: any) => {
  const sceneParams = readScene(options.scene)
  const code = options.inviteCode || options.inviter || sceneParams.inviteCode || sceneParams.inviter || options.scene
  if (code) {
    inviteCode.value = String(code)
    store.setReferral(inviteCode.value)
  }
  sourceProductId.value = options.productId || sceneParams.productId || ''
  scene.value = options.scene || ''
})

async function submit() {
  if (!agreed.value) {
    uni.showToast({ title: '请先勾选用户协议和隐私协议', icon: 'none' })
    return
  }
  if (!inviteCode.value) {
    uni.showToast({ title: '注册必须绑定推荐人，请输入邀请码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await wechatLogin({ inviteCode: inviteCode.value, scene: scene.value, sourceProductId: sourceProductId.value, userAgreement: true, privacyPolicy: true })
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => uni.switchTab({ url: '/pages/profile/index' }), 300)
  } finally {
    loading.value = false
  }
}
function goHome() { uni.switchTab({ url: '/pages/index/index' }) }
</script>

<style scoped lang="scss">
.page{min-height:100vh;background:#f6f1e8;color:#24170c}
.hero{height:430rpx;background:#2b1207;color:#fff;padding:130rpx 40rpx 0}
.brand{display:block;font-size:48rpx;font-weight:900;color:#f0bf55}
.slogan{display:block;margin-top:20rpx;color:#e8d6b8}
.card{background:#fff;margin:-44rpx 24rpx 0;padding:32rpx;border-radius:22rpx;box-shadow:0 10rpx 30rpx rgba(58,32,12,.08)}
.field{display:flex;flex-direction:column;gap:14rpx;font-weight:700}
.field input{height:86rpx;background:#f8f4ed;border-radius:12rpx;padding:0 22rpx}
.agree{display:flex;align-items:center;margin:28rpx 0;color:#6f6254;font-size:25rpx;line-height:1.45}
.hint{margin-top:18rpx;color:#b97700;font-size:24rpx}
.primary,.ghost{height:88rpx;line-height:88rpx;margin-top:20rpx;border:0;border-radius:999rpx;font-size:28rpx}
.primary{background:#b97700;color:#fff}
.ghost{background:#fff4df;color:#8a5b0e}
button:after{border:0}
</style>
