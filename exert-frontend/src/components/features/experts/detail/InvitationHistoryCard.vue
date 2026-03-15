<template>
  <el-card class="detail-card modern-card" shadow="hover">
    <template #header>
      <div class="card-header modern-header">
        <div class="header-title">
          <el-icon>
            <Clock />
          </el-icon>
          <span>邀请历史记录 ({{ invitations?.length || 0 }})</span>
        </div>
      </div>
    </template>
    
    <el-table :data="invitations" size="small" stripe class="detail-table">
      <el-table-column type="index" label="序号" width="60"></el-table-column>
      <el-table-column prop="projectName" label="项目名称" min-width="180"></el-table-column>
      <el-table-column prop="inviteTime" label="邀请时间" width="150">
        <template #default="scope">
          {{ formatDateTime(scope.row.inviteTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="inviter" label="邀请人" width="120">
        <template #default="scope">
          {{ scope.row.inviter || '系统' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getInvitationStatusType(scope.row.status)" size="small">
            {{ formatInvitationStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="responseTime" label="响应时间" width="150">
        <template #default="scope">
          {{ scope.row.responseTime ? formatDateTime(scope.row.responseTime) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="notes" label="邀请备注" min-width="200" show-overflow-tooltip>
        <template #default="scope">
          {{ scope.row.notes || '-' }}
        </template>
      </el-table-column>
    </el-table>
    
    <div v-if="!invitations || invitations.length === 0" class="empty-data">
      <el-empty description="暂无邀请记录" :image-size="80" />
    </div>
  </el-card>
</template>

<script setup>
import { Clock } from '@element-plus/icons-vue'
import { formatDateTime, formatInvitationStatus, getInvitationStatusType } from '@/utils/formatters'

defineProps({
  invitations: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.empty-data {
  padding: 40px 0;
}
</style>