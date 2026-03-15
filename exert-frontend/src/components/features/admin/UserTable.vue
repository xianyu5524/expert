<template>
  <el-table 
    :data="data" 
    stripe 
    v-loading="loading"
    @selection-change="$emit('selection-change', $event)"
  >
    <el-table-column type="selection" width="55" />
    <el-table-column type="index" label="序号" width="60" />
    <el-table-column prop="username" label="用户名" min-width="120" />
    <el-table-column prop="name" label="姓名" min-width="100" />
    <el-table-column prop="email" label="邮箱" min-width="180" />
    <el-table-column prop="phone" label="手机号" width="120" />
    <el-table-column prop="role" label="角色" width="100">
      <template #default="scope">
        <el-tag :type="getUserRoleTagType(scope.row.role)" size="small">
          {{ formatUserRole(scope.row.role) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="status" label="状态" width="80">
      <template #default="scope">
        <el-tag :type="getUserStatusTagType(scope.row.status)" size="small">
          {{ formatUserStatus(scope.row.status) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="lastLoginTime" label="最后登录" width="160">
      <template #default="scope">
        <span v-if="scope.row.lastLoginTime">{{ formatDateTime(scope.row.lastLoginTime) }}</span>
        <span v-else>-</span>
      </template>
    </el-table-column>
    <el-table-column prop="createdTime" label="创建时间" width="160">
      <template #default="scope">
        {{ formatDateTime(scope.row.createdTime) }}
      </template>
    </el-table-column>
    <el-table-column label="操作" width="200" fixed="right">
      <template #default="scope">
        <div class="table-actions">
          <el-button size="small" type="primary" @click="$emit('edit', scope.row)">编辑</el-button>
          <el-button 
            size="small" 
            :type="scope.row.status === 'ACTIVE' ? 'warning' : 'success'" 
            @click="$emit('toggle-status', scope.row)"
          >
            {{ scope.row.status === 'ACTIVE' ? '禁用' : '启用' }}
          </el-button>
          <el-button 
            size="small" 
            type="danger" 
            @click="$emit('delete', scope.row)"
            :disabled="scope.row.role === 'SUPER_ADMIN'"
          >
            删除
          </el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import { 
  formatUserRole, 
  formatUserStatus, 
  getUserRoleTagType, 
  getUserStatusTagType 
} from '@/utils/formatters'
import { formatDateTime } from '@/utils/date'  // 使用独立的日期工具

defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['selection-change', 'edit', 'delete', 'toggle-status'])
</script>

<style scoped>
.table-actions {
  display: flex;
  gap: 8px;
}
</style>