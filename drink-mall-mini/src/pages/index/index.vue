<template>
  <view class="page home">
    <view class="top"><text class="hello">酒水分销商城</text><view class="search" @click="go('/pages/product/list')">搜索商品</view></view>
    <swiper class="banner" indicator-dots autoplay circular><swiper-item v-for="b in banners" :key="b.id || b.imageUrl"><image v-if="b.imageUrl" :src="b.imageUrl" mode="aspectFill"/><view v-else class="banner-fallback">精选酒水专区</view></swiper-item></swiper>
    <view class="notice" @click="go('/pages/announcement/list/index')"><text>公告</text><text>{{ noticeTitle }}</text></view>
    <view class="zones"><view v-for="z in zones" :key="z.title" class="zone" @click="go(z.url)"><uni-icons :type="z.icon" size="26" color="#b97700"/><text>{{z.title}}</text></view></view>
    <view class="card"><view class="row"><text class="title">推荐商品</text><text class="muted" @click="go('/pages/product/list')">更多</text></view><view class="grid"><view v-for="p in products" :key="p.id" class="product" @click="go('/pages/product/detail/index?id='+p.id)"><image v-if="p.mainImage" :src="p.mainImage" mode="aspectFill"/><view v-else class="ph"></view><text>{{p.name}}</text><text class="price">¥{{p.price}}</text></view></view></view>
    <button open-type="contact" class="service">客服</button>
  </view>
</template>
<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onPullDownRefresh, onShareAppMessage, onShow } from '@dcloudio/uni-app'
import { configApi } from '@/services/config'
import { productApi } from '@/services/product'
import { listOf } from '@/utils/format'
import { useUserStore } from '@/store/user'
const store = useUserStore()
const banners = ref<any[]>([{ id: 0 }])
const notices = ref<any[]>([])
const products = ref<any[]>([])
const zones = [{ title: '主产品专区', icon: 'medal', url: '/pages/product/list?zoneType=main' }, { title: '招商专区', icon: 'briefcase', url: '/pages/zone/investment/index' }, { title: '零售专区', icon: 'shop', url: '/pages/product/list?zoneType=retail' }, { title: '礼包专区', icon: 'gift', url: '/pages/zone/gift/index' }]
const noticeTitle = computed(() => notices.value[0]?.title || '查看平台公告')
async function load() { const [bs, ns, ps] = await Promise.allSettled([configApi.banners(), configApi.announcements(), productApi.list({ page: 1, size: 4 })]); if (bs.status === 'fulfilled' && bs.value.data?.length) banners.value = bs.value.data.slice(0, 5); if (ns.status === 'fulfilled') notices.value = ns.value.data || []; if (ps.status === 'fulfilled') products.value = listOf(ps.value.data).slice(0, 4) }
function go(url: string) { uni.navigateTo({ url }) }
onLoad((o: any) => { if (o.inviteCode || o.scene) store.setReferral(o.inviteCode || o.scene) })
onShow(load)
onPullDownRefresh(async () => { await load(); uni.stopPullDownRefresh() })
onShareAppMessage(() => ({ title: '酒水分销商城', path: '/pages/index/index?inviteCode=' + (store.userInfo?.inviteCode || '') }))
</script>
<style scoped lang="scss">.page{min-height:100vh;background:#f6f1e8;color:#24170c;padding-bottom:32rpx}.top{background:#2b1207;padding:100rpx 28rpx 26rpx;color:#fff}.hello{font-size:40rpx;font-weight:900;color:#f0bf55}.search{margin-top:22rpx;height:72rpx;line-height:72rpx;background:#fff;border-radius:36rpx;color:#8d8175;padding-left:30rpx}.banner{height:300rpx;margin:20rpx}.banner image,.banner-fallback{width:100%;height:100%;border-radius:18rpx}.banner-fallback{display:flex;align-items:center;justify-content:center;background:linear-gradient(135deg,#4b1708,#d6a240);color:#fff;font-size:40rpx;font-weight:900}.notice{margin:0 20rpx 20rpx;background:#fff8e8;border-radius:14rpx;padding:22rpx;display:flex;gap:20rpx;color:#795722}.zones{display:grid;grid-template-columns:repeat(4,1fr);gap:14rpx;margin:20rpx}.zone{height:138rpx;background:#fff;border-radius:16rpx;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:12rpx;font-size:24rpx}.card{background:#fff;margin:20rpx;padding:28rpx;border-radius:16rpx}.row{display:flex;justify-content:space-between;align-items:center}.title{font-size:34rpx;font-weight:900}.muted{color:#8d8175;font-size:24rpx}.grid{display:grid;grid-template-columns:repeat(2,1fr);gap:18rpx}.product image,.ph{width:100%;height:180rpx;border-radius:12rpx;background:#ead7b8}.product text{display:block;margin-top:10rpx;font-size:25rpx}.price{color:#b97700;font-weight:900}.service{position:fixed;right:22rpx;bottom:160rpx;width:92rpx;height:92rpx;border-radius:50%;background:#b97700;color:#fff;font-size:24rpx;border:0}button:after{border:0}</style>
