<template>
  <el-card class="detail-card modern-card" shadow="hover">
    <template #header>
      <div class="card-header modern-header">
        <div class="header-title">
          <el-icon>
            <User />
          </el-icon>
          <span>基本信息</span>
        </div>
      </div>
    </template>
    <div class="basic-info">
      <div class="info-item">
        <span class="label">姓名：</span>
        <span class="value">{{ expert.name }}</span>
      </div>
      <div class="info-item">
        <span class="label">性别：</span>
        <span class="value">{{ expert.gender === 'MALE' ? '男' : '女' }}</span>
      </div>
      <div class="info-item">
        <span class="label">出生日期：</span>
        <span class="value">{{ formatDate(expert.birthDate) }}</span>
      </div>
      <div class="info-item">
        <span class="label">联系方式：</span>
        <span class="value">{{ expert.phone }}</span>
      </div>
      <div class="info-item">
        <span class="label">邮箱：</span>
        <span class="value">{{ expert.email }}</span>
      </div>
      <div class="info-item">
        <span class="label">个人简介：</span>
        <span class="value">{{ expert.introduction }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { User } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/formatters'

defineProps({
  expert: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.basic-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.basic-info .info-item {
  display: flex;
  margin-bottom: 12px;
}

.basic-info .label {
  width: 80px;
  color: #606266;
  font-weight: 500;
  flex-shrink: 0;
}

.basic-info .value {
  flex: 1;
  color: #303133;
}

.detail-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.modern-card {
  border: none;
  transition: all 0.3s ease;
}

.modern-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.modern-header {
  background: linear-gradient(90deg, #f8f9fa, #ffffff);
  border-radius: 8px 8px 0 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}
</style>