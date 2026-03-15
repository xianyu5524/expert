<template>
  <div class="expert-card">
    <div class="card-content">
      <!-- 选择框 -->
      <div class="selection-column">
        <el-checkbox 
          v-model="isSelected" 
          @change="handleSelectionChange"
        />
      </div>

      <!-- 专家基本信息 -->
      <div class="info-column">
        <div class="expert-basic">
          <div class="expert-name">
            <span class="name">{{ recommendation.expert.name }}</span>
            <el-tag 
              size="small" 
              :type="getExpertTypeTag(recommendation.expert.expertType)"
            >
              {{ getExpertTypeText(recommendation.expert.expertType) }}
            </el-tag>
          </div>
          <div class="expert-title">
            {{ recommendation.expert.title }} · {{ recommendation.expert.unit }}
          </div>
          <div class="expert-field">
            <el-tag
              v-for="field in getFieldArray(recommendation.expert.field)"
              :key="field"
              size="small"
              effect="plain"
            >
              {{ field }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 匹配度信息 -->
      <div class="match-column">
        <div class="match-score">
          <div class="score-circle">
            <span class="score-text">
              {{ (recommendation.matchScore * 100).toFixed(0) }}%
            </span>
          </div>
          <div class="score-label">匹配度</div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-column">
        <el-button 
          type="primary" 
          link 
          @click="$emit('view-detail', recommendation.expert)"
        >
          匹配详情
        </el-button>
        <el-button 
          type="primary" 
          @click="$emit('invite-expert', recommendation.expert)"
        >
          邀请
        </el-button>
      </div>
    </div>

    <!-- 简要匹配理由 -->
    <div class="match-reason" v-if="recommendation.matchReason">
      {{ recommendation.matchReason }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  recommendation: {
    type: Object,
    required: true
  },
  // 新增：是否选中
  selected: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['view-detail', 'invite-expert', 'select-change'])

const isSelected = computed({
  get: () => props.selected,
  set: (value) => {
    emit('select-change', props.recommendation.expert, value)
  }
})

// 方法定义
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

const getFieldArray = (fieldStr) => {
  if (!fieldStr) return []
  return fieldStr.split(',').slice(0, 3) // 最多显示3个领域
}

const handleSelectionChange = (selected) => {
  isSelected.value = selected
  emit('select-change', props.recommendation.expert, selected)
}
</script>

<style scoped>
.expert-card {
  padding: 16px 20px;
  transition: background-color 0.2s;
}

.expert-card:hover {
  background-color: #f8f9fa;
}

.card-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.selection-column {
  width: 32px;
  display: flex;
  justify-content: center;
}

.info-column {
  flex: 1;
  min-width: 0;
}

.match-column {
  width: 100px;
  display: flex;
  justify-content: center;
}

.action-column {
  width: 140px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.expert-basic {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.expert-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.expert-title {
  font-size: 14px;
  color: #606266;
}

.expert-field {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.match-score {
  text-align: center;
}

.score-circle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #67c23a, #85ce61);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 8px;
}

.score-text {
  color: white;
  font-size: 14px;
  font-weight: bold;
}

.score-label {
  font-size: 12px;
  color: #909399;
}

.match-reason {
  margin-top: 12px;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
  border-left: 3px solid #409eff;
}
</style>