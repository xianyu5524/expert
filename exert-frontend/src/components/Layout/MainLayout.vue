<template>
  <el-container class="common-layout">
    <el-header>
      <div class="logo">高校专家库系统</div>
      <div class="user-info">
        <span>当前用户: {{ currentUser.username }}</span>
        <el-button type="primary" text @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="currentRoute"
          class="el-menu-vertical-demo"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
          router
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/experts">
            <el-icon><User /></el-icon>
            <span>专家管理</span>
          </el-menu-item>
          <el-menu-item index="/projects">
            <el-icon><Folder /></el-icon>
            <span>项目管理</span>
          </el-menu-item>
          <el-menu-item index="/recommendations">
            <el-icon><Star /></el-icon>
            <span>推荐中心</span>
          </el-menu-item>
          <el-menu-item index="/applications">
            <el-icon><DocumentChecked /></el-icon>
            <span>审核中心</span>
          </el-menu-item>
          <el-menu-item index="/reminders">
            <el-icon><Bell /></el-icon>
            <span>提醒管理</span>
          </el-menu-item>
          
          <!-- 管理员功能 -->
          <el-sub-menu v-if="isAdmin" index="admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/admin-management">管理员管理</el-menu-item>
            <el-menu-item index="/system-config">系统配置</el-menu-item>
            <el-menu-item index="/operation-logs">操作日志</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store'
import {
  DataAnalysis,
  User,
  Folder,
  Star,
  DocumentChecked,
  Bell,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const currentRoute = computed(() => route.path)
const currentUser = computed(() => userStore.currentUser)
const isAdmin = computed(() => {
  const role = userStore.userRole
  return role === 'SUPER_ADMIN' || role === 'ADMIN'
})

const handleLogout = () => {
  userStore.clearUser()
  router.push('/login')
}
</script>

<style scoped>
.common-layout {
  height: 100vh;
}

.el-header {
  background-color: #545c64;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-aside {
  background-color: #545c64;
}

.el-main {
  padding: 20px;
  background-color: #f5f5f5;
  overflow-y: auto;
}
</style>