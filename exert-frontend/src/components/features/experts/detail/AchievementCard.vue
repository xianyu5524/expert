<template>
  <el-card class="detail-card modern-card" shadow="hover">
    <template #header>
      <div class="card-header modern-header">
        <div class="header-title">
          <el-icon>
            <Trophy />
          </el-icon>
          <span>成就荣誉 ({{ achievements?.length || 0 }})</span>
        </div>
      </div>
    </template>
    
    <div class="achievements-list">
      <div v-for="achievement in achievements" :key="achievement.id" class="achievement-item">
        <div class="achievement-header">
          <span class="achievement-title">{{ achievement.title }}</span>
          <span class="achievement-date">{{ formatDate(achievement.achievementDate) }}</span>
        </div>
        <div class="achievement-description" v-if="achievement.description">
          {{ achievement.description }}
        </div>
      </div>
    </div>
    
    <div v-if="!achievements || achievements.length === 0" class="empty-data">
      <el-empty description="暂无成就记录" :image-size="80" />
    </div>
  </el-card>
</template>

<script setup>
import { Trophy } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/formatters'

defineProps({
  achievements: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.achievements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.achievement-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409EFF;
}

.achievement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.achievement-title {
  font-weight: 500;
  color: #303133;
}

.achievement-date {
  color: #909399;
  font-size: 12px;
}

.achievement-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.empty-data {
  padding: 40px 0;
}
</style>