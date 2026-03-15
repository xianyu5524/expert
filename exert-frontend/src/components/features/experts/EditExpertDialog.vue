<template>
  <el-dialog v-model="dialogVisible" title="编辑专家信息" width="900px" @close="handleClose" :close-on-click-modal="false">
    <div class="edit-expert-container">
      <!-- 数据加载状态 -->
      <div v-if="dataLoading" class="loading-state">
        <el-icon class="is-loading">
          <Loading />
        </el-icon>
        <span>正在加载专家数据...</span>
      </div>

      <div v-else>
        <!-- 步骤指示器 -->
        <div class="step-indicator">
          <el-steps :active="currentStep" align-center>
            <el-step title="基本信息" />
            <el-step title="附加信息" />
            <el-step title="确认信息" />
          </el-steps>
        </div>

        <!-- 步骤内容 -->
        <BasicInfoStep v-if="currentStep === 0" :form="form" :rules="rules" ref="basicFormRef" mode="edit" />

        <AdditionalInfoStep v-if="currentStep === 1" :form="form" mode="edit" @add-achievement="handleAddAchievement"
          @edit-achievement="handleEditAchievement" @remove-achievement="removeAchievement"
          @add-project="handleAddProject" @remove-project="handleRemoveProject" />

        <ConfirmationStep v-if="currentStep === 2" :form="form" mode="edit" />
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handlePrevStep" :disabled="currentStep === 0 || dataLoading || loading">
          上一步
        </el-button>
        <el-button v-if="currentStep < 2" type="primary" @click="handleNextStep" :disabled="dataLoading || loading">
          下一步
        </el-button>
        <el-button v-else type="primary" @click="handleSave" :loading="loading"
          :disabled="dataLoading || !props.expertId">
          确认修改
        </el-button>
        <el-button @click="handleClose" :disabled="loading">取消</el-button>
      </div>
    </template>

    <!-- 子弹窗 -->
    <AchievementDialog v-model="achievementDialogVisible" :form="achievementForm" :index="currentAchievementIndex"
      @save="handleSaveAchievement" />

    <ProjectDialog v-model="projectDialogVisible" mode="edit" @save="handleSaveProject" />
  </el-dialog>
</template>


<script setup>
import { computed, watch, nextTick, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import BasicInfoStep from './add/BasicInfoStep.vue'
import AdditionalInfoStep from './add/AdditionalInfoStep.vue'
import ConfirmationStep from './add/ConfirmationStep.vue'
import AchievementDialog from './add/AchievementDialog.vue'
import ProjectDialog from './add/ProjectDialog.vue'
import { updateExpertAPI, getExpertDetailAPI } from "@/api/expert" // 新增导入
import { useExpertForm } from '@/composables/useExpertForm'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  expertId: {  // 改为接收ID而不是完整对象
    type: [Number, String],
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 添加加载状态
const dataLoading = ref(false)
const currentExpertData = ref(null)

// 使用表单组合函数
const {
  form,
  rules,
  currentStep,
  loading,
  basicFormRef,
  achievementDialogVisible,
  projectDialogVisible,
  currentAchievementIndex,
  achievementForm,
  handleNextStep,
  handlePrevStep,
  handleAddAchievement,
  handleEditAchievement,
  removeAchievement,
  handleAddProject,
  handleRemoveProject,
  handleSaveAchievement,
  handleSaveProject,
  resetForm
} = useExpertForm()

// 获取专家详情
const fetchExpertDetail = async () => {
  if (!props.expertId) {
    console.warn('没有专家ID')
    return
  }

  try {
    dataLoading.value = true
    const response = await getExpertDetailAPI(props.expertId)
    currentExpertData.value = response.data

    // 填充表单数据
    fillEditForm(response.data)
  } catch (error) {
    console.error('获取专家详情失败:', error)
    ElMessage.error('获取专家详情失败')
  } finally {
    dataLoading.value = false
  }
}

// 填充编辑表单数据
const fillEditForm = (expertData) => {
  if (!expertData) {
    console.warn('没有专家数据可用于填充表单')
    return
  }

  try {
    // 处理不同的数据结构
    let expertDetail = expertData

    // 如果存在嵌套的 expert 对象，使用它
    if (expertData.expert) {
      expertDetail = expertData.expert
    }

    // 填充基本信息
    form.name = expertDetail.name || ''
    form.gender = expertDetail.gender || 'MALE'
    form.birthDate = expertDetail.birthDate || ''
    form.expertType = expertDetail.expertType || 'EDUCATION'
    form.weight = Number(expertDetail.weight) || 3
    form.status = expertDetail.status || 'PENDING'
    form.unit = expertDetail.unit || ''
    form.department = expertDetail.department || ''
    form.title = expertDetail.title || ''
    form.phone = expertDetail.phone || ''
    form.email = expertDetail.email || ''
    form.field = expertDetail.field || ''
    form.introduction = expertDetail.introduction || ''
    form.bankAccount = expertDetail.bankAccount || ''
    form.bankName = expertDetail.bankName || ''

    // 处理成就数据
    let achievementsData = []
    if (expertData.achievements && Array.isArray(expertData.achievements)) {
      achievementsData = expertData.achievements
    } else if (expertDetail.achievements && Array.isArray(expertDetail.achievements)) {
      achievementsData = expertDetail.achievements
    }

    form.achievements = achievementsData.map(ach => ({
      ...ach,
      achievementDate: ach.achievementDate || ''
    })).filter(ach => ach)

    // 处理项目数据
    let projectsData = []
    if (expertData.projects && Array.isArray(expertData.projects)) {
      projectsData = expertData.projects
    } else if (expertDetail.projects && Array.isArray(expertDetail.projects)) {
      projectsData = expertDetail.projects
    }

    form.expertProjects = projectsData.map(proj => {
      if (!proj) return null
      return {
        projectId: proj.projectId || proj.id,
        role: proj.role || '',
        status: proj.status || 'ONGOING',
        projectName: proj.projectName || proj.name,
        isNewProject: false
      }
    }).filter(proj => proj !== null)

  } catch (error) {
    console.error('填充表单数据时出错:', error)
    ElMessage.error('填充表单数据失败')
  }
}

// 保存编辑
const handleSave = async () => {
  try {
    loading.value = true

    const submitData = {
      expert: {
        id: props.expertId,
        name: form.name,
        gender: form.gender,
        birthDate: form.birthDate,
        title: form.title,
        unit: form.unit,
        department: form.department,
        field: form.field,
        expertType: form.expertType,
        weight: form.weight,
        phone: form.phone,
        email: form.email,
        introduction: form.introduction,
        status: form.status,
        bankAccount: form.bankAccount,
        bankName: form.bankName
      },
      achievements: form.achievements.filter(ach => ach && ach.title && ach.achievementDate),
      expertProjects: form.expertProjects.filter(proj => proj && proj.projectId && proj.role)
    }

    console.log('提交的数据:', submitData)

    await updateExpertAPI(submitData)
    emit('success')
    handleClose()
  } catch (error) {
    console.error('修改专家信息失败:', error)
    ElMessage.error('修改专家信息失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  dialogVisible.value = false
  resetForm()
  currentExpertData.value = null
}

// 监听对话框显示状态
watch(dialogVisible, (newVal) => {
  if (newVal && props.expertId) {
    currentStep.value = 0
    nextTick(() => {
      fetchExpertDetail()
    })
  }
})

// 监听专家ID变化
watch(() => props.expertId, (newId) => {
  if (newId && dialogVisible.value) {
    fetchExpertDetail()
  }
})
</script>

<style scoped>
.step-indicator {
  margin-bottom: 30px;
  padding: 0 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-overlay .el-icon {
  margin-right: 8px;
}

.no-data {
  padding: 40px 0;
  text-align: center;
}

/* 滚动条样式 */
.edit-expert-container::-webkit-scrollbar {
  width: 6px;
}

.edit-expert-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.edit-expert-container::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.edit-expert-container::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
}

.loading-state .el-icon {
  margin-right: 8px;
}

.edit-expert-container {
  max-height: 70vh;
  overflow-y: auto;
  padding: 0 10px;
}

.step-indicator {
  margin-bottom: 30px;
  padding: 0 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>