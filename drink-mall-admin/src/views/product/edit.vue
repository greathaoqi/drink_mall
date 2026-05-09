<template>
  <div>
    <el-card>
      <template #header>{{ isEdit ? '编辑商品' : '添加商品' }}</template>
      <el-form :model="form" label-width="100px" style="max-width: 600px">
        <el-form-item label="商品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="专区">
          <el-select v-model="form.zoneType" placeholder="选择专区">
            <el-option label="主产品专区" value="main" />
            <el-option label="零售专区" value="retail" />
            <el-option label="礼包专区" value="gift" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="积分价格" v-if="form.zoneType === 'gift'"><el-input-number v-model="form.pointsPrice" :min="0" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="主图"><el-input v-model="form.mainImage" placeholder="图片URL" /></el-form-item>
        <el-form-item label="轮播图"><el-input v-model="form.images" type="textarea" placeholder="多张图片URL，逗号分隔" /></el-form-item>
        <el-form-item label="商品详情"><el-input v-model="form.detail" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSubmit">保存</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const categories = ref<any[]>([])
const form = ref({
  name: '', categoryId: null as number | null, zoneType: 'main', price: 0, pointsPrice: 0,
  stock: 0, mainImage: '', images: '', detail: '', sortOrder: 0, status: 1
})

const loadData = async () => {
  if (isEdit.value) {
    const res = await request.get(`/api/v1/admin/product/${route.params.id}`)
    Object.assign(form.value, res.data)
  }
}

const loadCategories = async () => {
  const res = await request.get('/api/v1/admin/product/categories')
  categories.value = res.data || []
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await request.put(`/api/v1/admin/product/${route.params.id}`, form.value)
  } else {
    await request.post('/api/v1/admin/product', form.value)
  }
  ElMessage.success('保存成功')
  router.push('/product')
}

onMounted(() => { loadData(); loadCategories() })
</script>