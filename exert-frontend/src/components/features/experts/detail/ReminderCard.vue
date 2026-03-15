<template>
  <el-card class="detail-card modern-card" shadow="hover">
    <template #header>
      <div class="card-header modern-header">
        <div class="header-title">
          <el-icon>
            <Bell />
          </el-icon>
          <span>系统提醒</span>
        </div>
      </div>
    </template>
    
    <div class="reminders-list">
      <div v-for="reminder in reminders" :key="reminder.id" class="reminder-item">
        <div class="reminder-icon" :class="reminder.type === 'BIRTHDAY' ? 'birthday' : 'deadline'">
          <el-icon v-if="reminder.type === 'BIRTHDAY'">
            <User />
          </el-icon>
          <el-icon v-else>
            <AlarmClock />
          </el-icon>
        </div>
        <div class="reminder-content">
          <div class="reminder-title">
            {{ reminder.type === 'BIRTHDAY' ? '生日提醒' : '项目截止提醒' }}
          </div>
          <div class="reminder-date">
            {{ formatDate(reminder.reminderDate) }}
          </div>
          <div class="reminder-desc" v-if="reminder.description">
            {{ reminder.description }}
          </div>
        </div>
        <el-tag :type="reminder.status === 'PENDING' ? 'warning' : 'success'" size="small">
          {{ reminder.status === 'PENDING' ? '待发送' : '已发送' }}
        </el-tag>
      </div>
    </div>
    
    <div v-if="!reminders || reminders.length === 0" class="empty-tips">
      <span class="tips-text">暂无提醒</span>
    </div>
  </el-card>
</template>

<script setup>
import { Bell, User, AlarmClock } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/formatters'

defineProps({
  reminders: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.reminders-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reminder-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.reminder-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  flex-shrink: 0;
}

.reminder-icon.birthday {
  background: #f0f9ff;
  color: #409EFF;
}

.reminder-icon.deadline {
  background: #fdf6ec;
  color: #E6A23C;
}

.reminder-content {
  flex: 1;
}

.reminder-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.reminder-date {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.reminder-desc {
  font-size: 12px;
  color: #606266;
}

.empty-tips {
  text-align: center;
  padding: 20px;
  color: #909399;
}

.tips-text {
  font-size: 14px;
}
</style>