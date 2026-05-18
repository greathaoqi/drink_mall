<template>
  <view class="dm-page edit-page">
    <view class="form-card">
      <view class="form-row">
        <text>收货人</text>
        <input v-model="form.name" placeholder="请输入姓名" />
      </view>
      <view class="form-row">
        <text>手机号</text>
        <input v-model="form.phone" placeholder="请输入手机号" type="number" />
      </view>
      <picker mode="region" @change="regionChange">
        <view class="form-row">
          <text>所在地区</text>
          <view class="pick">{{ regionText || '请选择省市区' }}</view>
          <uni-icons type="right" size="15" color="#C9BFA9" />
        </view>
      </picker>
      <view class="form-row area-row">
        <text>详细地址</text>
        <textarea v-model="form.detail" placeholder="街道、门牌号等" />
      </view>
      <view class="form-row">
        <text>门牌号</text>
        <input v-model="form.houseNo" placeholder="选填" />
      </view>
    </view>

    <view class="form-card">
      <view class="form-row">
        <text>设为默认地址</text>
        <switch :checked="form.isDefault" color="#D38A00" @change="form.isDefault = $event.detail.value" />
      </view>
    </view>

    <view class="label-box">
      <text class="label-title">地址标签（选填）</text>
      <view class="labels">
        <text v-for="tag in tags" :key="tag" :class="{ active: form.tag === tag }" @click="form.tag = tag">{{ tag }}</text>
      </view>
    </view>

    <button class="save primary" @click="save">保存</button>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { addressApi } from '@/services/address'
import { requirePageLogin } from '@/utils/auth'

const id = ref('')
const tags = ['家', '公司', '父母', '其他']
const form = ref<any>({ name: '', phone: '', province: '', city: '', district: '', detail: '', houseNo: '', tag: '', isDefault: false })
const regionText = computed(() => [form.value.province, form.value.city, form.value.district].filter(Boolean).join(' '))

function regionChange(e: any) {
  const v = e.detail.value
  form.value.province = v[0]
  form.value.city = v[1]
  form.value.district = v[2]
}

async function save() {
  if (!requirePageLogin()) return
  if (!form.value.name || !form.value.phone || !form.value.province || !form.value.detail) {
    uni.showToast({ title: '请填写完整地址字段', icon: 'none' })
    return
  }
  if (id.value) await addressApi.update(id.value, form.value)
  else await addressApi.create(form.value)
  uni.showToast({ title: '保存成功', icon: 'success' })
  setTimeout(() => uni.navigateBack(), 300)
}

onLoad(async (o: any) => {
  if (!requirePageLogin()) return
  id.value = o.id || ''
  if (id.value) form.value = { ...form.value, ...((await addressApi.detail(id.value)).data || {}) }
})
</script>

<style scoped lang="scss">
.edit-page { padding: 20rpx 0 calc(130rpx + env(safe-area-inset-bottom)); }
.form-card { background: #fff; margin: 0 0 20rpx; padding: 0 28rpx; }
.form-row { min-height: 104rpx; display: flex; align-items: center; gap: 20rpx; border-bottom: 1rpx solid var(--dm-line-soft); font-size: 27rpx; color: var(--dm-text-1); }
.form-row:last-child { border-bottom: 0; }
.form-row > text { width: 150rpx; flex: 0 0 150rpx; color: var(--dm-text-2); }
input, .pick { flex: 1; min-width: 0; color: var(--dm-text-1); font-size: 27rpx; }
.area-row { align-items: flex-start; padding-top: 28rpx; }
.area-row textarea { flex: 1; min-height: 120rpx; font-size: 27rpx; line-height: 1.5; }
.label-box { padding: 8rpx 28rpx 0; }
.label-title { display: block; color: var(--dm-text-3); font-size: 24rpx; margin-bottom: 14rpx; }
.labels { display: flex; gap: 14rpx; }
.labels text { padding: 12rpx 30rpx; border-radius: 12rpx; background: #fff; border: 1rpx solid var(--dm-line); color: var(--dm-text-2); font-size: 25rpx; }
.labels text.active { background: var(--dm-gold-50); border-color: var(--dm-gold-300); color: var(--dm-gold-600); }
.save { position: fixed; left: 24rpx; right: 24rpx; bottom: calc(28rpx + env(safe-area-inset-bottom)); height: 88rpx; line-height: 88rpx; border: 0; border-radius: 999rpx; }
.primary { background: var(--dm-grad-gold); color: #3A1A00; font-weight: 800; }
button:after { border: 0; }
</style>
