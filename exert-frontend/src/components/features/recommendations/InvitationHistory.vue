<template>
  <el-card class="invitation-history">
    <template #header>
      <div class="card-header">
        <span>邀请历史记录</span>
        <div class="header-actions">
          <el-button type="primary" link @click="refreshHistory" :loading="loading">
            <el-icon>
              <Refresh />
            </el-icon>
            刷新
          </el-button>
          <el-button type="primary" link @click="exportHistory">
            <el-icon>
              <Download />
            </el-icon>
            导出
          </el-button>
        </div>
      </div>
    </template>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <el-form :model="filterForm" inline>
        <el-form-item label="状态筛选">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable>
            <el-option label="待响应" value="PENDING" />
            <el-option label="已接受" value="ACCEPTED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="filterForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 历史记录表格 -->
    <el-table :data="historyList" v-loading="loading" style="width: 100%" empty-text="暂无邀请记录"
      :empty-text="isEmptyData ? '暂无邀请记录' : '加载中...'" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="expertName" label="专家姓名" min-width="100" />
      <el-table-column prop="projectName" label="项目名称" min-width="150" show-overflow-tooltip />
      <!-- 移除邀请人列，因为后端返回为null -->
      <el-table-column prop="inviteTime" label="邀请时间" min-width="150">
        <template #default="{ row }">
          {{ formatDateTime(row.inviteTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)" size="small">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="responseTime" label="响应时间" min-width="150">
        <template #default="{ row }">
          {{ row.responseTime ? formatDateTime(row.responseTime) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="notes" label="邀请备注" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="viewInvitationDetail(row)">
            详情
          </el-button>
          <el-button type="primary" link v-if="row.status === 'PENDING'" @click="resendInvitation(row)">
            重新邀请
          </el-button>
          <el-button type="danger" link @click="cancelInvitation(row)" v-if="row.status === 'PENDING'">
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedRows.length > 0">
      <el-button type="primary" @click="batchResend" :disabled="!hasPendingSelected">
        批量重新邀请 ({{ selectedRows.length }})
      </el-button>
      <el-button type="danger" @click="batchCancel" :disabled="!hasPendingSelected">
        批量取消 ({{ selectedRows.length }})
      </el-button>
    </div>

    <!-- 分页 -->
    <div class="pagination-container" v-if="total > 0">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
        :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Download } from '@element-plus/icons-vue'

// API导入
import { getInvitationHistoryAPI } from '@/api/recommendation'

const props = defineProps({
  projectId: {
    type: Number,
    default: null
  }
})

// 响应式数据
const loading = ref(false)
const historyList = ref([])
const selectedRows = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 筛选表单
const filterForm = reactive({
  status: '',
  dateRange: []
})

// 计算属性
const hasPendingSelected = computed(() => {
  return selectedRows.value.some(row => row.status === 'PENDING')
})
//空状态判断
const isEmptyData = computed(() => {
  return !loading.value && historyList.value.length === 0
})

// 方法定义
const loadInvitationHistory = async () => {
  if (!props.projectId) return

  loading.value = true
  try {
    const params = {
      projectId: props.projectId,
      page: currentPage.value,
      pageSize: pageSize.value,
      status: filterForm.status || undefined
    }

    // 处理日期范围 - 注意后端使用的是 LocalDateTime
    if (filterForm.dateRange && filterForm.dateRange.length === 2) {
      // 将日期字符串转换为 LocalDateTime 格式
      params.startDate = filterForm.dateRange[0] + 'T00:00:00'
      params.endDate = filterForm.dateRange[1] + 'T23:59:59'
    }

    const response = await getInvitationHistoryAPI(params)
    const data = response.data

    // 调整数据结构映射
    historyList.value = data.list || []  // 后端返回的是 list，不是 records
    total.value = data.total || 0

    console.log('邀请历史数据:', historyList.value)
  } catch (error) {
    ElMessage.error('获取邀请历史失败')
    console.error('获取邀请历史失败:', error)
  } finally {
    loading.value = false
  }
}

const refreshHistory = () => {
  currentPage.value = 1
  loadInvitationHistory()
}

const exportHistory = () => {
  ElMessage.info('导出功能开发中...')
}

const handleFilter = () => {
  currentPage.value = 1
  loadInvitationHistory()
}

const resetFilter = () => {
  filterForm.status = ''
  filterForm.dateRange = []
  handleFilter()
}

// 方法定义 - 调整日期格式化
const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return ''
  try {
    // 处理 LocalDateTime 格式 "2025-10-10 14:38:07" 或 ISO 格式
    const date = new Date(dateTimeString.replace(' ', 'T'))
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return dateTimeString
  }
}

//用于纯日期显示
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const getStatusTagType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'ACCEPTED': 'success',
    'REJECTED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待响应',
    'ACCEPTED': '已接受',
    'REJECTED': '已拒绝'
  }
  return textMap[status] || '未知'
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const viewInvitationDetail = (invitation) => {
  ElMessageBox.alert(
    `邀请详情：<br><br>
    专家：${invitation.expertName || '未知'}<br>
    项目：${invitation.projectName || '未知'}<br>
    邀请时间：${formatDateTime(invitation.inviteTime)}<br>
    状态：${getStatusText(invitation.status)}<br>
    响应时间：${invitation.responseTime ? formatDateTime(invitation.responseTime) : '未响应'}<br>
    备注：${invitation.notes || '无'}`,
    '邀请详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

const resendInvitation = async (invitation) => {
  try {
    await ElMessageBox.confirm(
      `确定要重新邀请专家 ${invitation.expertName} 吗？`,
      '重新邀请确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    ElMessage.success('重新邀请发送成功')
    refreshHistory()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重新邀请发送失败')
    }
  }
}

const cancelInvitation = async (invitation) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消对专家 ${invitation.expertName} 的邀请吗？`,
      '取消邀请确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    ElMessage.success('邀请已取消')
    refreshHistory()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消邀请失败')
    }
  }
}

const batchResend = async () => {
  const pendingRows = selectedRows.value.filter(row => row.status === 'PENDING')
  if (pendingRows.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确定要批量重新邀请 ${pendingRows.length} 位专家吗？`,
      '批量重新邀请确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    ElMessage.success(`已重新邀请 ${pendingRows.length} 位专家`)
    refreshHistory()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量重新邀请失败')
    }
  }
}

const batchCancel = async () => {
  const pendingRows = selectedRows.value.filter(row => row.status === 'PENDING')
  if (pendingRows.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确定要批量取消 ${pendingRows.length} 个邀请吗？`,
      '批量取消确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    ElMessage.success(`已取消 ${pendingRows.length} 个邀请`)
    refreshHistory()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量取消失败')
    }
  }
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  loadInvitationHistory()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  loadInvitationHistory()
}

// 监听项目ID变化
watch(() => props.projectId, (newVal) => {
  if (newVal) {
    currentPage.value = 1
    loadInvitationHistory()
  } else {
    historyList.value = []
    total.value = 0
  }
})

onMounted(() => {
  if (props.projectId) {
    loadInvitationHistory()
  }
})
</script>

<style scoped>
.invitation-history {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.filter-section {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.batch-actions {
  margin: 16px 0;
  padding: 12px;
  background: #f0f9ff;
  border-radius: 4px;
  display: flex;
  gap: 12px;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>