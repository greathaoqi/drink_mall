<template>
  <view class="mall-page list-page">
    <view class="sticky-head">
      <view class="search-row">
        <uni-icons type="left" size="22" color="#1A1006" @click="backOrHome" />
        <MallSearchBar
          v-model="keyword"
          editable
          light
          compact
          placeholder="搜索酒水商品..."
          @search="search"
        />
      </view>

      <view v-if="showSearchLanding" class="search-panel">
        <view class="panel-head">
          <text>历史搜索</text>
          <uni-icons type="trash" size="16" color="#9B8E7C" @click="history = []" />
        </view>
        <view class="chip-wrap">
          <text v-for="item in history" :key="item" class="chip" @click="pickKeyword(item)">{{ item }}</text>
        </view>

        <view class="panel-title">热门搜索</view>
        <view class="hot-list">
          <view v-for="(item, index) in hotKeywords" :key="item.text" class="hot-row" @click="pickKeyword(item.text)">
            <text class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</text>
            <text class="hot-text">{{ item.text }}</text>
            <text class="heat">{{ item.heat }} 热度</text>
          </view>
        </view>

        <view class="panel-title">发现好酒</view>
        <view class="category-grid">
          <text v-for="item in aromaTypes" :key="item" @click="pickKeyword(item)">{{ item }}</text>
        </view>
      </view>

      <template v-else>
        <scroll-view scroll-x class="zone-scroll" show-scrollbar="false">
          <view class="zone-tabs">
            <view
              v-for="z in zones"
              :key="z.value"
              class="zone-tab mall-press"
              :class="{ on: zoneType === z.value }"
              @click="setZone(z.value)"
            >
              {{ z.label }}
            </view>
          </view>
        </scroll-view>

        <view class="sort-row">
          <view
            v-for="item in sortOptions"
            :key="item.value"
            class="sort-item"
            :class="{ on: sortBy === item.value }"
            @click="changeSort(item.value)"
          >
            {{ item.label }}
          </view>
          <view class="sort-item filter" @click="filterVisible = true">
            <uni-icons type="settings" size="14" color="#5E5142" />筛选
          </view>
        </view>

        <view v-if="mode === 'category'" class="category-mode">
          <scroll-view scroll-y class="side-categories">
            <view
              v-for="c in categoryTabs"
              :key="c.id || c.name"
              class="side-tab"
              :class="{ on: String(categoryId) === String(c.id || '') }"
              @click="setCategory(c.id)"
            >
              {{ c.name }}
            </view>
          </scroll-view>
          <view class="category-content">
            <text class="category-title">{{ activeCategoryName }}</text>
            <view class="sub-grid">
              <view v-for="c in childCategories" :key="c.id || c.name" class="sub-item" @click="setCategory(c.id)">
                <image src="/static/images/product-wine.png" mode="aspectFill" />
                <text>{{ c.name }}</text>
              </view>
            </view>
          </view>
        </view>
      </template>
    </view>

    <view v-if="!showSearchLanding" class="content">
      <PageState :loading="loading && products.length === 0" :empty="products.length === 0" empty-text="没有找到相关商品">
        <view class="empty-extra" v-if="products.length === 0 && !loading">
          <text>换个关键词试试，也可以浏览下方为你推荐的好酒</text>
          <button class="browse-main" @click="setZone('main')">浏览主产品专区</button>
        </view>
        <view class="product-grid">
          <ProductCard v-for="p in products" :key="p.id" :product="p" @tap="goDetail" />
        </view>
        <uni-load-more :status="loadMoreStatus" :content-text="loadMoreText" />
      </PageState>
    </view>

    <view v-if="filterVisible" class="drawer-mask" @click="filterVisible = false"></view>
    <view v-if="filterVisible" class="filter-drawer">
      <view class="drawer-status"></view>
      <text class="drawer-title">筛选条件</text>
      <view class="drawer-body">
        <text class="filter-title">专区</text>
        <view class="chip-wrap">
          <text
            v-for="z in zones.filter((item) => item.value)"
            :key="z.value"
            class="filter-chip"
            :class="{ on: zoneType === z.value }"
            @click="zoneType = z.value"
          >{{ z.label }}</text>
        </view>

        <text class="filter-title">香型</text>
        <view class="chip-wrap">
          <text v-for="item in aromaTypes" :key="item" class="filter-chip" @click="keyword = item">{{ item }}</text>
        </view>

        <text class="filter-title">价格区间</text>
        <view class="price-row">
          <input v-model="minPrice" type="digit" placeholder="￥ 最低价" />
          <text>-</text>
          <input v-model="maxPrice" type="digit" placeholder="￥ 最高价" />
        </view>
      </view>
      <view class="drawer-actions">
        <button class="reset" @click="resetFilter">重置</button>
        <button class="confirm" @click="applyFilter">确认</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad, onPullDownRefresh, onReachBottom, onShow } from '@dcloudio/uni-app'
import MallSearchBar from '@/components/MallSearchBar/MallSearchBar.vue'
import ProductCard from '@/components/ProductCard/ProductCard.vue'
import PageState from '@/components/PageState/PageState.vue'
import { productApi } from '@/services/product'
import { listOf } from '@/utils/format'
import { buildProductDetailUrl } from '@/utils/product'

const products = ref<any[]>([])
const categories = ref<any[]>([])
const loading = ref(false)
const finished = ref(false)
const keyword = ref('')
const zoneType = ref('')
const sortBy = ref('')
const minPrice = ref('')
const maxPrice = ref('')
const categoryId = ref('')
const mode = ref('')
const hasSearched = ref(false)
const filterVisible = ref(false)
const page = ref(1)
const pageSize = 10
const history = ref(['茅台飞天', '五粮液', '红酒礼盒', '威士忌', '酱香型', '中秋礼包'])

const sortOptions = [
  { label: '综合', value: '' },
  { label: '销量', value: 'sales' },
  { label: '价格↑', value: 'price_asc' },
  { label: '价格↓', value: 'price_desc' }
]
const zones = [
  { label: '全部', value: '' },
  { label: '主产品专区', value: 'main' },
  { label: '招商专区', value: 'investment' },
  { label: '零售专区', value: 'retail' },
  { label: '礼包专区', value: 'gift' }
]
const hotKeywords = [
  { text: '茅台飞天 53度', heat: '6.8w' },
  { text: '五粮液普五', heat: '4.2w' },
  { text: '中秋商务礼盒', heat: '3.5w' },
  { text: '国窖1573', heat: '2.1w' },
  { text: '青花汾酒', heat: '1.5w' }
]
const aromaTypes = ['酱香型', '浓香型', '清香型', '红葡萄酒', '威士忌', '黄酒', '清酒', '香槟']
const loadMoreText = { contentdown: '上拉加载更多', contentrefresh: '加载中', contentnomore: '没有更多商品' }

const showSearchLanding = computed(() => mode.value === 'search' && !hasSearched.value && !keyword.value)
const loadMoreStatus = computed(() => loading.value ? 'loading' : (finished.value ? 'noMore' : 'more'))
const categoryTabs = computed(() => categories.value.length ? categories.value : aromaTypes.map((name, index) => ({ id: 'local-' + index, name })))
const activeCategoryName = computed(() => categoryTabs.value.find((c) => String(c.id) === String(categoryId.value))?.name || '酱香型白酒')
const childCategories = computed(() => categories.value.filter((c) => String(c.parentId || '') === String(categoryId.value)).slice(0, 6))

async function load(reset = false) {
  if (showSearchLanding.value || loading.value) return
  if (reset) {
    page.value = 1
    finished.value = false
  }
  if (finished.value && !reset) return
  loading.value = true
  try {
    const res = await productApi.list({
      keyword: keyword.value,
      zoneType: zoneType.value,
      categoryId: categoryId.value,
      sortBy: sortBy.value,
      minPrice: minPrice.value,
      maxPrice: maxPrice.value,
      page: page.value,
      size: pageSize
    })
    const next = listOf(res.data)
    products.value = reset ? next : products.value.concat(next)
    finished.value = next.length < pageSize
    if (!finished.value) page.value += 1
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    categories.value = listOf((await productApi.categories()).data)
  } catch {
    categories.value = []
  }
}

function search(value = keyword.value) {
  keyword.value = value
  hasSearched.value = true
  if (value && !history.value.includes(value)) history.value.unshift(value)
  load(true)
}

function pickKeyword(value: string) {
  keyword.value = value
  search(value)
}

function changeSort(value: string) {
  sortBy.value = value
  load(true)
}

function setZone(value: string) {
  zoneType.value = value
  hasSearched.value = true
  load(true)
}

function setCategory(value: string | number) {
  categoryId.value = String(value || '')
  hasSearched.value = true
  load(true)
}

function resetFilter() {
  zoneType.value = ''
  minPrice.value = ''
  maxPrice.value = ''
  keyword.value = ''
}

function applyFilter() {
  filterVisible.value = false
  hasSearched.value = true
  load(true)
}

function goDetail(product: any) {
  const url = buildProductDetailUrl(product)
  if (!url) {
    uni.showToast({ title: '商品ID缺失', icon: 'none' })
    return
  }
  uni.navigateTo({ url })
}

function backOrHome() {
  const pages = getCurrentPages()
  if (pages.length > 1) uni.navigateBack()
  else uni.switchTab({ url: '/pages/index/index' })
}

onLoad((options: any) => {
  zoneType.value = options.zoneType || ''
  keyword.value = options.keyword || ''
  minPrice.value = options.minPrice || ''
  maxPrice.value = options.maxPrice || ''
  categoryId.value = options.categoryId || ''
  mode.value = options.mode || (options.categoryId ? 'category' : '')
  hasSearched.value = Boolean(keyword.value || zoneType.value || categoryId.value || mode.value !== 'search')
  loadCategories()
})
onShow(() => {
  if (!showSearchLanding.value && !products.value.length) load(true)
})
onReachBottom(() => load())
onPullDownRefresh(async () => {
  await load(true)
  uni.stopPullDownRefresh()
})
</script>

<style scoped lang="scss">
.list-page {
  padding-bottom: 30rpx;
}

.sticky-head {
  position: sticky;
  top: 0;
  z-index: 10;
  padding: 18rpx var(--dm-page-x) 0;
  background: #FFFFFF;
  box-shadow: 0 6rpx 18rpx rgba(40, 20, 0, .04);
}

.search-row {
  display: flex;
  align-items: center;
  gap: 18rpx;
}

.search-panel {
  min-height: calc(100vh - 110rpx);
  padding: 32rpx 0 48rpx;
  background: #FFFFFF;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 700;
}

.panel-title {
  margin-top: 44rpx;
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 700;
}

.chip-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 22rpx;
}

.chip,
.filter-chip {
  height: 56rpx;
  line-height: 56rpx;
  padding: 0 28rpx;
  border-radius: var(--dm-radius-pill);
  background: var(--dm-bg-app);
  color: var(--dm-text-2);
  font-size: 25rpx;
}

.hot-list {
  margin-top: 18rpx;
}

.hot-row {
  display: flex;
  align-items: center;
  gap: 22rpx;
  min-height: 76rpx;
}

.rank {
  width: 44rpx;
  height: 44rpx;
  line-height: 44rpx;
  border-radius: var(--dm-radius-xs);
  background: var(--dm-cream-100);
  color: var(--dm-text-3);
  text-align: center;
  font-size: 24rpx;
  font-weight: 800;
}

.rank.top {
  background: var(--dm-grad-gold);
  color: #3A1A00;
}

.hot-text {
  flex: 1;
  color: var(--dm-text-1);
  font-size: 28rpx;
}

.heat {
  color: var(--dm-text-3);
  font-size: 24rpx;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  margin-top: 22rpx;
}

.category-grid text {
  height: 72rpx;
  line-height: 72rpx;
  border-radius: var(--dm-radius-sm);
  background: var(--dm-cream-50);
  color: var(--dm-text-2);
  text-align: center;
  font-size: 24rpx;
}

.zone-scroll {
  width: 100%;
  margin-top: 18rpx;
  white-space: nowrap;
}

.zone-tabs {
  display: inline-flex;
  gap: 14rpx;
  padding-bottom: 16rpx;
}

.zone-tab {
  height: 58rpx;
  line-height: 58rpx;
  padding: 0 24rpx;
  border-radius: var(--dm-radius-pill);
  background: var(--dm-bg-app);
  color: var(--dm-text-2);
  font-size: 24rpx;
  font-weight: 700;
}

.zone-tab.on {
  background: var(--dm-grad-gold);
  color: #3A1A00;
}

.sort-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  border-top: 1rpx solid var(--dm-line-soft);
}

.sort-item {
  height: 72rpx;
  line-height: 72rpx;
  text-align: center;
  color: var(--dm-text-2);
  font-size: 25rpx;
}

.sort-item.on {
  color: var(--dm-gold-500);
  font-weight: 800;
}

.filter {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
}

.category-mode {
  display: flex;
  margin: 0 calc(var(--dm-page-x) * -1);
  border-top: 1rpx solid var(--dm-line-soft);
}

.side-categories {
  width: 180rpx;
  max-height: 430rpx;
  background: var(--dm-bg-app);
}

.side-tab {
  min-height: 96rpx;
  padding: 0 22rpx;
  display: flex;
  align-items: center;
  color: var(--dm-text-2);
  font-size: 25rpx;
}

.side-tab.on {
  background: #FFFFFF;
  color: var(--dm-gold-500);
  font-weight: 800;
}

.category-content {
  flex: 1;
  min-width: 0;
  padding: 24rpx 28rpx;
}

.category-title {
  display: block;
  margin-bottom: 20rpx;
  color: var(--dm-text-1);
  font-size: 28rpx;
  font-weight: 800;
}

.sub-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.sub-item {
  text-align: center;
  font-size: 22rpx;
  color: var(--dm-text-2);
}

.sub-item image {
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: var(--dm-radius-sm);
  background: var(--dm-cream-100);
}

.content {
  padding: 24rpx var(--dm-page-x);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20rpx;
}

.empty-extra {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 24rpx;
  color: var(--dm-text-3);
  font-size: 26rpx;
  text-align: center;
}

.browse-main {
  height: 76rpx;
  line-height: 76rpx;
  padding: 0 30rpx;
  border-radius: var(--dm-radius-pill);
  background: var(--dm-gold-50);
  color: var(--dm-gold-500);
  font-size: 26rpx;
  font-weight: 700;
}

.drawer-mask {
  position: fixed;
  inset: 0;
  z-index: 50;
  background: rgba(0, 0, 0, .42);
}

.filter-drawer {
  position: fixed;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 51;
  width: 640rpx;
  max-width: 86vw;
  background: #FFFFFF;
  padding: 0 36rpx 40rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.drawer-status {
  height: 74rpx;
}

.drawer-title {
  color: var(--dm-text-1);
  font-size: 36rpx;
  font-weight: 900;
}

.drawer-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
}

.filter-title {
  display: block;
  margin-top: 40rpx;
  color: var(--dm-text-1);
  font-size: 26rpx;
  font-weight: 800;
}

.filter-chip {
  border-radius: var(--dm-radius-sm);
}

.filter-chip.on {
  background: var(--dm-gold-50);
  color: var(--dm-gold-600);
  box-shadow: inset 0 0 0 2rpx var(--dm-gold-300);
}

.price-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-top: 20rpx;
}

.price-row input {
  flex: 1;
  min-width: 0;
  height: 68rpx;
  border-radius: var(--dm-radius-sm);
  background: var(--dm-bg-app);
  padding: 0 20rpx;
  font-size: 24rpx;
}

.price-row text {
  color: var(--dm-text-3);
}

.drawer-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 30rpx;
}

.drawer-actions button {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: var(--dm-radius-pill);
  font-size: 30rpx;
  font-weight: 800;
}

.reset {
  background: var(--dm-gold-50);
  color: var(--dm-gold-500);
}

.confirm {
  background: var(--dm-grad-gold);
  color: #3A1A00;
}
</style>
