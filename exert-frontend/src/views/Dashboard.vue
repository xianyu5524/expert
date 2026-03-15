<template>
  <div class="dashboard-container">
    <h2>首页</h2>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <StatsCard :number="stats.expertCount" label="专家总数" />
      </el-col>
      <el-col :xs="12" :sm="6">
        <StatsCard :number="stats.projectCount" label="项目总数" />
      </el-col>
      <el-col :xs="12" :sm="6">
        <StatsCard :number="stats.pendingAppCount" label="待审核申请" />
      </el-col>
      <el-col :xs="12" :sm="6">
        <StatsCard :number="stats.birthdayCount" label="近期生日专家" />
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
              <el-button type="primary" text>查看全部</el-button>
            </div>
          </template>
          <el-list>
            <el-list-item>
              <span>专家信息修改申请待审核</span>
              <el-tag type="warning">3</el-tag>
            </el-list-item>
            <el-list-item>
              <span>近期生日专家提醒</span>
              <el-tag type="info">5</el-tag>
            </el-list-item>
            <el-list-item>
              <span>项目评审邀请待处理</span>
              <el-tag>2</el-tag>
            </el-list-item>
          </el-list>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12">
        <el-card >
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" class="action-btn" @click="$router.push('/experts')">专家管理</el-button>
            <el-button type="primary" class="action-btn" @click="$router.push('/projects')">项目管理</el-button>
            <el-button type="primary" class="action-btn" @click="$router.push('/recommendations')">推荐中心</el-button>
            <el-button type="primary" class="action-btn" @click="$router.push('/applications')">审核中心</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useDashboardStore } from '@/store'
import StatsCard from '@/components/Common/StatsCard.vue'

const dashboardStore = useDashboardStore()
const stats = ref(dashboardStore.stats)

const fetchDashboardStats = async () => {
  // 模拟API调用
  const mockStats = {
    expertCount: 125,
    projectCount: 42,
    pendingAppCount: 8,
    birthdayCount: 5
  }
  dashboardStore.setStats(mockStats)
  stats.value = mockStats
}

onMounted(() => {
  fetchDashboardStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stats-row {
  margin-top: 20px;
}

.content-row {
  margin-top: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 12px; /* 统一间距 */
}

.action-btn {
  min-width: 120px;
  width: auto;
  margin: 0 auto; /* 水平居中 */
  display: block; /* 块级显示 */
}

</style>