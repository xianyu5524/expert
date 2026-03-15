<template>
  <el-card class="detail-card modern-card" shadow="hover">
    <template #header>
      <div class="card-header modern-header">
        <div class="header-title">
          <el-icon>
            <Money />
          </el-icon>
          <span>财务信息</span>
        </div>
      </div>
    </template>
    
    <div class="finance-info">
      <div class="info-item">
        <span class="label">银行卡号：</span>
        <span class="value">{{ expert.bankAccount || '未填写' }}</span>
      </div>
      <div class="info-item">
        <span class="label">开户行：</span>
        <span class="value">{{ expert.bankName || '未填写' }}</span>
      </div>
      <div class="info-item" v-if="expert.bankBranch">
        <span class="label">开户支行：</span>
        <span class="value">{{ expert.bankBranch }}</span>
      </div>
      <div class="info-item" v-if="expert.accountName">
        <span class="label">账户名：</span>
        <span class="value">{{ expert.accountName }}</span>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { Money } from '@element-plus/icons-vue'

defineProps({
  expert: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.finance-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
}

.label {
  width: 100px;
  color: #606266;
  font-weight: 500;
  flex-shrink: 0;
}

.value {
  flex: 1;
  color: #303133;
}
</style>