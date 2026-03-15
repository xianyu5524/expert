<template>
    <div>
        <div class="page-header">
            <h2>项目管理</h2>
            <div class="header-actions">
                <el-button type="primary" @click="$emit('add-project')">添加项目</el-button>
                <el-button @click="showImportDialog">批量导入</el-button>
                <el-button @click="handleExport">导出数据</el-button>
            </div>
        </div>

        <!-- 搜索和操作卡片 -->
        <el-card>
            <template #header>
                <ProjectSearchBar :search-form="searchForm" :selected-count="selectedProjects.length"
                    @search="handleSearch" @reset="handleReset" @batch-delete="handleBatchDelete" />
            </template>

            <!-- 项目列表表格 -->
            <ProjectTable :data="projectList" :loading="loading" :selected-projects="selectedProjects"
                @selection-change="handleSelectionChange" @edit="$emit('edit-project', $event)" @delete="handleDelete"
                @invite-expert="$emit('invite-expert', $event)" />

            <!-- 批量导入对话框 -->
            <BatchOperationDialog v-model="importDialogVisible" type="project" title="批量导入项目"
                @success="handleImportSuccess" />

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
                    :page-sizes="[10, 20, 50, 100]" :total="pagination.total"
                    layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProjectSearchBar from './ProjectSearchBar.vue'
import ProjectTable from './ProjectTable.vue'
import BatchOperationDialog from '@/components/common/BatchOperationDialog.vue'
import batchService from '@/composables/batchService'
import { useProject } from '@/composables/useProject'

// 定义事件
const emit = defineEmits(['add-project', 'edit-project', 'delete-project', 'batch-delete', 'invite-expert'])

// 使用项目组合式函数
const {
    loading,
    projectList,
    searchForm,
    pagination,
    handleSearch: projectHandleSearch,
    handleReset: projectHandleReset,
    handleSizeChange: projectHandleSizeChange,
    handleCurrentChange: projectHandleCurrentChange
} = useProject()

const selectedProjects = ref([])

// 搜索和重置
const handleSearch = async () => {
    await projectHandleSearch()
}

const handleReset = () => {
    projectHandleReset()
}

// 分页处理
const handleSizeChange = (size) => {
    projectHandleSizeChange(size)
}

const handleCurrentChange = (page) => {
    projectHandleCurrentChange(page)
}

// 选择处理
const handleSelectionChange = (selection) => {
    selectedProjects.value = selection
}

// 删除功能
const handleDelete = (project) => {
    ElMessageBox.confirm(
        `确定要删除项目 "${project.name}" 吗？`,
        '删除确认',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        emit('delete-project', project)
    }).catch(() => {
        ElMessage.info('取消删除')
    })
}

const handleBatchDelete = () => {
    if (selectedProjects.value.length === 0) {
        ElMessage.warning('请选择要删除的项目')
        return
    }

    const names = selectedProjects.value.map(project => project.name).join(', ')
    ElMessageBox.confirm(
        `确定要删除选中的 ${selectedProjects.value.length} 个项目吗？\n${names}`,
        '批量删除确认',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        emit('batch-delete', selectedProjects.value)
        selectedProjects.value = []
    }).catch(() => {
        ElMessage.info('取消删除')
    })
}

// 导入导出功能

const importDialogVisible = ref(false)

// 显示导入对话框
const showImportDialog = () => {
  importDialogVisible.value = true
}

// 导出数据
const handleExport = async () => {
  const params = { 
    name: searchForm.name,
    track: searchForm.track,
    proStatus: searchForm.status, // 项目状态
    stage: searchForm.stage,
    projectLevel: searchForm.projectLevel
   }
  await batchService.exportData('projects', params, '项目数据')
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