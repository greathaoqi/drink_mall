<template>
  <view class="page">
    <view class="summary">
      <text class="eyebrow">合规展示</text>
      <text class="title">我的团队</text>
      <text class="muted">仅展示邀请人、直推、间推和团队总人数。</text>
      <view class="stats">
        <view><text class="num">{{ overview.totalCount || 0 }}</text><text>团队总人数</text></view>
        <view><text class="num">{{ directs.length }}</text><text>直推</text></view>
        <view><text class="num">{{ indirects.length }}</text><text>间推</text></view>
      </view>
    </view>

    <view class="card">
      <text class="section-title">我的邀请人</text>
      <text class="muted">{{ overview.inviterName || '暂无' }}</text>
    </view>

    <view class="card">
      <text class="section-title">直推人员</text>
      <view v-for="m in directs" :key="m.userId" class="member">
        <text>{{ m.nickname || m.userId }}</text>
        <button @click="giftDf(m)">赠送DF</button>
      </view>
      <view v-if="!directs.length" class="empty">暂无直推人员</view>
    </view>

    <view class="card">
      <text class="section-title">间推人员</text>
      <view v-for="m in indirects" :key="m.userId" class="member">
        <text>{{ m.nickname || m.userId }}</text>
      </view>
      <view v-if="!indirects.length" class="empty">暂无间推人员</view>
    </view>

    <view class="card muted">小程序端不展示三级及以上人员明细，不提供团队树状图。</view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { teamApi } from '@/services/team'
import { assetApi } from '@/services/asset'
import { listOf } from '@/utils/format'

const overview = ref<any>({})
const directs = ref<any[]>([])
const indirects = ref<any[]>([])

async function load() {
  overview.value = (await teamApi.overview()).data || {}
  directs.value = listOf((await teamApi.directs()).data)
  indirects.value = listOf((await teamApi.indirects()).data)
}
function giftDf(m: any) {
  uni.showModal({
    title: '赠送DF',
    editable: true,
    placeholderText: '请输入数量',
    success: async (r) => {
      if (r.confirm && r.content) {
        await assetApi.transferDf({ toUserId: m.userId, amount: r.content })
        uni.showToast({ title: '赠送成功', icon: 'success' })
      }
    }
  })
}
onShow(load)
</script>

<style scoped lang="scss">
.page{min-height:100vh;background:#f6f1e8;color:#24170c;padding:20rpx}
.summary{background:#2b1207;color:#fff;border-radius:18rpx;padding:32rpx 28rpx;margin-bottom:20rpx}
.eyebrow{display:block;color:#f0bf55;font-size:23rpx}
.title{display:block;font-size:42rpx;font-weight:900;margin-top:10rpx}
.muted{color:#8d8175;font-size:24rpx;line-height:1.5}
.summary .muted{display:block;color:#e8d6b8;margin-top:10rpx}
.stats{display:grid;grid-template-columns:repeat(3,1fr);gap:14rpx;margin-top:24rpx}
.stats view{background:rgba(255,255,255,.1);border-radius:12rpx;padding:18rpx 10rpx;text-align:center;font-size:22rpx;color:#e8d6b8}
.num{display:block;color:#fff;font-size:34rpx;font-weight:900;margin-bottom:6rpx}
.card{background:#fff;margin-bottom:20rpx;padding:28rpx;border-radius:16rpx;box-shadow:0 8rpx 24rpx rgba(58,32,12,.06)}
.section-title{display:block;font-size:32rpx;font-weight:900;margin-bottom:12rpx}
.member{display:flex;justify-content:space-between;align-items:center;gap:20rpx;padding:20rpx 0;border-bottom:1rpx solid #eee7db}
.member text{min-width:0;flex:1;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
.member button{width:164rpx;height:64rpx;line-height:64rpx;background:#fff4df;color:#8a5b0e;border:0;border-radius:999rpx;font-size:24rpx}
.empty{color:#8d8175;font-size:24rpx;padding:20rpx 0}
button:after{border:0}
</style>
