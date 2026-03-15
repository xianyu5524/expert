<template>
  <el-card class="detail-card modern-card" shadow="hover">
    <template #header>
      <div class="card-header modern-header">
        <div class="header-title">
          <el-icon>
            <TrendCharts />
          </el-icon>
          <span>专家权重</span>
        </div>
      </div>
    </template>
    <div class="weight-display">
      <div class="weight-level">
        <div class="weight-number">{{ expert.weight }}</div>
        <div class="weight-label">权重等级</div>
      </div>
      <div class="weight-text">{{ getWeightText(expert.weight) }}</div>
      <div class="weight-stats">
        <div class="stat-item">
          <span class="stat-label">指导项目</span>
          <span class="stat-value">{{ expert.guidanceCount }} 次</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">参与项目</span>
          <span class="stat-value">{{ projects?.length || 0 }} 个</span>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { TrendCharts } from '@element-plus/icons-vue'
import { getWeightText } from '@/utils/formatters'

defineProps({
  expert: {
    type: Object,
    required: true
  },
  projects: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.weight-display {
  text-align: center;
  padding: 20px 0;
}

.weight-level {
  margin-bottom: 16px;
}

.weight-number {
  font-size: 48px;
  font-weight: bold;
  color: #409EFF;
  line-height: 1;
}

.weight-label {
  font-size: 16px;
  color: #909399;
  margin-top: 8px;
}

.weight-text {
  font-size: 16px;
  color: #409EFF;
  font-weight: 600;
  margin: 10px 0;
}

.weight-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}
</style>