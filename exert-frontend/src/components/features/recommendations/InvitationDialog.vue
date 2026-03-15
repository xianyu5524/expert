<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    :title="`邀请专家 - ${expert?.name}`"
    width="500px"
    :before-close="handleClose"
  >
    <el-form 
      ref="formRef"
      :model="inviteForm"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="专家姓名">
        <el-input :value="expert?.name" disabled />
      </el-form-item>
      <el-form-item label="项目名称">
        <el-input :value="project?.name" disabled />
      </el-form-item>
      <el-form-item label="邀请备注" prop="notes">
        <el-input
          v-model="inviteForm.notes"
          type="textarea"
          :rows="4"
          placeholder="请输入邀请备注，说明项目背景、合作方式、期望等（可选）"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
      
      <!-- 邀请统计信息 -->
      <el-form-item label="邀请统计">
        <div class="invitation-stats">
          <div class="stat-item">
            <span class="stat-label">历史邀请:</span>
            <span class="stat-value">{{ invitationStats.totalCount }} 次</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">接受率:</span>
            <span class="stat-value">{{ (invitationStats.acceptRate * 100).toFixed(1) }}%</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">最近邀请:</span>
            <span class="stat-value">{{ invitationStats.lastInviteTime || '无记录' }}</span>
          </div>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button 
        type="primary" 
        :loading="sending" 
        @click="handleSubmit"
      >
        发送邀请
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useInvitationForm } from '@/composables/useRecommendationForm'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  expert: {
    type: Object,
    default: null
  },
  project: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'success'])

// 表单引用
const formRef = ref()

// 使用组合式函数
const {
  sending,
  inviteForm,
  invitationStats,
  sendInvitation,
  resetInviteForm,
  loadInvitationStats
} = useInvitationForm()

// 表单验证规则
const rules = {
  notes: [
    { max: 500, message: '备注长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 方法定义
const handleClose = () => {
  formRef.value?.resetFields()
  resetInviteForm()
  emit('update:visible', false)
}

const handleSubmit = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const success = await sendInvitation(props.expert, props.project)
  if (success) {
    emit('success')
    handleClose()
  }
}

// 监听visible变化，打开时加载统计
watch(() => props.visible, (newVal) => {
  if (newVal && props.expert) {
    loadInvitationStats(props.expert)
  }
})
</script>

<style scoped>
.invitation-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 4px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.stat-label {
  color: #606266;
}

.stat-value {
  color: #303133;
  font-weight: 500;
}
</style>