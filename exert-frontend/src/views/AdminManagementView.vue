<template>
  <div>
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-button 
          v-if="showAdminManagement" 
          type="primary" 
          @click="handleAddAdmin"
        >
          添加管理员
        </el-button>
        <el-button 
          v-if="showExpertManagement" 
          type="primary" 
          @click="handleAddExpert"
        >
          添加专家
        </el-button>
      </div>
    </div>

    <!-- 选项卡 -->
    <el-card v-if="isSuperAdmin">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane 
          v-if="showAdminManagement" 
          label="管理员管理" 
          name="admin"
        >
          <AdminManagementTab 
            ref="adminTabRef"
            @add-user="handleAddAdmin"
            @edit-user="handleEditUser"
            @delete-user="handleDeleteUser"
            @batch-delete="handleBatchDelete"
            @toggle-status="handleToggleStatus"
          />
        </el-tab-pane>
        
        <el-tab-pane 
          v-if="showExpertManagement" 
          label="专家管理" 
          name="expert"
        >
          <ExpertManagementTab 
            ref="expertTabRef"
            @add-user="handleAddExpert"
            @edit-user="handleEditUser"
            @delete-user="handleDeleteUser"
            @batch-delete="handleBatchDelete"
            @toggle-status="handleToggleStatus"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 管理员登录时只显示专家管理 -->
    <el-card v-else-if="isAdmin">
      <ExpertManagementTab 
        ref="expertTabRef"
        @add-user="handleAddExpert"
        @edit-user="handleEditUser"
        @delete-user="handleDeleteUser"
        @batch-delete="handleBatchDelete"
        @toggle-status="handleToggleStatus"
      />
    </el-card>

    <!-- 用户对话框 -->
    <UserDialog 
      v-model="userDialogVisible"
      :mode="dialogMode"
      :user-id="currentUserId"
      :default-role="defaultRole"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminManagementTab from '@/components/features/admin/AdminManagementTab.vue'
import ExpertManagementTab from '@/components/features/admin/ExpertManagementTab.vue'
import UserDialog from '@/components/features/admin/UserDialog.vue'
import { deleteUserAPI, toggleUserStatusAPI } from '@/api/user'

const userStore = useUserStore()

// 权限计算
const isSuperAdmin = computed(() => userStore.userRole === 'SUPER_ADMIN')
const isAdmin = computed(() => userStore.userRole === 'ADMIN' || isSuperAdmin.value)
const showAdminManagement = computed(() => isSuperAdmin.value)
const showExpertManagement = computed(() => isAdmin.value)

// 选项卡
const activeTab = ref(showAdminManagement.value ? 'admin' : 'expert')
const adminTabRef = ref(null)
const expertTabRef = ref(null)

// 对话框状态
const userDialogVisible = ref(false)
const dialogMode = ref('add')
const currentUserId = ref(null)
const defaultRole = ref('')

// 添加管理员
const handleAddAdmin = () => {
  dialogMode.value = 'add'
  defaultRole.value = 'ADMIN'
  currentUserId.value = null
  userDialogVisible.value = true
}

// 添加专家
const handleAddExpert = () => {
  dialogMode.value = 'add'
  defaultRole.value = 'EXPERT'
  currentUserId.value = null
  userDialogVisible.value = true
}

// 编辑用户
const handleEditUser = (user) => {
  dialogMode.value = 'edit'
  currentUserId.value = user.userId
  defaultRole.value = user.role
  userDialogVisible.value = true
}

// 删除用户
const handleDeleteUser = async (user) => {
  try {
    const isExpert = user.role === 'EXPERT'
    const confirmMessage = isExpert 
      ? `确定要删除专家 "${user.name}" 吗？此操作将同时删除专家相关数据。`
      : `确定要删除用户 "${user.name}" 吗？`

    await ElMessageBox.confirm(
      confirmMessage,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const response = await deleteUserAPI([user.userId])
    if (response.success) {
      ElMessage.success(isExpert ? '删除专家成功' : '删除用户成功')
      refreshCurrentTab()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败: ' + error.message)
    }
  }
}

// 批量删除
const handleBatchDelete = async (users) => {
  if (users.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }

  const names = users.map(user => user.name).join(', ')
  const hasExperts = users.some(user => user.role === 'EXPERT')
  const hasRegularUsers = users.some(user => user.role !== 'EXPERT')
  
  let confirmMessage = `确定要删除选中的 ${users.length} 个用户吗？`
  if (hasExperts && hasRegularUsers) {
    confirmMessage += '\n其中包含专家用户和普通用户，专家相关数据将被同时删除。'
  } else if (hasExperts) {
    confirmMessage += '\n其中包含专家用户，专家相关数据将被同时删除。'
  }
  confirmMessage += `\n${names}`

  try {
    await ElMessageBox.confirm(
      confirmMessage,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const ids = users.map(user => user.userId)
    const response = await deleteUserAPI(ids)
    if (response.success) {
      ElMessage.success('批量删除成功')
      refreshCurrentTab()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败: ' + error.message)
    }
  }
}

// 切换用户状态
const handleToggleStatus = async (user) => {
  const newStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  const action = newStatus === 'ACTIVE' ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${user.name}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const response = await toggleUserStatusAPI(user.userId, newStatus)
    if (response.success) {
      ElMessage.success(`${action}用户成功`)
      refreshCurrentTab()
    } else {
      ElMessage.error(response.message || `${action}用户失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}用户失败:`, error)
      ElMessage.error(`${action}用户失败`)
    }
  }
}

// 对话框成功回调
const handleDialogSuccess = () => {
  refreshCurrentTab()
}

// 选项卡切换
const handleTabChange = () => {
  // 选项卡切换时不需要特殊处理，组件会自动加载数据
}

// 刷新当前选项卡
const refreshCurrentTab = () => {
  const currentRef = getCurrentTabRef()
  if (currentRef && currentRef.refresh) {
    currentRef.refresh()
  }
}

// 获取当前选项卡的引用
const getCurrentTabRef = () => {
  if (activeTab.value === 'admin' && adminTabRef.value) {
    return adminTabRef.value
  } else if (activeTab.value === 'expert' && expertTabRef.value) {
    return expertTabRef.value
  }
  return null
}

onMounted(() => {
  // 页面加载时默认选项卡会自动加载数据
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
</style>