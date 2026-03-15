<template>
  <el-dialog v-model="dialogVisible" title="添加专家" width="900px" :before-close="handleClose">
    <div class="add-expert-container">
      <!-- 步骤指示器 -->
      <div class="step-indicator">
        <el-steps :active="currentStep" align-center>
          <el-step title="基本信息" />
          <el-step title="附加信息" />
          <el-step title="确认信息" />
        </el-steps>
      </div>

      <!-- 步骤1: 基本信息 -->
      <BasicInfoStep
        v-if="currentStep === 0"
        :form="form"
        :rules="rules"
        ref="basicFormRef"
      />

      <!-- 步骤2: 附加信息 -->
      <AdditionalInfoStep
        v-if="currentStep === 1"
        :form="form"
        @add-achievement="handleAddAchievement"
        @edit-achievement="handleEditAchievement"
        @remove-achievement="removeAchievement"
        @add-project="handleAddProject"
        @remove-project="handleRemoveProject"
      />

      <!-- 步骤3: 确认信息 -->
      <ConfirmationStep
        v-if="currentStep === 2"
        :form="form"
      />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handlePrevStep" :disabled="currentStep === 0">
          上一步
        </el-button>
        <el-button v-if="currentStep < 2" type="primary" @click="handleNextStep">
          下一步
        </el-button>
        <el-button v-else type="primary" @click="handleSave" :loading="loading">
          确认添加
        </el-button>
        <el-button @click="handleClose">取消</el-button>
      </div>
    </template>

    <!-- 子弹窗 -->
    <AchievementDialog
      v-model="achievementDialogVisible"
      :form="achievementForm"
      :index="currentAchievementIndex"
      @save="handleSaveAchievement"
    />

    <ProjectDialog
      v-model="projectDialogVisible"
      @save="handleSaveProject"
    />

    <AccountInfoDialog
      v-model="accountInfoDialog.visible"
      :username="accountInfoDialog.username"
      :name="accountInfoDialog.name"
    />
  </el-dialog>
</template>

<script setup>
import {computed,watch } from 'vue'
import { ElMessage } from 'element-plus'
import BasicInfoStep from './add/BasicInfoStep.vue'
import AdditionalInfoStep from './add/AdditionalInfoStep.vue'
import ConfirmationStep from './add/ConfirmationStep.vue'
import AchievementDialog from './add/AchievementDialog.vue'
import ProjectDialog from './add/ProjectDialog.vue'
import AccountInfoDialog from './detail/AccountInfoDialog.vue'
import { addExpertAPI } from "@/api/expert"
import { useExpertForm } from '@/composables/useExpertForm'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 使用重构后的 useExpertForm，不包含项目相关逻辑
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
  accountInfoDialog,
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

// 保存专家
const handleSave = async () => {
  try {
    loading.value = true
    const submitData = {
      expert: {
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
      achievements: form.achievements.filter(ach => ach.title && ach.achievementDate),
      expertProjects: form.expertProjects.map(project => ({
        projectId: project.isNewProject ? null : project.projectId,
        role: project.role,
        status: project.status,
        newProject: project.isNewProject ? {
          name: project.newProjectData.name,
          track: project.newProjectData.track,
          category: project.newProjectData.category,
          leaderName: project.newProjectData.leaderName,
          grade: project.newProjectData.grade,
          major: project.newProjectData.major,
          stage: project.newProjectData.stage,
          status: project.newProjectData.status,
          award: project.newProjectData.award,
          advisor: project.newProjectData.advisor,
          projectLevel: project.newProjectData.projectLevel,
          createdYear: project.newProjectData.createdYear
        } : null
      }))
    }

    const response = await addExpertAPI(submitData)

    if (response.success && response.code === 200) {
      const userData = response.data
      const username = userData.username || '未知'
      ElMessage.success(`添加专家成功！用户名：${username}，初始密码：123456`)
      showAccountInfo(username, form.name)
      emit('success')
      handleClose()
    } else {
      const errorMsg = response.message || response.msg || '添加专家失败'
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    console.error('添加专家失败:', error)
    const errorMsg = error.message || error.msg || '添加专家失败，请检查网络连接'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const showAccountInfo = (username, name) => {
  accountInfoDialog.username = username
  accountInfoDialog.name = name
  accountInfoDialog.visible = true
}

const handleClose = () => {
  dialogVisible.value = false
  resetForm()
}

// 监听对话框显示状态
watch(dialogVisible, (newVal) => {
  if (newVal) {
    resetForm()
  }
})
</script>

<style scoped>
.add-expert-container {
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

/* 滚动条样式 */
.add-expert-container::-webkit-scrollbar {
  width: 6px;
}

.add-expert-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.add-expert-container::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.add-expert-container::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>