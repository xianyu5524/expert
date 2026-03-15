import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getRecommendationListAPI, 
  getMatchDetailAPI,
  inviteExpertAPI,
  batchInviteExpertsAPI 
} from '@/api/recommendation'

// 推荐列表相关逻辑
export function useRecommendationList() {
  const recommendations = ref([])
  const loading = ref(false)
  
  // 分页相关
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)
  
  // 选中的专家
  const selectedExperts = ref([])
  
  // 计算分页后的数据
  const paginatedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return recommendations.value.slice(start, end)
  })
  
  // 构建查询参数
  const buildQueryParams = (projectId, filters = {}) => {
    const params = {
      projectId: projectId,
      filters: filters
    }
    console.log('构建的查询参数:', params)
    return params
  }
  
  // 加载推荐列表
  const loadRecommendations = async (projectId, filters = {}) => {
    if (!projectId) {
      console.error('项目ID不能为空')
      return
    }
    
    loading.value = true
    try {
      const queryParams = buildQueryParams(projectId, filters)
      const response = await getRecommendationListAPI(queryParams)
      
      // 根据后端返回结构调整
      const data = response.data?.data || response.data || []
      recommendations.value = Array.isArray(data) ? data : []
      total.value = recommendations.value.length
      
      console.log('获取推荐专家列表成功:', recommendations.value.length)
    } catch (error) {
      ElMessage.error('获取推荐列表失败')
      console.error('获取推荐列表失败:', error)
      recommendations.value = []
      total.value = 0
    } finally {
      loading.value = false
    }
  }
  
  // 刷新推荐列表
  const refreshRecommendations = (projectId, filters = {}) => {
    currentPage.value = 1
    selectedExperts.value = []
    loadRecommendations(projectId, filters)
  }
  
  // 分页处理
  const handleSizeChange = (newSize) => {
    pageSize.value = newSize
    currentPage.value = 1
  }
  
  const handleCurrentChange = (newPage) => {
    currentPage.value = newPage
  }
  
  // 专家选择处理
  const handleExpertSelect = (expert, selected) => {
    if (selected) {
      selectedExperts.value.push(expert)
    } else {
      selectedExperts.value = selectedExperts.value.filter(e => e.id !== expert.id)
    }
  }
  
  const isExpertSelected = (expertId) => {
    return selectedExperts.value.some(expert => expert.id === expertId)
  }
  
  const clearSelectedExperts = () => {
    selectedExperts.value = []
  }
  
  return {
    recommendations,
    loading,
    currentPage,
    pageSize,
    total,
    selectedExperts,
    paginatedData,
    loadRecommendations,
    refreshRecommendations,
    handleSizeChange,
    handleCurrentChange,
    handleExpertSelect,
    isExpertSelected,
    clearSelectedExperts
  }
}

// 匹配详情相关逻辑
export function useMatchDetail() {
  const matchDetail = ref(null)
  const expertDetail = ref(null)
  const invitationHistory = ref([])
  const loading = ref(false)
  
  const loadMatchDetail = async (expertId, projectId) => {
    if (!expertId || !projectId) return
    
    loading.value = true
    try {
      const response = await getMatchDetailAPI({
        expertId,
        projectId
      })
      const data = response.data
      expertDetail.value = data.expert
      matchDetail.value = data.matchDetail
      invitationHistory.value = data.invitationHistory || []
    } catch (error) {
      ElMessage.error('获取匹配详情失败')
      console.error('获取匹配详情失败:', error)
    } finally {
      loading.value = false
    }
  }
  
  const resetMatchDetail = () => {
    matchDetail.value = null
    expertDetail.value = null
    invitationHistory.value = []
  }
  
  return {
    matchDetail,
    expertDetail,
    invitationHistory,
    loading,
    loadMatchDetail,
    resetMatchDetail
  }
}