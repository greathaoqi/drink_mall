<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日订单" :value="stats.todayOrders" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="今日销售额" :value="stats.todaySales" prefix="¥" :precision="2" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="用户总数" :value="stats.totalUsers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="待处理售后" :value="stats.pendingAftersales" />
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card header="最近订单">
          <el-table :data="recentOrders" size="small">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="payAmount" label="金额" width="80">
              <template #default="{ row }">¥{{ row.payAmount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="recentOrders.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="待审核提现">
          <el-table :data="pendingWithdrawals" size="small">
            <el-table-column prop="userId" label="用户ID" width="80" />
            <el-table-column prop="amount" label="金额" width="80">
              <template #default="{ row }">¥{{ row.amount }}</template>
            </el-table-column>
            <el-table-column prop="createdAt" label="申请时间">
              <template #default="{ row }">{{ row.createdAt?.slice(0, 10) }}</template>
            </el-table-column>
          </el-table>
          <el-empty v-if="pendingWithdrawals.length === 0" description="暂无待审核提现" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({ todayOrders: 0, todaySales: 0, totalUsers: 0, pendingAftersales: 0 })
const recentOrders = ref<any[]>([])
const pendingWithdrawals = ref<any[]>([])

const statusLabel = (status: string) => {
  const map: Record<string, string> = { pending: '待付款', paid: '待发货', shipped: '待收货', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}

const statusTagType = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', paid: '', shipped: 'primary', completed: 'success', cancelled: 'info' }
  return map[status] || ''
}

onMounted(async () => {
  try {
    const [ordersRes, usersRes, withdrawalsRes, aftersaleRes] = await Promise.all([
      request.get('/api/v1/admin/order/list', { params: { page: 1, size: 5 } }),
      request.get('/api/v1/admin/user/list', { params: { page: 1, size: 1 } }),
      request.get('/api/v1/admin/finance/withdrawals', { params: { status: 'pending', page: 1, size: 5 } }),
      request.get('/api/v1/admin/order/aftersale/list', { params: { page: 1, size: 1 } })
    ])
    // request interceptor unwraps envelope: res is already the inner `data` field
    if (ordersRes?.records) {
      recentOrders.value = ordersRes.records
      const today = new Date().toISOString().slice(0, 10)
      stats.value.todayOrders = recentOrders.value.filter((o: any) => o.createdAt?.startsWith(today)).length
      stats.value.todaySales = recentOrders.value
        .filter((o: any) => o.createdAt?.startsWith(today) && ['paid', 'shipped', 'completed'].includes(o.status))
        .reduce((s: number, o: any) => s + Number(o.payAmount || 0), 0)
    }
    if (usersRes?.total != null) stats.value.totalUsers = usersRes.total
    if (withdrawalsRes?.records) pendingWithdrawals.value = withdrawalsRes.records
    if (aftersaleRes?.total != null) stats.value.pendingAftersales = aftersaleRes.total
  } catch (e) {
    console.error('Dashboard load error', e)
  }
})
</script>
