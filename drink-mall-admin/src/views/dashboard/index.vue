<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="今日订单" :value="0" /></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="今日销售额" :value="0" prefix="¥" /></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="新增用户" :value="0" /></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><el-statistic title="待处理售后" :value="0" /></el-card></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12"><el-card header="最近订单"><el-empty description="暂无数据" /></el-card></el-col>
      <el-col :span="12"><el-card header="销售趋势"><el-empty description="暂无数据" /></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
</script>