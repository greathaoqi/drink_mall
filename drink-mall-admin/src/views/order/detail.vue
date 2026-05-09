<template>
  <div>
    <el-card v-loading="loading">
      <template #header>订单详情</template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag>{{ order.status }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="商品金额">¥{{ order.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ order.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ order.paymentTime }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ order.shipTime }}</el-descriptions-item>
      </el-descriptions>
      <h4 style="margin-top: 20px">商品列表</h4>
      <el-table :data="order.items" style="margin-top: 10px">
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="price" label="单价"><template #default="{ row }">¥{{ row.price }}</template></el-table-column>
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="totalAmount" label="小计"><template #default="{ row }">¥{{ row.totalAmount }}</template></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'

const route = useRoute()
const loading = ref(false)
const order = ref<any>({ items: [] })

const loadData = async () => {
  loading.value = true
  const res = await request.get(`/api/v1/admin/order/${route.params.id}`)
  order.value = res.data || {}
  loading.value = false
}

onMounted(loadData)
</script>