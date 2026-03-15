<template>
  <div class="expert-detail-page">
    <div class="detail-header">
      <div class="back-navigation">
        <el-button type="text" @click="$emit('back')" class="back-button">
          <el-icon>
            <ArrowLeft />
          </el-icon>
          返回专家列表
        </el-button>
      </div>
      <div class="detail-title">
        <h1>{{ expert.expert.name }} - 专家详情</h1>
        <p class="expert-subtitle">{{ expert.expert.unit }} · {{ expert.expert.department }} · {{ expert.expert.title }}</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleEditClick">编辑专家</el-button>
        <el-button @click="$emit('back')">返回列表</el-button>
      </div>
    </div>

    <div class="expert-detail-content">
      <el-row :gutter="20">
        <!-- 左侧信息卡片 -->
        <el-col :span="16">
          <!-- 基本信息卡片 -->
          <BasicInfoCard :expert="expert.expert" />

          <!-- 项目参与卡片 -->
          <ProjectParticipationCard :projects="expert.projects" />

          <!-- 邀请历史记录卡片 -->
          <InvitationHistoryCard :invitations="expert.invitations" />

          <!-- 成就荣誉卡片 -->
          <AchievementCard :achievements="expert.achievements" />
        </el-col>

        <!-- 右侧信息卡片 -->
        <el-col :span="8">
          <!-- 专家权重卡片 -->
          <WeightCard :expert="expert.expert" :projects="expert.projects" />

          <!-- 行业领域卡片 -->
          <FieldCard :field="expert.expert.field" />

          <!-- 财务信息卡片 -->
          <FinanceCard :expert="expert.expert" />

          <!-- 系统提醒卡片 -->
          <ReminderCard :reminders="expert.reminders" />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ArrowLeft } from '@element-plus/icons-vue'
import BasicInfoCard from './detail/BasicInfoCard.vue'
import ProjectParticipationCard from './detail/ProjectParticipationCard.vue'
import InvitationHistoryCard from './detail/InvitationHistoryCard.vue'
import AchievementCard from './detail/AchievementCard.vue'
import WeightCard from './detail/WeightCard.vue'
import FieldCard from './detail/FieldCard.vue'
import FinanceCard from './detail/FinanceCard.vue'
import ReminderCard from './detail/ReminderCard.vue'

const props=defineProps({
  expert: {
    type: Object,
    required: true
  }
})

const emit=defineEmits(['back', 'edit-expert'])

const handleEditClick = () => {
  if (!props.expert?.expert?.id) {
    console.error('专家数据不完整，无法编辑')
    ElMessage.error('专家数据不完整')
    return
  }
  
  // 触发 edit-expert 事件
  emit('edit-expert', props.expert.expert)
}
</script>

<style scoped>
.expert-detail-page {
  padding: 0;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 16px 0;
  border-bottom: 1px solid #e8e8e8;
}

.back-navigation {
  flex: 0 0 auto;
}

.back-button {
  display: flex;
  align-items: center;
  padding: 0;
  font-size: 14px;
}

.detail-title {
  flex: 1;
  text-align: center;
  margin: 0 20px;
}

.detail-title h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.expert-subtitle {
  margin: 5px 0 0;
  color: #909399;
  font-size: 14px;
}

.expert-detail-content {
  max-height: calc(100vh - 180px);
  overflow-y: auto;
  padding-right: 10px;
}

/* 滚动条样式 */
.expert-detail-content::-webkit-scrollbar {
  width: 6px;
}

.expert-detail-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.expert-detail-content::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.expert-detail-content::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>