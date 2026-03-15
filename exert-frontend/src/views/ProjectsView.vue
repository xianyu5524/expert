<template>
  <div class="projects-container">
    <!-- 主页面 -->
    <ProjectList v-if="currentView === 'list'" @add-project="handleAdd" @edit-project="handleEdit"
      @delete-project="handleDelete" @batch-delete="handleBatchDelete" @invite-expert="handleInviteExpert" />

    <!-- 添加项目弹窗 -->
    <ProjectDialog v-model="showAddDialog" :mode="'add'" @success="handleProjectAdded" />

    <!-- 编辑项目弹窗 -->
    <ProjectDialog v-model="showEditDialog" :mode="'edit'" :project-id="currentProjectId"
      @success="handleProjectUpdated" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import ProjectList from '@/components/features/projects/ProjectList.vue'
import ProjectDialog from '@/components/features/projects/ProjectDialog.vue'
import { useProject } from '@/composables/useProject'

// 使用项目组合式函数
const {
  currentProject,
  fetchProjectDetail,
  handleSearch,
  deleteProject
} = useProject()

// 视图状态管理
const currentView = ref('list')
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const currentProjectId = ref(null)

// 添加项目
const handleAdd = () => {
  showAddDialog.value = true
}

// 编辑项目
const handleEdit = (project) => {
  currentProjectId.value = project.id
  showEditDialog.value = true
}

// 删除项目
const handleDelete = async (project) => {
  try {
    const success = await deleteProject([project.id])
    if (success) {
      handleSearch()
      ElMessage.success('删除项目成功')
    }
  } catch (error) {
    ElMessage.error('删除项目失败')
  }
}

// 批量删除项目
const handleBatchDelete = async (projects) => {
  try {
    const ids = projects.map(project => project.id)
    const success = await deleteProject(ids)
    if (success) {
      handleSearch()
      ElMessage.success('批量删除项目成功')
    }
  } catch (error) {
    ElMessage.error('批量删除项目失败')
  }
}

// 邀请专家
const handleInviteExpert = (project) => {
  // 跳转到推荐模块，传递项目信息
  console.log('邀请专家，项目信息:', project)
  ElMessage.info('邀请专家功能开发中...')
  // 后续开发：router.push(`/recommendations?projectId=${project.id}`)
}

// 项目添加成功回调
const handleProjectAdded = () => {
  ElMessage.success('项目添加成功')
  showAddDialog.value = false
  handleSearch()
}

// 项目更新成功回调
const handleProjectUpdated = () => {
  ElMessage.success('项目信息更新成功')
  showEditDialog.value = false
  currentProjectId.value = null
  handleSearch()
}
</script>

<style scoped>
.projects-container {
  padding: 20px;
  height: 100%;
  background-color: #f5f7fa;
}
</style>