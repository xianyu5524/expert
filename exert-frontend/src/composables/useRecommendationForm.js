import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { inviteExpertAPI, batchInviteExpertsAPI } from '@/api/recommendation'

// 邀请表单相关逻辑
export function useInvitationForm() {
  const sending = ref(false)
  
  // 单个邀请表单
  const inviteForm = reactive({
    notes: ''
  })
  
  // 批量邀请表单
  const batchInviteForm = reactive({
    notes: '',
    settings: {
      sendMethod: 'immediate',
      scheduledTime: null,
      sendNotification: true
    }
  })
  
  // 邀请统计（模拟数据）
  const invitationStats = reactive({
    totalCount: 0,
    acceptRate: 0,
    lastInviteTime: null
  })
  
  // 发送单个邀请
  const sendInvitation = async (expert, project) => {
    if (!expert || !project) return false
    
    sending.value = true
    try {
      await inviteExpertAPI({
        expertId: expert.id,
        projectId: project.id,
        notes: inviteForm.notes
      })
      ElMessage.success('邀请发送成功')
      resetInviteForm()
      return true
    } catch (error) {
      ElMessage.error('邀请发送失败')
      console.error('邀请发送失败:', error)
      return false
    } finally {
      sending.value = false
    }
  }
  
  // 发送批量邀请
  const sendBatchInvitation = async (experts, project) => {
    if (!experts || experts.length === 0 || !project) return false
    
    try {
      await ElMessageBox.confirm(
        `确定要批量邀请 ${experts.length} 位专家吗？`,
        '批量邀请确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      sending.value = true
      await batchInviteExpertsAPI({
        expertIds: experts.map(expert => expert.id),
        projectId: project.id,
        notes: batchInviteForm.notes,
        settings: batchInviteForm.settings
      })
      ElMessage.success(`已成功发送 ${experts.length} 个邀请`)
      resetBatchInviteForm()
      return true
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('批量邀请发送失败')
        console.error('批量邀请发送失败:', error)
      }
      return false
    } finally {
      sending.value = false
    }
  }
  
  // 重置表单
  const resetInviteForm = () => {
    inviteForm.notes = ''
  }
  
  const resetBatchInviteForm = () => {
    batchInviteForm.notes = ''
    batchInviteForm.settings = {
      sendMethod: 'immediate',
      scheduledTime: null,
      sendNotification: true
    }
  }
  
  // 加载邀请统计（模拟）
  const loadInvitationStats = (expert) => {
    if (expert) {
      invitationStats.totalCount = Math.floor(Math.random() * 10)
      invitationStats.acceptRate = Math.random() * 0.8 + 0.2
      invitationStats.lastInviteTime = '2024-01-15'
    }
  }
  
  return {
    sending,
    inviteForm,
    batchInviteForm,
    invitationStats,
    sendInvitation,
    sendBatchInvitation,
    resetInviteForm,
    resetBatchInviteForm,
    loadInvitationStats
  }
}