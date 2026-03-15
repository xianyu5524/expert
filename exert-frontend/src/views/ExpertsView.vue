<template>
  <div class="experts-container">
    <!-- 主页面 -->
    <ExpertList v-if="currentView === 'list'" @add-expert="handleAdd" @view-expert="handleView"
      @edit-expert="handleEdit" @delete-expert="handleDelete" @batch-delete="handleBatchDelete" />

    <!-- 专家详情子页面 -->
    <ExpertDetail v-else-if="currentView === 'detail'" :expert="currentExpert" @back="goBack"
      @edit-expert="handleEdit" />

    <!-- 添加专家弹窗 -->
    <AddExpertDialog v-model="showAddDialog" @success="handleExpertAdded" />

    <!-- 编辑专家弹窗 - 修改为传递ID -->
    <EditExpertDialog v-model="showEditDialog" :expert-id="currentExpertId" @success="handleExpertUpdated" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import ExpertList from '@/components/features/experts/ExpertList.vue'
import ExpertDetail from '@/components/features/experts/ExpertDetail.vue'
import AddExpertDialog from '@/components/features/experts/AddExpertDialog.vue'
import EditExpertDialog from '@/components/features/experts/EditExpertDialog.vue'
import { useExpert } from '@/composables/useExpert'

// 使用专家组合式函数
const {
  currentExpert,
  fetchExpertDetail,
  handleSearch,
  deleteExpert
} = useExpert()

// 视图状态管理
const currentView = ref('list')
const showAddDialog = ref(false)
const showEditDialog = ref(false)
// 新增：当前编辑的专家ID
const currentExpertId = ref(null)

// 返回列表
const goBack = () => {
  currentView.value = 'list'
  currentExpert.value = null
  currentExpertId.value = null // 重置ID
}

// 添加专家
const handleAdd = () => {
  showAddDialog.value = true
}

// 查看专家详情
const handleView = async (expert) => {
  try {
    await fetchExpertDetail(expert.id)
    currentView.value = 'detail'
  } catch (error) {
    ElMessage.error('获取专家详情失败')
  }
}

// 编辑专家 - 修改为只传递ID
const handleEdit = (expert) => {
  currentExpertId.value = expert.id
  showEditDialog.value = true
}

// 删除专家
const handleDelete = async (expert) => {
  try {
    const success = await deleteExpert([expert.id])
    if (success) {
      handleSearch()
    }
  } catch (error) {
    // 错误已经在 deleteExperts 中处理了
  }
}

// 批量删除专家
const handleBatchDelete = async (experts) => {
  try {
    const ids = experts.map(expert => expert.id)
    const success = await deleteExpert(ids)
    if (success) {
      // 刷新列表
      handleSearch()
    }
  } catch (error) {
    // 错误已经在 deleteExperts 中处理了
  }
}

// 专家添加成功回调
const handleExpertAdded = () => {
  ElMessage.success('专家添加成功')
  showAddDialog.value = false
  // 刷新列表
  handleSearch()
}

// 专家更新成功回调
const handleExpertUpdated = () => {
  ElMessage.success('专家信息更新成功')
  showEditDialog.value = false
  currentExpertId.value = null // 重置ID
  
  // 如果当前在详情页，重新加载详情数据
  if (currentView.value === 'detail' && currentExpert.value) {
    fetchExpertDetail(currentExpert.value.expert?.id || currentExpert.value.id)
  } else {
    // 刷新列表
    handleSearch()
  }
}
</script>

<style scoped>
.experts-container {
  padding: 20px;
  height: 100%;
  background-color: #f5f7fa;
}
</style>