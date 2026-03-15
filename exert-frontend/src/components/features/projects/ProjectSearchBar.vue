<template>
  <div class="search-section">
    <div class="search-controls">
      <el-form :model="searchForm" class="search-form">
        <div class="form-row">
          <div class="form-group">
            <el-input v-model="searchForm.name" placeholder="搜索项目名称" clearable class="search-input" />
          </div>
          <div class="form-group">
            <el-select v-model="searchForm.track" placeholder="项目赛道" clearable class="search-select">
              <el-option label="高教主赛道" value="MAIN_TRACK" />
              <el-option label="红旅赛道" value="RED_TOURISM_TRACK" />
              <el-option label="产业赛道" value="INDUSTRY_TRACK" />
            </el-select>
          </div>
          <div class="form-group">
            <el-select v-model="searchForm.status" placeholder="项目状态" clearable class="search-select">
              <el-option label="准备中" value="PREPARING" />
              <el-option label="进行中" value="ONGOING" />
              <el-option label="已暂停" value="SUSPENDED" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已终止" value="TERMINATED" />
            </el-select>
          </div>
          <div class="form-group">
            <el-select v-model="searchForm.stage" placeholder="项目阶段" clearable class="search-select">
              <el-option label="校赛" value="SCHOOL" />
              <el-option label="省赛" value="PROVINCIAL" />
              <el-option label="国赛" value="NATIONAL" />
            </el-select>
          </div>
          <div class="form-group">
            <el-select v-model="searchForm.projectLevel" placeholder="项目等级" clearable class="search-select">
              <el-option label="重点" value="KEY" />
              <el-option label="一般" value="GENERAL" />
              <el-option label="其他" value="OTHER" />
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