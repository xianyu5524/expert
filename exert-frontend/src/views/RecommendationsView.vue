<template>
  <div class="recommendations-container">
    <!-- 页面标题和操作区 -->
    <div class="page-header">
      <h2>专家推荐中心</h2>
      <div class="header-actions">
        <el-button type="primary" @click="refreshRecommendations" :loading="loading">
          <el-icon>
            <Refresh />
          </el-icon>
          刷新推荐
        </el-button>
      </div>
    </div>

    <!-- 项目选择区域 -->
    <ProjectSelector 
      v-model:selected-project="selectedProject" 
      @project-change="handleProjectChange"
      @filters-change="handleFiltersChange" 
    />

    <!-- 主要内容区域 -->
    <div class="main-content" v-loading="loading">
      <!-- 推荐专家列表 -->
      <RecommendationList 
        v-if="selectedProject && recommendations.length > 0" 
        :recommendations="recommendations"
        :selected-project="selectedProject" 
        @view-detail="handleViewDetail" 
        @invite-expert="handleInviteExpert"
        @batch-invite="handleBatchInvite" 
      />

      <!-- 空状态 -->
      <el-empty 
        v-else-if="selectedProject && !loading" 
        description="暂无推荐结果，请调整筛选条件或刷新重试" 
        :image-size="200" 
      />

      <!-- 初始状态提示 -->
      <div v-else class="empty-tips">
        <el-icon size="64"><Select /></el-icon>
        <p>请先选择项目开始专家推荐</p>
      </div>
    </div>

    <!-- 匹配详情弹窗 -->
    <MatchDetailDialog 
      v-model:visible="showDetailDialog" 
      :expert-id="selectedExpertId"
      :project-id="selectedProject?.id" 
      @invite-expert="handleInviteExpert" 
    />

    <!-- 邀请专家弹窗 -->
    <InvitationDialog 
      v-model:visible="showInviteDialog" 
      :expert="selectedExpert" 
      :project="selectedProject"
      @success="handleInviteSuccess" 
    />

    <!-- 批量邀请弹窗 -->
    <BatchInvitationDialog 
      v-model:visible="showBatchInviteDialog" 
      :experts="selectedExperts" 
      :project="selectedProject"
      @success="handleBatchInviteSuccess" 
      @update:experts="handleExpertsUpdate" 
    />

    <!-- 邀请历史记录 -->
    <InvitationHistory 
      v-if="selectedProject" 
      :project-id="selectedProject.id" 
      class="history-section" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh, Select } from '@element-plus/icons-vue'

// 组件导入
import ProjectSelector from '@/components/features/recommendations/ProjectSelector.vue'
import RecommendationList from '@/components/features/recommendations/RecommendationList.vue'
import MatchDetailDialog from '@/components/features/recommendations/MatchDetailDialog.vue'
import InvitationDialog from '@/components/features/recommendations/InvitationDialog.vue'
import BatchInvitationDialog from '@/components/features/recommendations/BatchInvitationDialog.vue'
import InvitationHistory from '@/components/features/recommendations/InvitationHistory.vue'

// 组合式函数导入
import { useRecommendationList } from '@/composables/useRecommendation'
import { useInvitationForm } from '@/composables/useRecommendationForm'

// 使用组合式函数
const {
  recommendations,
  loading,
  selectedExperts,
  loadRecommendations,
  refreshRecommendations,
  clearSelectedExperts
} = useRecommendationList()

const {
  sendInvitation,
  sendBatchInvitation,
  loadInvitationStats
} = useInvitationForm()

// 项目选择
const selectedProject = ref(null)

// 弹窗控制
const showDetailDialog = ref(false)
const showInviteDialog = ref(false)
const showBatchInviteDialog = ref(false)

// 选中的专家
const selectedExpert = ref(null)
const selectedExpertId = ref(null)

// 方法定义
const handleProjectChange = async (project) => {
  console.log('项目变更:', project)
  selectedProject.value = project
  if (project && project.id) {
    await loadRecommendations(project.id)
  }
}

const handleFiltersChange = (filters) => {
  console.log('筛选条件变化:', filters)
  // 根据筛选条件重新加载推荐专家
  if (selectedProject.value && selectedProject.value.id) {
    refreshRecommendations(selectedProject.value.id, filters)
  }
}

const handleViewDetail = (expert) => {
  console.log('查看详情:', expert)
  selectedExpertId.value = expert.id
  showDetailDialog.value = true
}

const handleInviteExpert = (expert) => {
  console.log('邀请专家:', expert)
  selectedExpert.value = expert
  loadInvitationStats(expert)
  showInviteDialog.value = true
}

const handleBatchInvite = (experts) => {
  console.log('批量邀请:', experts)
  selectedExperts.value = experts
  showBatchInviteDialog.value = true
}

const handleExpertsUpdate = (experts) => {
  console.log('专家列表更新:', experts)
  selectedExperts.value = experts
}

const handleInviteSuccess = async () => {
  console.log('单个邀请成功')
  const success = await sendInvitation(selectedExpert.value, selectedProject.value)
  if (success) {
    showInviteDialog.value = false
    selectedExpert.value = null
    refreshRecommendations(selectedProject.value?.id)
  }
}

const handleBatchInviteSuccess = async () => {
  console.log('批量邀请成功')
  const success = await sendBatchInvitation(selectedExperts.value, selectedProject.value)
  if (success) {
    showBatchInviteDialog.value = false
    clearSelectedExperts()
    refreshRecommendations(selectedProject.value?.id)
  }
}

onMounted(() => {
  console.log('RecommendationsView 组件已挂载')
})
</script>

<style scoped>
.recommendations-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.main-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  min-height: 400px;
  margin-bottom: 20px;
}

.empty-tips {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-tips .el-icon {
  margin-bottom: 16px;
}

.history-section {
  margin-top: 20px;
}
</style>