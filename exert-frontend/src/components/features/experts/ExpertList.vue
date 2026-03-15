<template>
  <div>
    <div class="page-header">
      <h2>专家管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="$emit('add-expert')">添加专家</el-button>
        <el-button @click="showImportDialog">批量导入</el-button>
        <el-button @click="handleExport">导出数据</el-button>
      </div>
    </div>

    <!-- 搜索和操作卡片 -->
    <el-card>
      <template #header>
        <SearchBar :search-form="searchForm" :selected-count="selectedExperts.length" @search="handleSearch"
          @reset="handleReset" @batch-delete="handleBatchDelete" />
      </template>

      <!-- 批量导入对话框 -->
      <BatchOperationDialog v-model="importDialogVisible" type="expert" title="批量导入专家" @success="handleImportSuccess" />

      <!-- 专家列表表格 -->
      <ExpertTable :data="expertList" :loading="loading" :selected-experts="selectedExperts"
        @selection-change="handleSelectionChange" @view="$emit('view-expert', $event)"
        @edit="$emit('edit-expert', $event)" @delete="handleDelete" />

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BatchOperationDialog from '@/components/common/BatchOperationDialog.vue'
import SearchBar from './SearchBar.vue'
import ExpertTable from './ExpertTable.vue'
import { useExpert } from '@/composables/useExpert'
import batchService from '@/composables/batchService'

// 定义事件
const emit = defineEmits(['add-expert', 'view-expert', 'edit-expert', 'delete-expert', 'batch-delete'])

// 使用专家组合式函数
const {
  loading,
  expertList,
  searchForm,
  pagination,
  handleSearch: expertHandleSearch,
  handleReset: expertHandleReset,
  handleSizeChange: expertHandleSizeChange,
  handleCurrentChange: expertHandleCurrentChange
} = useExpert()

const selectedExperts = ref([])

// 搜索和重置 - 直接使用 composable 的方法
const handleSearch = async () => {
  await expertHandleSearch()
}

const handleReset = () => {
  expertHandleReset()
}

// 分页处理 - 直接使用 composable 的方法
const handleSizeChange = (size) => {
  expertHandleSizeChange(size)
}

const handleCurrentChange = (page) => {
  expertHandleCurrentChange(page)
}

// 选择处理
const handleSelectionChange = (selection) => {
  selectedExperts.value = selection
}

// 删除功能
const handleDelete = (expert) => {
  ElMessageBox.confirm(
    `确定要删除专家 "${expert.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    // 只触发事件，由父组件处理实际删除逻辑
    emit('delete-expert', expert)
  }).catch(() => {
    ElMessage.info('取消删除')
  })
}

const handleBatchDelete = () => {
  if (selectedExperts.value.length === 0) {
    ElMessage.warning('请选择要删除的专家')
    return
  }

  const names = selectedExperts.value.map(expert => expert.name).join(', ')
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedExperts.value.length} 位专家吗？\n${names}`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    // 只触发事件，由父组件处理实际删除逻辑
    emit('batch-delete', selectedExperts.value)
    selectedExperts.value = []
  }).catch(() => {
    ElMessage.info('取消删除')
  })
}


const importDialogVisible = ref(false)
// 显示导入对话框
const showImportDialog = () => {
  importDialogVisible.value = true
}

// 导出数据
const handleExport = async () => {
  const params = { 
    name: searchForm.name,
    expStatus: searchForm.status, // 专家状态
    expertType: searchForm.expertType
   }
  await batchService.exportData('experts', params, '专家数据')
}

// 导入成功回调
const handleImportSuccess = () => {
  // 刷新列表
  handleSearch()
}

// 暴露刷新方法，供父组件调用
defineExpose({
  refresh: handleSearch
})

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>