<template>
  <view class="dm-page realname-page">
    <view class="real-hero">
      <view class="badge">证</view>
      <text class="title">实名认证</text>
      <text class="copy">用于提现、分销奖励结算等关键场景，一期由后台人工审核。</text>
    </view>

    <view class="dm-card form-card">
      <view class="field">
        <text>真实姓名</text>
        <input v-model.trim="form.realName" placeholder="请输入身份证上的姓名" />
      </view>
      <view class="field">
        <text>身份证号</text>
        <input
          v-model.trim="form.idCardNo"
          maxlength="18"
          type="idcard"
          placeholder="请输入 18 位身份证号码"
          @blur="validateIdCard"
        />
        <text v-if="idCardError" class="field-error">{{ idCardError }}</text>
      </view>

      <view class="upload-section">
        <text class="section-title">身份证照片</text>
        <view class="upload-grid">
          <view class="upload-box" :class="{ filled: !!frontPreview }" @click="chooseIdImage('front')">
            <image v-if="frontPreview" class="id-preview" :src="frontPreview" mode="aspectFill" />
            <template v-else>
              <text class="upload-icon">人</text>
              <text>人像面</text>
              <text class="upload-sub">上传身份证人像面</text>
            </template>
            <view v-if="uploadingSide === 'front'" class="upload-mask">上传中...</view>
          </view>
          <view class="upload-box" :class="{ filled: !!backPreview }" @click="chooseIdImage('back')">
            <image v-if="backPreview" class="id-preview" :src="backPreview" mode="aspectFill" />
            <template v-else>
              <text class="upload-icon">徽</text>
              <text>国徽面</text>
              <text class="upload-sub">上传身份证国徽面</text>
            </template>
            <view v-if="uploadingSide === 'back'" class="upload-mask">上传中...</view>
          </view>
        </view>
      </view>

      <view class="notice">
        <text>认证须知</text>
        <text>信息仅用于身份核验；请确认资料真实有效，审核结果以后台人工审核为准。</text>
      </view>

      <label class="agreement">
        <checkbox :checked="agreed" color="#D38A00" @click="agreed = !agreed" />
        <text>我已阅读并同意《实名认证服务协议》</text>
      </label>

      <button class="submit" :loading="loading" @click="submit">提交认证</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { userApi } from '@/services/user'
import { useUserStore } from '@/store/user'
import { requirePageLogin } from '@/utils/auth'

type IdImageSide = 'front' | 'back'

const loading = ref(false)
const agreed = ref(true)
const uploadingSide = ref<IdImageSide | ''>('')
const idCardError = ref('')
const form = ref({
  realName: '',
  idCardNo: '',
  frontImageUrl: '',
  backImageUrl: '',
  frontLocalPath: '',
  backLocalPath: ''
})

const frontPreview = computed(() => form.value.frontLocalPath || form.value.frontImageUrl)
const backPreview = computed(() => form.value.backLocalPath || form.value.backImageUrl)

onLoad(() => requirePageLogin())

function isValidChineseIdCard(value: string) {
  const id = value.trim().toUpperCase()
  if (!/^\d{17}[\dX]$/.test(id)) return false

  const birth = id.slice(6, 14)
  const year = Number(birth.slice(0, 4))
  const month = Number(birth.slice(4, 6))
  const day = Number(birth.slice(6, 8))
  const date = new Date(year, month - 1, day)
  if (date.getFullYear() !== year || date.getMonth() !== month - 1 || date.getDate() !== day) {
    return false
  }

  const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
  const checks = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']
  const sum = weights.reduce((total, weight, index) => total + Number(id[index]) * weight, 0)
  return checks[sum % 11] === id[17]
}

function validateIdCard() {
  form.value.idCardNo = form.value.idCardNo.trim().toUpperCase()
  idCardError.value = form.value.idCardNo && !isValidChineseIdCard(form.value.idCardNo)
    ? '请输入有效的18位身份证号码'
    : ''
  return !idCardError.value
}

function chooseIdImage(side: IdImageSide) {
  if (uploadingSide.value) return
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const filePath = res.tempFilePaths?.[0]
      if (!filePath) return
      uploadingSide.value = side
      try {
        const uploaded = await userApi.uploadImage(filePath)
        if (side === 'front') {
          form.value.frontLocalPath = filePath
          form.value.frontImageUrl = uploaded.url
        } else {
          form.value.backLocalPath = filePath
          form.value.backImageUrl = uploaded.url
        }
      } catch (error: any) {
        uni.showToast({ title: error.message || '图片上传失败', icon: 'none' })
      } finally {
        uploadingSide.value = ''
      }
    }
  })
}

async function submit() {
  if (!requirePageLogin()) return
  if (!form.value.realName || !form.value.idCardNo) {
    uni.showToast({ title: '请填写实名信息', icon: 'none' })
    return
  }
  if (!validateIdCard()) {
    uni.showToast({ title: idCardError.value, icon: 'none' })
    return
  }
  if (!form.value.frontImageUrl || !form.value.backImageUrl) {
    uni.showToast({ title: '请上传身份证正反面照片', icon: 'none' })
    return
  }
  if (!agreed.value) {
    uni.showToast({ title: '请先同意认证协议', icon: 'none' })
    return
  }
  loading.value = true
  try {
    await userApi.submitRealName({
      realName: form.value.realName,
      idCardNo: form.value.idCardNo,
      frontImageUrl: form.value.frontImageUrl,
      backImageUrl: form.value.backImageUrl
    })
    useUserStore().patchUser({ realNameStatus: 'PENDING' })
    uni.showToast({ title: '已提交审核', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 500)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.realname-page {
  padding-bottom: 44rpx;
}

.real-hero {
  padding: 72rpx var(--dm-page-x) 62rpx;
  text-align: center;
  color: var(--dm-text-on-brown);
  background: var(--dm-grad-brown);
}

.badge {
  width: 126rpx;
  height: 126rpx;
  margin: 0 auto;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3A1A00;
  background: var(--dm-grad-gold);
  font-size: 46rpx;
  font-weight: 900;
  box-shadow: 0 16rpx 48rpx rgba(228, 165, 22, 0.32);
}

.title {
  display: block;
  margin-top: 22rpx;
  color: #FFE0A0;
  font-size: 36rpx;
  font-weight: 800;
}

.copy {
  display: block;
  margin-top: 10rpx;
  color: rgba(246, 231, 194, 0.68);
  font-size: 24rpx;
  line-height: 1.6;
}

.form-card {
  margin: -24rpx var(--dm-page-x) 0;
  padding: 30rpx;
  position: relative;
  z-index: 2;
}

.field {
  margin-bottom: 24rpx;
}

.field text,
.section-title {
  display: block;
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 800;
  margin-bottom: 12rpx;
}

.field input {
  height: 84rpx;
  padding: 0 22rpx;
  border-radius: var(--dm-radius-md);
  background: var(--dm-cream-100);
}

.field-error {
  margin-top: 10rpx;
  color: #D84315;
  font-size: 22rpx;
  font-weight: 500;
}

.upload-section {
  margin-top: 26rpx;
}

.upload-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18rpx;
}

.upload-box {
  position: relative;
  aspect-ratio: 5 / 3;
  border: 2rpx dashed #F0BC4A;
  border-radius: var(--dm-radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #7A5610;
  background: linear-gradient(135deg, #FBF2DC, #F6E5BC);
  font-size: 24rpx;
  font-weight: 700;
  overflow: hidden;
}

.upload-box.filled {
  border-style: solid;
  background: var(--dm-cream-100);
}

.id-preview {
  width: 100%;
  height: 100%;
}

.upload-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFFFFF;
  background: rgba(31, 9, 0, 0.58);
  font-size: 24rpx;
}

.upload-icon {
  font-size: 38rpx;
  margin-bottom: 8rpx;
}

.upload-sub {
  margin-top: 4rpx;
  color: #A0846A;
  font-size: 20rpx;
  font-weight: 400;
}

.notice {
  margin-top: 24rpx;
  padding: 22rpx;
  border-radius: var(--dm-radius-md);
  background: #E8F4FC;
  color: #1F4B9A;
  font-size: 23rpx;
  line-height: 1.6;
}

.notice text {
  display: block;
}

.notice text:first-child {
  font-weight: 800;
  margin-bottom: 6rpx;
}

.agreement {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin-top: 22rpx;
  color: var(--dm-text-2);
  font-size: 23rpx;
}

.submit {
  width: 100%;
  height: 92rpx;
  line-height: 92rpx;
  margin-top: 28rpx;
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
