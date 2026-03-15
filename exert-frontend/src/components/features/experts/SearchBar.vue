<template>
  <div class="search-section">
    <div class="search-controls">
      <el-form :model="searchForm" class="search-form">
        <div class="form-row">
          <div class="form-group">
            <el-input v-model="searchForm.name" placeholder="搜索专家姓名" clearable class="search-input" />
          </div>
          <div class="form-group">
            <el-select v-model="searchForm.status" placeholder="全部状态" clearable class="search-select">
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
            </el-select>
          </div>
          <div class="form-group">
            <el-select v-model="searchForm.expertType" placeholder="专家类型" clearable class="search-select">
              <el-option label="教育专家" value="EDUCATION" />
              <el-option label="企业专家" value="ENTERPRISE" />
            </el-select>
          </div>
          <div class="form-group">
            <el-select v-model="searchForm.weight" placeholder="专家权重" clearable class="search-select">
              <el-option v-for="i in 5" :key="i" :label="`${i}级`" :value="i" />
            </el-select>
          </div>
          <div class="form-group button-group">
            <el-button type="primary" @click="$emit('search')" class="search-button">搜索</el-button>
            <el-button @click="$emit('reset')" class="reset-button">重置</el-button>
          </div>
        </div>
      </el-form>
      <div class="batch-delete-container">
        <el-button 
          type="danger" 
          @click="$emit('batch-delete')" 
          :disabled="selectedCount === 0"
          class="batch-delete-button"
        >
          批量删除
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  searchForm: {
    type: Object,
    required: true
  },
  selectedCount: {
    type: Number,
    default: 0
  }
})

defineEmits(['search', 'reset', 'batch-delete'])
</script>

<style scoped>
.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
}

.search-section {
  width: 100%;
}

.search-controls {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.search-form {
  flex: 1;
  min-width: 0;
}

.form-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: flex-start;
}

.form-group {
  flex: 1;
  min-width: 150px;
}

.button-group {
  display: flex;
  gap: 8px;
  flex: 0 0 auto;
}

.search-input,
.search-select {
  width: 100%;
}

.batch-delete-container {
  flex: 0 0 auto;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
  }

  .form-group {
    width: 100%;
    min-width: auto;
  }

  .button-group {
    width: 100%;
    justify-content: flex-start;
  }

  .search-button,
  .reset-button {
    flex: 1;
  }
}
</style>