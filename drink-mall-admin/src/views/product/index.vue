<template>
  <el-card>
    <template #header>
      <el-row justify="space-between" align="middle">
        <el-col :span="18">
          <el-input v-model="search.keyword" placeholder="搜索商品" class="w180" clearable @keyup.enter="loadData" />
          <el-select v-model="search.categoryId" placeholder="分类" clearable class="w150">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
          <el-select v-model="search.zoneType" placeholder="专区" clearable class="w150">
            <el-option label="主产品专区" value="main" />
            <el-option label="招商专区" value="investment" />
            <el-option label="零售专区" value="retail" />
            <el-option label="礼包专区" value="gift" />
          </el-select>
          <el-button type="primary" :loading="loading" @click="loadData">查询</el-button>
        </el-col>
        <el-col :span="6" class="right"><el-button type="primary" @click="$router.push('/product/add')">添加商品</el-button></el-col>
      </el-row>
    </template>
    <el-table :data="tableData" v-loading="loading" empty-text="暂无商品">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="mainImage" label="图片" width="80">
        <template #default="{ row }"><el-image :src="row.mainImage" style="width: 48px; height: 48px" fit="cover" /></template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="180" />
      <el-table-column prop="price" label="价格" width="100"><template #default="{ row }">¥{{ row.price }}</template></el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="giftPoints" label="赠送积分" width="100" />
      <el-table-column prop="allowedPaymentMethods" label="支付方式" min-width="140" />
      <el-table-column prop="zoneType" label="专区" width="110">
        <template #default="{ row }"><el-tag :type="zoneType(row.zoneType)">{{ zoneText(row.zoneType) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" v-if="can('product:write')" @click="$router.push(`/product/edit/${row.id}`)">编辑</el-button>
          <el-button link type="primary" v-if="can('product:write')" @click="openPayment(row)">支付</el-button>
          <el-button link type="warning" v-if="can('product:write')" @click="openStock(row)">库存</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" v-if="can('product:write')" @click="toggleStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="20" layout="total, prev, pager, next" :total="total" @current-change="loadData" />

    <el-dialog v-model="stockDialogVisible" title="库存调整" width="460px">
      <el-alert title="请输入正数增加库存，负数减少库存；原因会写入库存流水和操作日志。" type="warning" show-icon :closable="false" />
      <el-form :model="stockForm" label-width="96px" class="dialog-form">
        <el-form-item label="商品"><span>{{ currentProduct?.name }}</span></el-form-item>
        <el-form-item label="调整数量" required><el-input-number v-model="stockForm.quantity" :step="1" /></el-form-item>
        <el-form-item label="调整原因" required><el-input v-model="stockForm.reason" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStock">确认调整</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="paymentDialogVisible" title="商品支付方式" width="500px">
      <el-form :model="paymentForm" label-width="116px">
        <el-form-item label="商品"><span>{{ currentProduct?.name }}</span></el-form-item>
        <el-form-item label="支持支付方式" required>
          <el-checkbox-group v-model="paymentForm.methods">
            <el-checkbox label="online">微信/线上</el-checkbox>
            <el-checkbox label="balance">余额</el-checkbox>
            <el-checkbox label="offline_corporate">线下对公</el-checkbox>
            <el-checkbox label="points">积分</el-checkbox>
            <el-checkbox label="wine_bean">酒豆</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="酒豆支付"><el-switch v-model="paymentForm.wineBeanPayable" /></el-form-item>
        <el-form-item label="调整原因" required><el-input v-model="paymentForm.reason" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPayment">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { can, confirmCritical, promptReason, requireText } from '@/utils/adminAction'

const loading = ref(false)
const tableData = ref<any[]>([])
const categories = ref<any[]>([])
const page = ref(1)
const total = ref(0)
const search = ref({ keyword: '', categoryId: '', zoneType: '' })
const currentProduct = ref<any>(null)
const stockDialogVisible = ref(false)
const stockForm = ref({ quantity: 0, reason: '' })
const paymentDialogVisible = ref(false)
const paymentForm = ref({ methods: [] as string[], wineBeanPayable: false, reason: '' })

const zoneText = (zone: string) => ({ main: '主产品', investment: '招商', retail: '零售', gift: '礼包' }[zone] || zone || '-')
const zoneType = (zone: string) => ({ main: 'primary', investment: 'danger', retail: 'success', gift: 'warning' }[zone] || 'info')

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/v1/admin/product/list', { params: { ...search.value, page: page.value, size: 20 } })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  const res = await request.get('/api/v1/admin/product/categories')
  categories.value = res.data || []
}

const toggleStatus = async (row: any) => {
  const reason = await promptReason('商品状态确认', `请输入${row.status === 1 ? '下架' : '上架'}原因`)
  await confirmCritical(`确认${row.status === 1 ? '下架' : '上架'}该商品？`, '商品状态确认')
  await request.put(`/api/v1/admin/product/${row.id}/status`, null, { params: { status: row.status === 1 ? 0 : 1, reason } })
  ElMessage.success('商品状态已更新')
  loadData()
}

const openStock = (row: any) => {
  currentProduct.value = row
  stockForm.value = { quantity: 0, reason: '' }
  stockDialogVisible.value = true
}

const submitStock = async () => {
  if (!currentProduct.value || !stockForm.value.quantity) {
    ElMessage.warning('调整数量不能为 0')
    return
  }
  if (!requireText(stockForm.value.reason, '调整原因')) return
  await confirmCritical(`确认调整商品 ${currentProduct.value.name} 库存 ${stockForm.value.quantity}？`, '库存调整确认')
  await request.put(`/api/v1/admin/product/${currentProduct.value.id}/stock`, null, { params: stockForm.value })
  ElMessage.success('库存已调整')
  stockDialogVisible.value = false
  loadData()
}

const openPayment = (row: any) => {
  currentProduct.value = row
  paymentForm.value = {
    methods: String(row.allowedPaymentMethods || '').split(',').filter(Boolean),
    wineBeanPayable: Boolean(row.wineBeanPayable),
    reason: ''
  }
  paymentDialogVisible.value = true
}

const submitPayment = async () => {
  if (!currentProduct.value) return
  if (!paymentForm.value.methods.length) {
    ElMessage.warning('至少选择一种支付方式')
    return
  }
  if (!requireText(paymentForm.value.reason, '调整原因')) return
  await confirmCritical('确认修改该商品可用支付方式？', '支付方式确认')
  await request.put(`/api/v1/admin/product/${currentProduct.value.id}/payment-methods`, null, {
    params: {
      allowedPaymentMethods: paymentForm.value.methods.join(','),
      wineBeanPayable: paymentForm.value.wineBeanPayable,
      reason: paymentForm.value.reason
    }
  })
  ElMessage.success('商品支付方式已更新')
  paymentDialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.w180 { width: 180px; margin-right: 8px; }
.w150 { width: 150px; margin-right: 8px; }
.right { text-align: right; }
.dialog-form { margin-top: 16px; }
</style>
