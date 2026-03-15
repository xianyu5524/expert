<template>
  <el-card class="detail-card modern-card" shadow="hover">
    <template #header>
      <div class="card-header modern-header">
        <div class="header-title">
          <el-icon>
            <Collection />
          </el-icon>
          <span>行业领域</span>
        </div>
      </div>
    </template>
    
    <div class="field-tags">
      <el-tag 
        v-for="(field, index) in getFieldArray(field)" 
        :key="index"
        class="field-tag" 
        type="primary" 
        effect="light" 
        size="large"
      >
        {{ field }}
      </el-tag>
    </div>
    
    <div v-if="!field" class="empty-tips">
      <span class="tips-text">暂无领域信息</span>
    </div>
  </el-card>
</template>

<script setup>
import { Collection } from '@element-plus/icons-vue'
import { getFieldArray } from '@/utils/formatters'

defineProps({
  field: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.field-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.field-tag {
  margin: 2px;
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