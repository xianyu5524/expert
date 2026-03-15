<template>
  <div>
    <!-- 搜索和操作卡片 -->
    <el-card>
      <template #header>
        <UserSearchBar 
          :search-form="searchForm" 
          :selected-count="selectedUsers.length"
          @search="handleSearch"
          @reset="handleReset"
          @batch-delete="handleBatchDelete"
        />
      </template>

      <!-- 用户列表表格 -->
      <UserTable 
        :data="userList" 
        :loading="loading" 
        :selected-users="selectedUsers"
        @selection-change="handleSelectionChange"
        @edit="$emit('edit-user', $event)"
        @delete="$emit('delete-user', $event)"
        @toggle-status="$emit('toggle-status', $event)"
      />

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination 
          v-model:current-page="pagination.currentPage" 
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]" 
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper" 
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange" 
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUser } from '@/composables/useUser'
import UserSearchBar from '@/components/features/admin/UserSearchBar.vue'
import UserTable from '@/components/features/admin/UserTable.vue'

// 定义事件
defineEmits(['edit-user', 'delete-user', 'batch-delete', 'toggle-status'])

// 使用用户组合式函数，只管理管理员角色
const {
  loading,
  userList,
  searchForm,
  pagination,
  handleSearch,
  handleReset,
  handleSizeChange,
  handleCurrentChange,
  fetchUserList
} = useUser('ADMIN')

const selectedUsers = ref([])

// 选择处理
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedUsers.value.length === 0) {
    return
  }
  // 触发父组件的批量删除事件
  defineEmits()['batch-delete'](selectedUsers.value)
}

// 暴露刷新方法
const refresh = () => {
  fetchUserList()
}

defineExpose({
  refresh,
  searchForm
})
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>