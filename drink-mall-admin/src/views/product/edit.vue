<template>
  <el-card v-loading="loading">
    <template #header>{{ isEdit ? '编辑商品' : '新增商品' }}</template>
    <el-form :model="form" label-width="130px" style="max-width: 820px">
      <el-form-item label="商品名称" required><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="商品副标题"><el-input v-model="form.subtitle" /></el-form-item>
      <el-form-item label="分类" required>
        <el-select v-model="form.categoryId" placeholder="请选择分类">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="所属专区" required>
        <el-select v-model="form.zoneType">
          <el-option label="主产品专区" value="main" />
          <el-option label="招商专区" value="investment" />
          <el-option label="零售专区" value="retail" />
          <el-option label="礼包专区" value="gift" />
        </el-select>
      </el-form-item>
      <el-form-item label="招商礼包等级" v-if="form.zoneType === 'investment'">
        <el-input v-model="form.investmentLevelCode" placeholder="如业务未确认可留空" />
      </el-form-item>
      <el-form-item label="销售价" required><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
      <el-form-item label="划线价"><el-input-number v-model="form.originalPrice" :min="0" :precision="2" /></el-form-item>
      <el-form-item label="库存" required><el-input-number v-model="form.stock" :min="0" /></el-form-item>
      <el-form-item label="销量"><el-input-number v-model="form.sales" :min="0" /></el-form-item>
      <el-form-item label="赠送积分"><el-input-number v-model="form.giftPoints" :min="0" /></el-form-item>
      <el-form-item label="礼包积分兑换" v-if="form.zoneType === 'gift'"><el-input-number v-model="form.giftPointsPrice" :min="0" /></el-form-item>
      <el-form-item label="支持支付方式">
        <el-checkbox-group v-model="paymentMethods">
          <el-checkbox label="balance">余额</el-checkbox>
          <el-checkbox label="wechat">微信</el-checkbox>
          <el-checkbox label="points">积分</el-checkbox>
          <el-checkbox label="wine_bean">酒豆</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="酒豆支付"><el-switch v-model="form.wineBeanPayable" />
      </el-form-item>
      <el-form-item label="主图URL"><el-input v-model="form.mainImage" /></el-form-item>
      <el-form-item label="详情图URL"><el-input v-model="form.images" type="textarea" placeholder="多张图片用英文逗号分隔" /></el-form-item>
      <el-form-item label="商品详情"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
      <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">上架</el-radio>
          <el-radio :value="0">下架</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const loading = ref(false)
const submitting = ref(false)
const categories = ref<any[]>([])
const paymentMethods = ref<string[]>([])
const form = ref({
  name: '',
  subtitle: '',
  categoryId: null as number | null,
  zoneType: 'main',
  price: 0,
  originalPrice: 0,
  stock: 0,
  sales: 0,
  mainImage: '',
  images: '',
  description: '',
  sortOrder: 0,
  status: 1,
  giftPoints: 0,
  giftPointsPrice: 0,
  investmentLevelCode: '',
  wineBeanPayable: false,
  allowedPaymentMethods: ''
})

const loadData = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const res = await request.get(`/api/v1/admin/product/${route.params.id}`)
    Object.assign(form.value, res.data)
    paymentMethods.value = String(form.value.allowedPaymentMethods || '').split(',').filter(Boolean)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  const res = await request.get('/api/v1/admin/product/categories')
  categories.value = res.data || []
}

const handleSubmit = async () => {
  if (!form.value.name || !form.value.categoryId) {
    ElMessage.warning('商品名称和分类必填')
    return
  }
  submitting.value = true
  try {
    const payload = { ...form.value, allowedPaymentMethods: paymentMethods.value.join(',') }
    if (isEdit.value) {
      await request.put(`/api/v1/admin/product/${route.params.id}`, payload)
    } else {
      await request.post('/api/v1/admin/product', payload)
    }
    ElMessage.success('商品已保存')
    router.push('/product')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>
