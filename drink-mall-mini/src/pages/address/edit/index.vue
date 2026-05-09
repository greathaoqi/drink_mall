<template>
  <view class="address-edit">
    <view class="form-card">
      <view class="form-item">
        <text class="label">收货人</text>
        <input v-model="form.receiverName" placeholder="请输入姓名" />
      </view>
      <view class="form-item">
        <text class="label">手机号</text>
        <input v-model="form.receiverPhone" placeholder="请输入手机号" type="number" />
      </view>
      <view class="form-item">
        <text class="label">所在地区</text>
        <picker mode="region" @change="onRegionChange" :value="region">
          <view class="picker">{{ regionText || '请选择省市区' }}</view>
        </picker>
      </view>
      <view class="form-item">
        <text class="label">详细地址</text>
        <textarea v-model="form.detail" placeholder="请输入详细地址" />
      </view>
      <view class="form-item switch">
        <text class="label">设为默认</text>
        <switch :checked="form.isDefault" @change="form.isDefault = $event.detail.value" />
      </view>
    </view>

    <button class="save-btn" @click="handleSave">保存</button>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import request from '@/utils/request'

const form = ref({ receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '', isDefault: false })
const region = ref<string[]>([])
const isEdit = ref(false)
const addressId = ref(0)

const regionText = computed(() => region.value.join(' '))

const onRegionChange = (e: any) => {
  region.value = e.detail.value
  form.value.province = e.detail.value[0]
  form.value.city = e.detail.value[1]
  form.value.district = e.detail.value[2]
}

const loadAddress = async () => {
  const res = await request.get(`/api/v1/address/${addressId.value}`)
  Object.assign(form.value, res.data)
  region.value = [form.value.province, form.value.city, form.value.district]
}

const handleSave = async () => {
  if (!form.value.receiverName || !form.value.receiverPhone || !form.value.detail) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  if (isEdit.value) {
    await request.put(`/api/v1/address/${addressId.value}`, form.value)
  } else {
    await request.post('/api/v1/address', form.value)
  }
  uni.showToast({ title: '保存成功', icon: 'success' })
  setTimeout(() => uni.navigateBack(), 1000)
}

onLoad((options: any) => {
  if (options.id) {
    isEdit.value = true
    addressId.value = Number(options.id)
    loadAddress()
  }
})
</script>

<style scoped>
.address-edit { background: #f5f5f5; min-height: 100vh; padding: 20rpx; }
.form-card { background: #fff; border-radius: 12rpx; padding: 0 30rpx; }
.form-item { display: flex; align-items: center; padding: 30rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.label { width: 160rpx; font-size: 28rpx; color: #333; }
input, textarea { flex: 1; font-size: 28rpx; }
textarea { height: 120rpx; }
.picker { flex: 1; font-size: 28rpx; color: #333; }
.form-item.switch { justify-content: space-between; }
.save-btn { margin: 40rpx; background: #e93b3d; color: #fff; border-radius: 40rpx; font-size: 30rpx; }
</style>