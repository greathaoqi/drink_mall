<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">酒水商城管理后台</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon>控制台</el-menu-item>
        <el-sub-menu index="product">
          <template #title><el-icon><Goods /></el-icon>商品管理</template>
          <el-menu-item index="/product">商品列表</el-menu-item>
          <el-menu-item index="/product/category">分类管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="order">
          <template #title><el-icon><List /></el-icon>订单管理</template>
          <el-menu-item index="/order">订单列表</el-menu-item>
          <el-menu-item index="/order/aftersale">售后管理</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/user"><el-icon><User /></el-icon>用户管理</el-menu-item>
        <el-sub-menu index="content">
          <template #title><el-icon><Picture /></el-icon>内容管理</template>
          <el-menu-item index="/content/banner">轮播图</el-menu-item>
          <el-menu-item index="/content/announcement">公告管理</el-menu-item>
          <el-menu-item index="/content/video">视频管理</el-menu-item>
          <el-menu-item index="/content/help">帮助中心</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="finance">
          <template #title><el-icon><Money /></el-icon>财务管理</template>
          <el-menu-item index="/finance/withdrawal">提现审核</el-menu-item>
          <el-menu-item index="/finance/logs">资金流水</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="system">
          <template #title><el-icon><Setting /></el-icon>系统管理</template>
          <el-menu-item index="/system/admin">管理员账号</el-menu-item>
          <el-menu-item index="/system/config">系统配置</el-menu-item>
          <el-menu-item index="/system/logs">操作日志</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-dropdown>
            <span class="user-info"><el-icon><User /></el-icon>管理员</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
const handleLogout = () => { router.push('/login') }
</script>

<style scoped>
.layout-container { height: 100vh; }
.aside { background: #304156; }
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; font-weight: bold; background: #263445; }
.header { background: #fff; border-bottom: 1px solid #dcdfe6; display: flex; align-items: center; justify-content: flex-end; padding: 0 20px; }
.user-info { cursor: pointer; display: flex; align-items: center; gap: 5px; }
.main { background: #f0f2f5; padding: 20px; }
</style>