<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    title="匹配度详情"
    width="800px"
    :before-close="handleClose"
  >
    <div v-loading="loading" class="match-detail-dialog">
      <!-- 专家基本信息 -->
      <div class="expert-info-section">
        <h4>专家信息</h4>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="姓名">
            {{ expertDetail?.name }}
          </el-descriptions-item>
          <el-descriptions-item label="职称">
            {{ expertDetail?.title }}
          </el-descriptions-item>
          <el-descriptions-item label="单位">
            {{ expertDetail?.unit }}
          </el-descriptions-item>
          <el-descriptions-item label="领域">
            {{ expertDetail?.field }}
          </el-descriptions-item>
          <el-descriptions-item label="专家类型">
            <el-tag :type="getExpertTypeTag(expertDetail?.expertType)">
              {{ getExpertTypeText(expertDetail?.expertType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="专家权重">
            <el-rate
              :model-value="expertDetail?.weight"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value} 级"
            />
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 匹配度分析 -->
      <div class="match-analysis-section">
        <h4>匹配度分析</h4>
        <div class="overall-score">
          <div class="score-display">
            <div class="score-circle">
              <span class="score-value">{{ (matchDetail?.overallScore * 100).toFixed(1) }}%</span>
              <span class="score-label">总体匹配度</span>
            </div>
          </div>
        </div>

        <div class="dimension-scores">
          <div 
            v-for="dimension in matchDetail?.dimensionScores" 
            :key="dimension.name"
            class="dimension-item"
          >
            <div class="dimension-header">
              <span class="dimension-name">{{ dimension.label }}</span>
              <span class="dimension-score">{{ (dimension.score * 100).toFixed(1) }}%</span>
            </div>
            <el-progress 
              :percentage="dimension.score * 100" 
              :show-text="false"
              :color="getProgressColor(dimension.score)"
            />
            <div class="dimension-reason">
              {{ dimension.reason }}
            </div>
          </div>
        </div>
      </div>

      <!-- 匹配理由 -->
      <div class="match-reason-section" v-if="matchDetail?.matchReason">
        <h4>匹配理由</h4>
        <div class="reason-content">
          {{ matchDetail.matchReason }}
        </div>
      </div>

      <!-- 历史邀请记录 -->
      <div class="invitation-history-section" v-if="invitationHistory?.length > 0">
        <h4>历史邀请记录</h4>
        <el-timeline>
          <el-timeline-item
            v-for="invitation in invitationHistory"
            :key="invitation.id"
            :timestamp="formatTime(invitation.inviteTime)"
            :type="getInvitationStatusType(invitation.status)"
          >
            <p>项目: {{ invitation.projectName }}</p>
            <p>状态: 
              <el-tag :type="getStatusTagType(invitation.status)" size="small">
                {{ getStatusText(invitation.status) }}
              </el-tag>
            </p>
            <p v-if="invitation.notes">备注: {{ invitation.notes }}</p>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" @click="handleInvite">邀请专家</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { watch } from 'vue'

// 组合式函数导入
import { useMatchDetail } from '@/composables/useRecommendation'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  expertId: {
    type: Number,
    default: null
  },
  projectId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'invite-expert'])

// 使用匹配详情组合式函数
const {
  matchDetail,
  expertDetail,
  invitationHistory,
  loading,
  loadMatchDetail,
  resetMatchDetail
} = useMatchDetail()

// 方法定义
const handleClose = () => {
  emit('update:visible', false)
}

const handleInvite = () => {
  emit('invite-expert', expertDetail.value)
  handleClose()
}

// 工具方法
const getExpertTypeTag = (type) => {
  const typeMap = {
    'EDUCATION': 'success',
    'ENTERPRISE': 'warning'
  }
  return typeMap[type] || 'info'
}

const getExpertTypeText = (type) => {
  const typeMap = {
    'EDUCATION': '教育专家',
    'ENTERPRISE': '企业专家'
  }
  return typeMap[type] || '其他'
}

const getProgressColor = (score) => {
  if (score >= 0.8) return '#67c23a'
  if (score >= 0.6) return '#e6a23c'
  return '#f56c6c'
}

const formatTime = (timeString) => {
  if (!timeString) return ''
  return new Date(timeString).toLocaleString()
}

const getInvitationStatusType = (status) => {
  const typeMap = {
    'ACCEPTED': 'success',
    'REJECTED': 'danger',
    'PENDING': 'primary'
  }
  return typeMap[status] || 'info'
}

const getStatusTagType = (status) => {
  const typeMap = {
    'ACCEPTED': 'success',
    'REJECTED': 'danger',
    'PENDING': 'warning'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'ACCEPTED': '已接受',
    'REJECTED': '已拒绝',
    'PENDING': '待响应'
  }
  return textMap[status] || '未知'
}

// 监听visible变化，打开时加载数据
watch(() => props.visible, (newVal) => {
  if (newVal) {
    loadMatchDetail(props.expertId, props.projectId)
  } else {
    resetMatchDetail()
  }
})
</script>

<style scoped>
.match-detail-dialog {
  max-height: 70vh;
  overflow-y: auto;
  padding: 0 10px;
}

.expert-info-section,
.match-analysis-section,
.match-reason-section,
.invitation-history-section {
  margin-bottom: 24px;
}

.expert-info-section h4,
.match-analysis-section h4,
.match-reason-section h4,
.invitation-history-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-weight: 600;
}

.overall-score {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.score-display {
  text-align: center;
}

.score-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #67c23a, #85ce61);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 auto 8px;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.score-value {
  color: white;
  font-size: 24px;
  font-weight: bold;
  line-height: 1.2;
}

.score-label {
  color: white;
  font-size: 12px;
  opacity: 0.9;
}

.dimension-item {
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.dimension-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.dimension-name {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.dimension-score {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.dimension-reason {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  line-height: 1.4;
}

.reason-content {
  padding: 12px;
  background: #f0f9ff;
  border-radius: 4px;
  border-left: 4px solid #409eff;
  font-size: 14px;
  line-height: 1.5;
  color: #606266;
}

.invitation-history-section {
  max-height: 200px;
  overflow-y: auto;
}
</style>