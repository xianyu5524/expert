<template>
  <el-dialog
    :visible="visible"
    @update:visible="$emit('update:visible', $event)"
    title="批量邀请专家"
    width="700px"
    :before-close="handleClose"
  >
    <div>
      <!-- 项目信息 -->
      <div class="project-info">
        <h4>项目信息</h4>
        <el-descriptions :column="2" size="small">
          <el-descriptions-item label="项目名称">
            {{ project?.name }}
          </el-descriptions-item>
          <el-descriptions-item label="项目赛道">
            {{ project?.track }}
          </el-descriptions-item>
          <el-descriptions-item label="项目阶段">
            {{ project?.stage }}
          </el-descriptions-item>
          <el-descriptions-item label="项目等级">
            {{ project?.projectLevel }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 专家列表 -->
      <div class="expert-list">
        <h4>待邀请专家 ({{ experts.length }}人)</h4>
        <div class="expert-tags">
          <el-tag
            v-for="expert in experts"
            :key="expert.id"
            closable
            @close="removeExpert(expert)"
            class="expert-tag"
            type="info"
          >
            {{ expert.name }} ({{ (expert.matchScore * 100).toFixed(0) }}%)
          </el-tag>
        </div>
        
        <!-- 专家详情表格 -->
        <el-table
          :data="experts"
          size="small"
          max-height="200"
          class="expert-table"
        >
          <el-table-column prop="name" label="专家姓名" width="120" />
          <el-table-column prop="title" label="职称" width="120" />
          <el-table-column prop="unit" label="单位" show-overflow-tooltip />
          <el-table-column label="匹配度" width="100">
            <template #default="{ row }">
              <span class="match-score">{{ (row.matchScore * 100).toFixed(0) }}%</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ row }">
              <el-button type="danger" link @click="removeExpert(row)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 邀请备注 -->
      <div class="invitation-notes">
        <h4>邀请备注</h4>
        <el-input
          v-model="batchInviteForm.notes"
          type="textarea"
          :rows="4"
          placeholder="请输入邀请备注（将发送给所有专家）"
          maxlength="500"
          show-word-limit
        />
      </div>

      <!-- 发送设置 -->
      <div class="send-settings">
        <h4>发送设置</h4>
        <el-form :model="batchInviteForm.settings" label-width="120px">
          <el-form-item label="发送方式">
            <el-radio-group v-model="batchInviteForm.settings.sendMethod">
              <el-radio label="immediate">立即发送</el-radio>
              <el-radio label="scheduled">定时发送</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item 
            label="发送时间" 
            v-if="batchInviteForm.settings.sendMethod === 'scheduled'"
          >
            <el-date-picker
              v-model="batchInviteForm.settings.scheduledTime"
              type="datetime"
              placeholder="选择发送时间"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="发送通知">
            <el-checkbox v-model="batchInviteForm.settings.sendNotification">
              同时发送系统通知
            </el-checkbox>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button 
        type="primary" 
        :loading="sending" 
        :disabled="experts.length === 0"
        @click="handleSubmit"
      >
        发送批量邀请 ({{ experts.length }}人)
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
  experts: {
    type: Array,
    default: () => []
  },
  project: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'success', 'update:experts'])

// 使用组合式函数
const {
  sending,
  batchInviteForm,
  sendBatchInvitation,
  resetBatchInviteForm
} = useInvitationForm()

// 方法定义
const handleClose = () => {
  resetBatchInviteForm()
  emit('update:visible', false)
}

const removeExpert = (expert) => {
  // 通过emit通知父组件移除专家
  const updatedExperts = props.experts.filter(e => e.id !== expert.id)
  emit('update:experts', updatedExperts)
}

const handleSubmit = async () => {
  if (props.experts.length === 0) return

  const success = await sendBatchInvitation(props.experts, props.project)
  if (success) {
    emit('success')
    handleClose()
  }
}

// 监听visible变化，打开时重置表单
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetBatchInviteForm()
  }
})
</script>

<style scoped>
.project-info,
.expert-list,
.invitation-notes,
.send-settings {
  margin-bottom: 20px;
}

.project-info h4,
.expert-list h4,
.invitation-notes h4,
.send-settings h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-weight: 600;
}

.expert-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.expert-tag {
  margin-bottom: 8px;
}

.expert-table {
  margin-top: 12px;
}

.match-score {
  font-weight: 600;
  color: #67c23a;
}

.send-settings {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
}
</style>