<template>
  <div class="search-section">
    <div class="search-controls">
      <el-form :model="searchForm" class="search-form">
        <div class="form-row">
          <div class="form-group">
            <el-input v-model="searchForm.username" placeholder="搜索用户名" clearable class="search-input" />
          </div>
          <div class="form-group">
            <el-input v-model="searchForm.name" placeholder="搜索姓名" clearable class="search-input" />
          </div>
          <div class="form-group" v-if="!hideRoleFilter">
            <el-select v-model="searchForm.role" placeholder="用户角色" clearable class="search-select">
              <el-option label="超管" value="SUPER_ADMIN" />
              <el-option label="管理员" value="ADMIN" />
              <el-option label="专家" value="EXPERT" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </div>
          <div class="form-group">
            <el-select v-model="searchForm.status" placeholder="账号状态" clearable class="search-select">
              <el-option label="活跃" value="ACTIVE" />
              <el-option label="禁用" value="INACTIVE" />
              <el-option label="待激活" value="PENDING" />
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
  },
  hideRoleFilter: {
    type: Boolean,
    default: false
  }
})

defineEmits(['search', 'reset', 'batch-delete'])
</script>

<style scoped>
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